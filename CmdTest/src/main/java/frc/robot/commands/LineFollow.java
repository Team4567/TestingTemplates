/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Constants;
import frc.robot.Robot;

public class LineFollow {
  double yOut,xOut=0;
  double yPrevOut, xPrevOut=0;
  double kPY,kPX;
  NetworkTableInstance inst= NetworkTableInstance.getDefault();
  NetworkTable lineFollower= inst.getTable( "Line Follow" );
  NetworkTableEntry xEntry= lineFollower.getEntry( "x" );
  NetworkTableEntry yEntry=lineFollower.getEntry( "y" );
  public LineFollow( double kPY, double kPX ) {
    
    
    this.kPY=kPY;
    this.kPX=kPX;
   
  }
  public double turn(){
    xOut=( xEntry.getDouble(0) - ( Constants.camW/2 ) )*kPX;
    double direction= Math.signum( xEntry.getDouble(0) - ( Constants.camW/2 ) );
    if( Math.abs( xPrevOut-xOut ) >.02 ){
      xOut=xPrevOut+Math.signum( direction );
    }
    if( Math.abs( xOut ) <.1 ){
      xOut=.1*direction;
    }
    xPrevOut=xOut;
    return xOut;
  }
}
