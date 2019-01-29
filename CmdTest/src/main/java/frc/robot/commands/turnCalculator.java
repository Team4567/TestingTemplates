package frc.robot.commands;
public interface turnCalculator{
    public void calculate();
    public double getOutput();
    public void start();
    public boolean isDone();
    public void setDone(boolean set);
    public void setSetpoint(double setpoint);
    public double getSetpoint();
    public void setSetpointFromPos(int inc);
    public void setSetpointToCurrent();
}