/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.DoubleSolenoid;

/**
 * Add your docs here.
 */
public class scoringMech extends Subsystem {
  private static scoringMech starter= new scoringMech();
  TalonSRX elevatorL, elevatorR;
  Spark intakeL, intakeR, flipL, flipR;
  DoubleSolenoid pistonL,pistonR;
  // Put methods for controlling this subsystem
  // here. Call these from Commands.
  public static scoringMech start(){
    return starter;
  }
  public scoringMech(){
    
  }
  @Override
  public void initDefaultCommand() {
    // Set the default command for a subsystem here.
    // setDefaultCommand(new MySpecialCommand());
  }
}
