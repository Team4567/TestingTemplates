/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import com.ctre.phoenix.sensors.PigeonIMU;
import frc.robot.Robot;
import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Constants;

public class SimpleTurnP implements TurnCalculator{

  private double targetValue,maxAccel,Kp,maxOutput,minOutput,minError,prevOutput;

  //double targetValue, double maxOutChange, double Kp, double maxOutput, 
  //double minOutput, double minError
  public SimpleTurnP( double targetValue, double maxOutChange, double Kp, double maxOutput, double minOutput, double minError ) {
    init( maxOutChange, Kp, maxOutput, minOutput, minError );
    setSetpoint( targetValue );
  }
  public SimpleTurnP( double maxOutChange, double Kp, double maxOutput, double minOutput, double minError ) {
    init( maxOutChange, Kp, maxOutput, minOutput, minError );

  }
  private void init( double maxOutChange, double Kp, double maxOutput, double minOutput, double minError ){
    
  }
  public void setSetpoint( double setpoint ){
    targetValue = setpoint;
  }
  
  
  
  public double getOutput( double currentValue ) {
    double output;
    double error = Math.abs( targetValue - currentValue );
    double direction = Math.signum( targetValue - currentValue );
    if( error < minError ) error = 0;
    output = Kp * error;
    if( output > maxOutput ) {
      output = maxOutput;
    } else if( error > 0.0 && output < minOutput ) {
      output = minOutput;
    }
    output *= direction;
    if( Math.abs( output - prevOutput ) > maxAccel ) {
      double directionOfOutputChange = Math.signum( output - prevOutput );
      output = prevOutput + ( maxAccel * directionOfOutputChange );
    }
    
    prevOutput = output;           
    
    
    return output; 
  }
  
  // Called just before this Command runs the first time
 
  // Called repeatedly when this Command is scheduled to run
  

}
