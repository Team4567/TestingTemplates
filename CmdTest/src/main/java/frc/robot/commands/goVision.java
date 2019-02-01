/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import frc.robot.Robot;
import frc.robot.enums.want;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import frc.robot.Constants;

public class goVision extends Command {
  NetworkTableInstance inst=NetworkTableInstance.getDefault();
  NetworkTable chickenVision=inst.getTable("ChickenVision");
  NetworkTableEntry driveWanted,tapeWanted,cargoWanted,tapeYaw,cargoYaw;
  private XboxController xbC;
  double yDist;
  MotorCalculator mc;
  turnCalculator tc;
  private boolean xbM;
  private boolean done;
  private want w;
  private double setpointX,prevCalcX,outCalcX;

  
  public goVision(XboxController xbC,want w) {
    // Use requires() here to declare subsystem dependencies
    // eg. requires(chassis);
    requires(Robot.drive);
    this.xbC=xbC;
    xbM=true;
    this.w=w;

    driveWanted = chickenVision.getEntry("Driver");
		tapeWanted = chickenVision.getEntry("Tape");
		cargoWanted = chickenVision.getEntry("Cargo");
    tapeYaw=chickenVision.getEntry("tapeYaw");
    cargoYaw=chickenVision.getEntry("cargoYaw");
  }
  public goVision(double yDist,want w, MotorCalculator mc){
    xbM=false;
    this.yDist=yDist;
    this.mc=mc;
    this.w=w;

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
      driveWanted.setBoolean(false);
      tapeWanted.setBoolean(true);
      cargoWanted.setBoolean(false);
    }else if(w==want.cargo){
      System.out.println("Aligning to Cargo");
      driveWanted.setBoolean(false);
      tapeWanted.setBoolean(false);
      cargoWanted.setBoolean(true);
      }
    calcX();
    
  }
  private void calcX(){
    prevCalcX=outCalcX;
    switch(w){
      case tape:
      setpointX=tapeYaw.getDouble(123456789);
        if(setpointX==123456789){
          System.out.println("Yaw at the Default/None Found Value- 123456789");
        }
      break;
      case cargo:
        setpointX=cargoYaw.getDouble(123456789);
        if(setpointX==123456789){
          System.out.println("Yaw at the Default/None Found Value- 123456789");
        }
      break;
    }
    outCalcX= Math.min(Math.max(.015*-setpointX,-.4),.4);
    if(outCalcX-prevCalcX<=.02){

    }else{
      
        outCalcX=prevCalcX+Math.signum(outCalcX-prevCalcX)*.02;
      
        
        
      
    }
    if(Math.abs(outCalcX)<Constants.minValX){
      outCalcX=Math.signum(outCalcX)*Constants.minValX;
    }
  }

  // Called repeatedly when this Command is scheduled to run
  @Override
  protected void execute() {
    calcX();
    if(xbM){
      Robot.drive.drive(xbC.getY(Hand.kLeft),outCalcX);
    }else{
      
    }
  }
  public void setDone(boolean done){
    this.done=done;
  }
  // Make this return true when this Command no longer needs to run execute()
  @Override
  protected boolean isFinished() {
    if(done){
      return true;
    }else{
      return false;
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
