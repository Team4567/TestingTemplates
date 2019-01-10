package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import frc.robot.constants;

public class drivetrain {
    private static drivetrain starter = new drivetrain();
    private TalonSRX rightMain, rightSlave, leftMain, leftSlave;
    public static drivetrain start(){
       return starter;
    }
    
    private drivetrain(){
        rightMain= new TalonSRX(constants.rightMainMC);
        rightMain.setNeutralMode(NeutralMode.Brake);
        leftMain= new TalonSRX(constants.leftMainMC);
        leftMain.setNeutralMode(NeutralMode.Brake);
        rightSlave= new TalonSRX(constants.rightSlaveMC);
        rightSlave.follow(rightMain);
        rightSlave.setNeutralMode(NeutralMode.Brake);
        leftSlave= new TalonSRX(constants.leftSlaveMC);
        leftSlave.follow(leftMain);
        leftSlave.setNeutralMode(NeutralMode.Brake);
    }
    public void teleOpDrive(XboxController controller){
        // Version of arcadeDrive from edu.wpi.first.wpilibj.RobotDrive, lines 401-417
        // Joe Lange (Driver) is used to this naturally from years past, maybe we can change it.
        // People I talk to say we should split robot controls to 2 people, 1 on the drivetrain 1 on the mechanisms
        // I'm not gonna argue with these people, 
        //I usually have nothing to do after the match starts during drive team, I'd take a controller any day :)
        // Would have to convince Mr. Isgro...
        double y= controller.getY(Hand.kLeft);
        double x= controller.getX(Hand.kLeft);
        double leftMotors, rightMotors;
        if (y > 0.0) {
            if (x > 0.0) {
              leftMotors = y - x;
              rightMotors = Math.max(y, x);
            } else {
              leftMotors = Math.max(y, -x);
              rightMotors = y + x;
            }
          } else {
            if (x > 0.0) {
              leftMotors = -Math.max(-y, x);
              rightMotors = y + x;
            } else {
              leftMotors = y - x;
              rightMotors = -Math.max(-y, -x);
            }
          }
        rightMain.set(ControlMode.PercentOutput, rightMotors);
        leftMain.set(ControlMode.PercentOutput, leftMotors);
        
        
        
    }
    public void autoDrive(){
        // Have plenty of ideas, need event details
    }
    public void stop(){
        rightMain.set(ControlMode.PercentOutput, 0);
        leftMain.set(ControlMode.PercentOutput, 0);
    }
    








}