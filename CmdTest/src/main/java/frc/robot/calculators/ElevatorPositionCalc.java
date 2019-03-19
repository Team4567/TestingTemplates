/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.calculators;
import frc.robot.Constants;
import frc.robot.Robot;
import frc.robot.enums.ElevatorPos;
public class ElevatorPositionCalc{
  double P, I, D;
  double previous_error, setpoint, error,kP;
  public ElevatorPositionCalc( double kP ) {
    this.kP = kP;
  }
  public ElevatorPositionCalc( double setpoint, double kP ) {
    this.kP = kP;
    this.setpoint = setpoint * 4096 * 45;
  }
  public void setSetpoint( int setpoint ){
    this.setpoint = setpoint * 4096 * 45;
  }
  public double getOutput(){
    error = setpoint - Robot.upper.t1.getSelectedSensorPosition();
    double newOutput = ( ( error*kP ) - previous_error > .02 ) ? previous_error + .02 : error*kP;
    
    return newOutput;
  }
  

 
//#25 chain 

}
