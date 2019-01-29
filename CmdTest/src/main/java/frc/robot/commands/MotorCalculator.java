package frc.robot.commands;
public interface MotorCalculator{
    public void calculate();
    public double getOutput();
    public void start();
    public boolean isDone();
    public void setDone(boolean set);
    public void setSetpointInches(double setpoint);
}