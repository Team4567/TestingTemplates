/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.enums.want;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.NetworkTableEntry;

public class SetWanted extends Command {
  private want w;
  private NetworkTableInstance inst;
  private NetworkTable chickenVision;
  private NetworkTableEntry driveWanted,cargoWanted,tapeWanted;
  public SetWanted(want w) {
    // Use requires() here to declare subsystem dependencies
    // eg. requires(chassis);
    this.w=w;
    inst=NetworkTableInstance.getDefault();
    chickenVision=inst.getTable("ChickenVision");
    driveWanted = chickenVision.getEntry("Driver");
		tapeWanted = chickenVision.getEntry("Tape");
		cargoWanted = chickenVision.getEntry("Cargo");
  }

  // Called just before this Command runs the first time
  @Override
  protected void initialize() {
    switch(w){
      case cargo:
        driveWanted.setBoolean(false);
        cargoWanted.setBoolean(true);
        tapeWanted.setBoolean(false); 
      break;
      case tape:
        driveWanted.setBoolean(false);
        cargoWanted.setBoolean(false);
        tapeWanted.setBoolean(true); 
      break;
      default:
        driveWanted.setBoolean(true);
        cargoWanted.setBoolean(false);
        tapeWanted.setBoolean(false); 
      break;
    }
  }

  // Called repeatedly when this Command is scheduled to run
  @Override
  protected void execute() {
    switch(w){
      case cargo:
        driveWanted.setBoolean(false);
        cargoWanted.setBoolean(true);
        tapeWanted.setBoolean(false); 
      break;
      case tape:
        driveWanted.setBoolean(false);
        cargoWanted.setBoolean(false);
        tapeWanted.setBoolean(true); 
      break;
      default:
        driveWanted.setBoolean(true);
        cargoWanted.setBoolean(false);
        tapeWanted.setBoolean(false); 
      break;
    }
  }

  // Make this return true when this Command no longer needs to run execute()
  @Override
  protected boolean isFinished() {
    
    if(w==want.cargo){
      // If not the ones you aren't supposed to and the one you ARE supposed to
      if(!driveWanted.getBoolean(false)&&!tapeWanted.getBoolean(false)&&cargoWanted.getBoolean(false)){
        return true;
      }else{
        return false;
      }
    } else if(w==want.tape){
      if(!driveWanted.getBoolean(false)&&tapeWanted.getBoolean(false)&&!cargoWanted.getBoolean(false)){
        return true;
      }else{
        return false;
      }
    }else{
      if(driveWanted.getBoolean(false)&&!tapeWanted.getBoolean(false)&&!cargoWanted.getBoolean(false)){
        return true;
      }else{
        return false;
      }
    }
  }

  // Called once after isFinished returns true
  @Override
  protected void end() {
  }

  // Called when another command which requires one or more of the same
  // subsystems is scheduled to run
  @Override
  protected void interrupted() {
  }
}
