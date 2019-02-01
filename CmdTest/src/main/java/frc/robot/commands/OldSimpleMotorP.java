/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Constants;

public class OldSimpleMotorP extends Command { // implements MotorCalculator {

  public static boolean done;
  private double output, prevOutput;
  public double setpoint;
  public double P,I,D;
  TalonSRX tR,tL;

  public OldSimpleMotorP(double setpointInches, TalonSRX tR, TalonSRX tL) {
    init( tR, tL );
    setSetpointInches(setpointInches);
  }

  public OldSimpleMotorP(TalonSRX tR, TalonSRX tL) {
    init( tR, tL );
  }

  private void init( TalonSRX tR, TalonSRX tL) {
    done=false;
    P= Constants.motorP;
    I= Constants.motorI;
    D= Constants.motorD;
    this.tR=tR;
    this.tL=tL;
  }

  public void setSetpointInches(double setpoint) {
    this.setpoint=setpoint/Constants.wheelCirc*4096;
  }

  // Called just before this Command runs the first time
  @Override
  protected void initialize() {
    done=false;
    tR.setSelectedSensorPosition(0);
    prevOutput=0;
    output=0;
    calculate();
  }

  // Called repeatedly when this Command is scheduled to run
  
  public void calculate() {
    prevOutput=output;
    double currentPosition = tR.getSelectedSensorPosition();
    output=Math.max(Math.min(P*(setpoint-currentPosition),0.5),-0.5);
  }

  @Override
  protected void execute() {
    calculate();
    // Limit change in output to 0.02
    if( Math.abs(output-prevOutput) > 0.02 ) 
    {
        output=prevOutput+Math.signum(output-prevOutput) * 0.05;
    }
    // Make sure output is at least minValY
    if( Math.abs(output) < Constants.minValY && output<prevOutput) 
    {
        output = Math.signum(output) * Constants.minValY;
    }
  }
  
  // @Override
  public double getOutput() {
    return output;
  }

  public void setDone(boolean set) {
    done=set;
  }
  
  public boolean isDone() {
    return done;
  }

  // Make this return true when this Command no longer needs to run execute()
  @Override
  protected boolean isFinished() {
    if(done||(tR.getSelectedSensorPosition()>setpoint-100&&tR.getSelectedSensorPosition()<setpoint+100&&getOutput()==.1)){
      return true;
    }else{
    return false;
    }
  }

  // Called once after isFinished returns true
  @Override
  protected void end() {
    done=true;
    System.out.println("Target Reached");
  }

  // Called when another command which requires one or more of the same
  // subsystems is scheduled to run
  @Override
  protected void interrupted() {
    done=true;
  }
}
