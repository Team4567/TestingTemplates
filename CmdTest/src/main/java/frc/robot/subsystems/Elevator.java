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

/**
 * Add your docs here.
 */
public class Elevator extends Subsystem {
  private MotorCalculator posCalc;
  private double diameter=0;
  private double circ= Math.PI*diameter;
  private double elevatorGearbox=184320;
  public TalonSRX t1;
  // Put methods for controlling this subsystem
  // here. Call these from Commands.
  public Elevator(){
    t1=new TalonSRX(Constants.elevatorMainMC);
    t1.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative);
    //double maxOutChange, double Kp, double maxOutput, double minOutput, double minError
    // ~184340 is one rotation
    posCalc= new SimpleMotorP(.02,1,.1,100000);
    
  }
  
  
  public void move(ElevatorPos pos){
    switch(pos){
      case ballLow:
        posCalc.setSetpoint( 1 / circ * elevatorGearbox );
      break;
      case ballMed:
        posCalc.setSetpoint( 2 / circ * elevatorGearbox );
      break;
      case ballHigh:
        posCalc.setSetpoint( 3 / circ * elevatorGearbox );
      break;
    }
    t1.set( ControlMode.PercentOutput, posCalc.getOutput( t1.getSelectedSensorPosition() ) );
    
  }
  @Override
  public void initDefaultCommand() {
    // Set the default command for a subsystem here.
    // setDefaultCommand(new MySpecialCommand());
    
  }
}
