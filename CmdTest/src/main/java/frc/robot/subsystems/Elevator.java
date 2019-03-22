/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.command.Subsystem;
import frc.robot.Constants;
import frc.robot.commands.*;
import frc.robot.enums.*;
import frc.robot.calculators.*;
/**
 * Add your docs here.
 */
public class Elevator extends Subsystem {
  public TalonSRX t1;
  public Elevator(){
    t1 = new TalonSRX( Constants.elevatorMainMC );
    t1.setNeutralMode( NeutralMode.Brake ); 
  }
  public void move( double val ){
    t1.set( ControlMode.PercentOutput, val );
  }
  public void stop(){
    t1.set( ControlMode.PercentOutput, 0 );
  }
  @Override
  public void initDefaultCommand() {
    // Set the default command for a subsystem here.
    // setDefaultCommand(new MySpecialCommand());
    
  }
}
