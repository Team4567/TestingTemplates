package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.AnalogInput;
import frc.robot.constants;
import frc.robot.enums.*;

public class drivetrain {
    private static drivetrain starter = new drivetrain();
    private TalonSRX rightMain, rightSlave, leftMain, leftSlave;
    private AnalogInput range;
    private double scaleR;
    public static drivetrain start(){
       return starter;
    }
    
    private drivetrain(){
        rightMain= new TalonSRX(constants.rightMainMC);
        rightMain.setNeutralMode(NeutralMode.Brake);
        rightMain.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative);
        leftMain= new TalonSRX(constants.leftMainMC);
        leftMain.setNeutralMode(NeutralMode.Brake);
        rightMain.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative);
        rightSlave= new TalonSRX(constants.rightSlaveMC);
        rightSlave.follow(rightMain);
        rightSlave.setNeutralMode(NeutralMode.Brake);
        leftSlave= new TalonSRX(constants.leftSlaveMC);
        leftSlave.follow(leftMain);
        leftSlave.setNeutralMode(NeutralMode.Brake);
        range = new AnalogInput(0);
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
    public double rangeFinderDistance(distanceUnit unit){
      final float Vi=5/1024;
      
      //Vi= Volts per 5 mm
      if(unit== distanceUnit.centimeters){
        scaleR = (Vi/5)*100;

      }
      if(unit==distanceUnit.inches){
        scaleR = ((Vi/5)*100)/2.54;

      }
      return (range.getVoltage()*scaleR);
    }
    public double encoderDistanceInInches(TalonSRX t){
      return t.getSelectedSensorPosition()*((1/4096)*(constants.wheelCirc));
    }
    public double encoderDistanceInCentimenters(TalonSRX t){
      return t.getSelectedSensorPosition()*((1/4096)*(constants.wheelCirc)*2.54);
    }
    public TalonSRX right(){
      return rightMain;
    }
    public TalonSRX left(){
      return leftMain;
    }





}