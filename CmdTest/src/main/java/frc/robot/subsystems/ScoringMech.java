/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.Talon;

import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import frc.robot.Constants;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.ControlMode;
import edu.wpi.first.wpilibj.Compressor;
/**
 * Add your docs here.
 */
public class ScoringMech extends Subsystem {
  VictorSPX scoreL, scoreR;
  Talon emergBack;
  TalonSRX flippy;
  DoubleSolenoid pistonL,pistonR, pistonMisc;
  
  // Put methods for controlling this subsystem
  // here. Call these from Commands.
  public ScoringMech(){
    scoreL= new VictorSPX( Constants.scoreLMC );
    scoreR= new VictorSPX( Constants.scoreRMC );
    scoreR.follow( scoreL );
    flippy= new TalonSRX( Constants.flippyMC );
    emergBack= new Talon( 0 );
    
    pistonL= new DoubleSolenoid( 10, 0, 1 );
    pistonR= new DoubleSolenoid( 10, 2, 3 );
    pistonMisc= new DoubleSolenoid( 10, 4, 5 );

  }
  public void moveScore( double value ){
    scoreL.set( ControlMode.PercentOutput, value );
    scoreR.follow( scoreL );
  }
  public void moveFlipper( double value ){
    flippy.set( ControlMode.PercentOutput, value );
  }
  public void setPiston( boolean in, boolean out ){
    if( in ){
      pistonL.set( DoubleSolenoid.Value.kReverse );
      pistonR.set( DoubleSolenoid.Value.kReverse );
    } else if ( out ){
      pistonL.set( DoubleSolenoid.Value.kForward );
      pistonR.set( DoubleSolenoid.Value.kForward );
    } else {
      pistonL.set( DoubleSolenoid.Value.kOff );
      pistonR.set( DoubleSolenoid.Value.kOff );
    }
  }
  public void useEmerg( double value ){
    emergBack.set(value);
  }
  @Override
  public void initDefaultCommand() {
    // Set the default command for a subsystem here.
    // setDefaultCommand(new MySpecialCommand());
  }
}
