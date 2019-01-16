package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.sensors.PigeonIMU;
public class PID{
    private double P,I,D;
    private TalonSRX left,right;
    private double integral,derivative, previous_error=0;
    private PigeonIMU pidgey;
    public double avgEncoder;
    public double[] ypr;
    public int setpoint=0;

    public PID(TalonSRX TalonL, TalonSRX TalonR){
        P=constants.motorP;
        I=constants.motorI;
        D=constants.motorD;
        avgEncoder=(TalonL.getSelectedSensorPosition()+TalonR.getSelectedSensorPosition())/2;
        left=TalonL;
        right=TalonR;
        ypr= new double[3];
        

    }
    public PID(PigeonIMU Pigeon){
        P=constants.gyroP;
        I=constants.gyroI;
        D=constants.gyroD;
        pidgey= Pigeon;
        pidgey.getYawPitchRoll(ypr);
    }
    public void setSetpoint(int set){
        this.setpoint=set;
    }
    public double distanceSpeed(){
        double error=setpoint-avgEncoder;
        integral+= (error*.02);
        derivative= (error-this.previous_error)/.02;
        previous_error=error;
        return Math.min(Math.max((P*error+I*integral+D*derivative),-1.0D), 1.0D);
    }
    public double angle(){
        double error=setpoint-ypr[0];
        integral+= (error*.02);
        derivative= (error-this.previous_error)/.02;
        previous_error=error;
        return Math.min(Math.max((P*error+I*integral+D*derivative),-1.0D), 1.0D);
    }
}