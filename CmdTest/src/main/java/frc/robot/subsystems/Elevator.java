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
  
  public TalonSRX t1;
  // Put methods for controlling this subsystem
  // here. Call these from Commands.
  public Elevator(){
    t1=new TalonSRX(Constants.elevatorMainMC);
    t1.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative);
    
    
  }
  
  
  public void move(double value){
    t1.set(ControlMode.PercentOutput, Math.min(Math.max(value,1),-1));
    
  }
  @Override
  public void initDefaultCommand() {
    // Set the default command for a subsystem here.
    // setDefaultCommand(new MySpecialCommand());
    
  }
}
