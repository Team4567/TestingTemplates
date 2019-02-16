package frc.robot.commands;

/*----------------------------------------------------------------------------*/
/* Sample control function that implements Proportional control to a maximum  */
/* speed but limits the acceleration at startup.                              */
/* The units don't matter as long as they are consistant:                     */
/* setPoint in the constructor, currentValue passed to getOuput and Kp all    */
/* should have the same distance unit (could be ticks or inches...)           */
/* maxAccel is the max change in the output per call.                         */
/*                                                                            */
/* NOTE: This is not a command, just a utility that can be used to control    */
/*       any motor.  This would be typically used by a command.               */
/*----------------------------------------------------------------------------*/

public class SimpleMotorP implements MotorCalculator {

    private double targetValue;
    private double maxAccel;        // max the output can change per call.
    private double Kp;
    private double maxOutput;       // Not too fast until we fully test robot.
    private double minOutput;       // Stay above dead zone so we reach targetValue
    private double minError;        // error that is close enough.
    private double previousOutput;

    public SimpleMotorP( double maxOutChange, double Kp, double maxOutput, double minOutput, double minError ) {
        init( maxOutChange, Kp, maxOutput, minOutput, minError );
    }

   

    private void init( double maxAccel, double Kp, double maxOutput, double minOutput, double minError ) {
        this.targetValue = 0.0;
        this.maxAccel = Math.abs(maxAccel);
        this.Kp = Kp;
        this.maxOutput = Math.abs(maxOutput);
        this.minOutput = Math.abs(minOutput);
        this.minError = Math.abs(minError);
        this.previousOutput = 0.0;
    }

    public void setSetpoint(double set){
        targetValue=set;
    }

    public double getOutput( double currentValue ) {
        // Separate the direction to simplify the math.
        // We could have a setpoint forward or backwards.
        // also, if we overshoot we need to come back.
        double direction = Math.signum(targetValue - currentValue);  // -1, 0, 1 returned for sign.
        double error = Math.abs(targetValue - currentValue);
        if( error < minError ) {
            error = 0.0;
        }
        double newOutput = error * Kp;

        if( newOutput > maxOutput ) {
            newOutput = maxOutput;
        } else if( error > 0.0 && newOutput < minOutput ) {
            newOutput = minOutput;
        }

        newOutput *= direction;  // Apply direction

        if( Math.abs(newOutput - previousOutput) > maxAccel ) {
            double directionOfOutputChange = Math.signum( newOutput - previousOutput );
            newOutput = previousOutput + (maxAccel * directionOfOutputChange );
        }

        previousOutput = newOutput;
        return newOutput;
    }

    public static void main(String[] args) {
        // This the test routine.
        // It is never called unless run explicitly, usually in the debugger.

        // Target 20000 ticks, max output change of 0.1, Kp, Max Output 0.5, 100 ticks for just under 1/2 inch
        //SimpleMotorP c = new SimpleMotorP( 20000, 0.10, 0.0004, 0.5, 0.05, 100 );

        int maxRuns = 100;
        double currentPosition = 0;
        double output = 0.0;

        // done only when position is within 100 AND output has settled on zero.
        while( (Math.abs(20000-currentPosition) > 100 || output != 0.0) && maxRuns-- > 0 ) {  
            //output = c.getOutput( currentPosition );

            System.out.println("CurrentPosition: " + currentPosition + " Output: " + output );

            // Max speed in ticks/call (for 1.0 output), 4096 ticks per rev, 0.25 rev/call
            // This needs to be accurate for the output to reflect reality.
            // We should measure speed at 0.5 (max) and double.
            currentPosition += output * 1024.0; 

        }
        System.out.println("CurrentPosition: " + currentPosition );
    }
  }

