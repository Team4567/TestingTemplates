/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.command.Subsystem;
import frc.robot.Constants;
import frc.robot.commands.*;
import frc.robot.enums.*;
/**
 * Add your docs here.
 */
public class Elevator extends Subsystem {
  private MotorCalculator posCalc;
  private double diameter=0;
  private double circ= Math.PI*diameter;
  private double elevatorGearbox=184320;
  private double initHeightOffGround=0;
  public TalonSRX t1;
  private ElevatorPos pos;
  // Put methods for controlling this subsystem
  // here. Call these from Commands.
  public Elevator(){
    t1=new TalonSRX( Constants.elevatorMainMC );
    t1.configSelectedFeedbackSensor( FeedbackDevice.CTRE_MagEncoder_Relative );
    // ~184340 is one rotation, 5120 allows 5 degrees of error on each side
    posCalc= new SimpleMotorP( .02, 0.00000054, 1, .1, 5120 );
    
  }
  public double getOutput(){
    return (pos==ElevatorPos.undecided) ? 1 : posCalc.getOutput( t1.getSelectedSensorPosition() );
  }
  
  public void move(ElevatorPos pos){
    this.pos=pos;
    switch(pos){
      case undecided:
        pos=ElevatorPos.ballLow;
        posCalc.setSetpoint( ( 27.5 - initHeightOffGround ) / circ * elevatorGearbox );
      case ballLow:
        posCalc.setSetpoint( ( 27.5 - initHeightOffGround ) / circ * elevatorGearbox );
      break;
      case ballMed:
        posCalc.setSetpoint( ( 55.5 - initHeightOffGround ) / circ * elevatorGearbox );
      break;
      case ballHigh:
        posCalc.setSetpoint( (83.5-initHeightOffGround) / circ * elevatorGearbox );
      break;
      case cargoShip:
        posCalc.setSetpoint( (31.5-initHeightOffGround) / circ * elevatorGearbox );
      break;
        /*
      case hatchLow:
        posCalc.setSetpoint( 19 / circ * elevatorGearbox );
      break;
      case hatchMed:
        posCalc.setSetpoint( 47 / circ * elevatorGearbox );
      break;
      case hatchHigh:
        posCalc.setSetpoint( 75 / circ * elevatorGearbox );
      break;
        */
    }
    t1.set( ControlMode.PercentOutput, posCalc.getOutput( t1.getSelectedSensorPosition() ) );
    
  }
  public void manualMove( double val ){
    t1.set( ControlMode.PercentOutput, val );
  }
  public void stop(){
    t1.set( ControlMode.PercentOutput, 0);
  }
  @Override
  public void initDefaultCommand() {
    // Set the default command for a subsystem here.
    // setDefaultCommand(new MySpecialCommand());
    
  }
}
