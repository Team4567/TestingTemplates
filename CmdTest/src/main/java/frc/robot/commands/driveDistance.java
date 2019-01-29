/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;
import frc.robot.constants;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
/**
 * An example command.  You can replace me with your own command.
 */
public class driveDistance extends Command {
  public double P,I,D;
  public double integral=0, previous_error=0, setpointInch, error, derivative;
  public double output=0;
  double avgEncoder;
  private TalonSRX tL, tR;
  Timer t;
  turnAngle straight;
  public boolean done;
  boolean incPhase;
  MotorCalculator mc;
  String mcType;
  public driveDistance(MotorCalculator mc){
    requires(Robot.drive);
    this.mc=mc;
    straight= new turnAngle(true);
    tL=Robot.drive.leftMain;
    tR=Robot.drive.rightMain;
    // If it is in this format, it is likely a teleOp and called repeatedly. This allows new mc to be made, preventing bug
    switch(mc.getClass().getName()){
      case "simpleMotorP":
        mcType="simpleMotorP";
      break;
    }
  }
  public driveDistance(double setpointInches, MotorCalculator mc) {
    
    // Use requires() here to declare subsystem dependencies
    requires(Robot.drive);
    this.mc=mc;
    tL= Robot.drive.leftMain;
    tR= Robot.drive.rightMain;
    avgEncoder=tR.getSelectedSensorPosition();
    setSetpointInches(setpointInches);
  }
  public void setSetpointInches(double setpointInch){ 
    this.setpointInch=setpointInch;
  }
  // Called just before this Command runs the first time
  @Override
  protected void initialize() {
    done=false;
    switch(mcType){
      case "simpleMotorP":
        mc= new simpleMotorP(tR, tL);
      break;
    }
    mc.setSetpointInches(setpointInch);
    mc.start();
    straight.setSetpointToCurrent();
    straight.start();
  }

  // Called repeatedly when this Command is scheduled to run
  @Override
  protected void execute() {
    Robot.drive.drive(mc.getOutput(),straight.getOutput());
    System.out.println(mc.getOutput());
  }

  // Make this return true when this Command no longer needs to run execute()
  @Override
  protected boolean isFinished() {
    if(mc.isDone()){
      return true;
    }else{
      return false;
    }
  }  


  // Called once after isFinished returns true
  @Override
  protected void end() {
    Robot.drive.stop();
    straight.done=true;
    mc.setDone(true);
  }

  // Called when another command which requires one or more of the same
  // subsystems is scheduled to run
  @Override
  protected void interrupted() {
    Robot.drive.stop();
    mc.setDone(true);
    straight.done=true;
  }
}
