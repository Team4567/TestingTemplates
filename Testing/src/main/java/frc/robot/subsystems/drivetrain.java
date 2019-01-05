package frc.robot.subsystems;

import edu.wpi.first.wpilibj.PWMTalonSRX;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import frc.robot.constants;

public class drivetrain {
    private static drivetrain starter = new drivetrain();
    private PWMTalonSRX rightMain, rightSlave, leftMain, leftSlave;


    public static drivetrain start(){
       return starter;
    }
    
    private drivetrain(){
        rightMain= new PWMTalonSRX(constants.rightMainMC);
        leftMain= new PWMTalonSRX(constants.leftMainMC);
        rightSlave= new PWMTalonSRX(constants.rightSlaveMC);
        //rightSlave.follow(rightMain);
        leftSlave= new PWMTalonSRX(constants.leftSlaveMC);
        //leftSlave.follow(leftMain);
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
        rightMain.set(rightMotors);
        leftMain.set(leftMotors);
        // \/ These will only be here until we have the real Talon Libs, then we can use .follow \/
        rightSlave.set(rightMotors);
        leftSlave.set(leftMotors);
        
    }
    public void autoDrive(){
        // Have plenty of ideas, need event details
    }
    public void stop(){
        rightMain.set(0);
        leftMain.set(0);
        rightSlave.set(0);
        leftSlave.set(0);
    }
    








}