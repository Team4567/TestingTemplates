/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;
import frc.robot.enums.want;

public class alignVision extends Command{
  //P,I,D,integral,previous_error,setpoint,error,derivative,output,pidgey,done,outputOnly all inherited
  //Retreive Vision Info
  NetworkTableInstance inst=NetworkTableInstance.getDefault();
  NetworkTable chickenVision=inst.getTable("ChickenVision");
  NetworkTableEntry driveWanted,tapeWanted,cargoWanted,tapeYaw,cargoYaw;
  // Alert us if we have a target selected (not in driver mode)/if we just want the output(no movement, for other commands)
  private boolean targetSelected;
  double setpoint;
  want w;
  turnCalculator tc;
  //Able to be defined if turnAngle is moving us or not moving us
  //Output, to be copied to others
  public alignVision(want w) {
    tc= new simpleTurnP(Robot.drive.gyro);
    this.w=w;
      requires(Robot.drive);
    
    driveWanted = chickenVision.getEntry("Driver");
		tapeWanted = chickenVision.getEntry("Tape");
		cargoWanted = chickenVision.getEntry("Cargo");
    tapeYaw=chickenVision.getEntry("tapeYaw");
    cargoYaw=chickenVision.getEntry("cargoYaw");
    
  }

  // Called just before this Command runs the first time
  @Override
  protected void initialize() {
    if(w==want.tape){
      System.out.println("Aligning to Tape");  
      targetSelected=true; 
      tc.setSetpoint(tapeYaw.getDouble(123456789));
      setpoint=tapeYaw.getDouble(123456789);
      System.out.println(setpoint);
      if(tc.getSetpoint()==123456789){
        System.out.println("Yaw at the Default/None Found Value- 123456789");
        targetSelected=false;
      }
    }else if(w==want.cargo){
      System.out.println("Aligning to Cargo");
      targetSelected=true;
      tc.setSetpoint(cargoYaw.getDouble(123456789));
      setpoint=cargoYaw.getDouble(123456789);
      if(tc.getSetpoint()==123456789){
        System.out.println("Yaw at the Default/None Found Value- 123456789");
        targetSelected=false;
      }
    }else{
      System.out.println("No Target Selected");
      targetSelected=false;
    }
    Robot.drive.gyro.setYaw(0);
    tc.start();
  }

  // Called repeatedly when this Command is scheduled to run
  
  @Override
  protected void execute() {
    if(targetSelected){
      Robot.drive.drive(0,tc.getOutput());
    }
      
  }
  
  // Make this return true when this Command no longer needs to run execute()
  @Override
  protected boolean isFinished() {
    if(!targetSelected){
      return true;
    }else{
      if(Robot.drive.getYaw()>setpoint-1&&Robot.drive.getYaw()<setpoint+1){
        return true;
      }else{
        return false;
     }
      
    }
  }

  // Called once after isFinished returns true
  
  
  @Override
  protected void end() {
    Robot.drive.stop();

  }

  // Called when another command which requires one or more of the same
  // subsystems is scheduled to run
  
  @Override
  protected void interrupted() {
    Robot.drive.stop();
  }
}
