package frc.robot.calculators;

public interface MotorCalculator {
    public void setSetpoint( double setpoint );
    public double getOutput( double currentValue );
}
