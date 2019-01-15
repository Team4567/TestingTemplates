package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.sensors.PigeonIMU;
public class PID{
    double P,I,D;
    TalonSRX left,right;
    double integral,derivative, previous_error, setpoint=0;
    PigeonIMU pidgey;
    double avgEncoder;
    double[] ypr;

    public PID(double P, double I, double D, TalonSRX TalonL, TalonSRX TalonR){
        P=this.P;
        I=this.I;
        D=this.D;
        avgEncoder=(TalonL.getSelectedSensorPosition()+TalonR.getSelectedSensorPosition())/2;
        left=TalonL;
        right=TalonR;
        ypr= new double[3];
        

    }
    public PID(double P, double I, double D, PigeonIMU Pigeon){
        P=this.P;
        I=this.I;
        D=this.D;
        pidgey= Pigeon;
        pidgey.getYawPitchRoll(ypr);
    }
    public void setSetpoint(double set){
        this.setpoint=set;
    }
    public double distanceSpeed(){
        double error=setpoint-avgEncoder;
        this.integral+= (error*.02);
        this.derivative= (error-this.previous_error)/.02;
        return P*error+I*this.integral+D*derivative;
    }
    public double angle(){
        double error=setpoint-ypr[0];
        this.integral+= (error*.02);
        this.derivative= (error-this.previous_error)/.02;
        return P*error+I*this.integral+D*derivative;
    }
}