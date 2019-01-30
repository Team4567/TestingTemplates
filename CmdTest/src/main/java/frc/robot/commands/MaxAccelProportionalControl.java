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

package frc.robot.commands;

public class MaxAccelProportionalControl {
    private double targetValue;
    private double maxAccel;        // max the output can change per call.
    private double Kp;
    private double maxOutput;       // Not too fast until we fully test robot.
    private double minOutput;       // Stay above dead zone so we reach targetValue
    private double minError;        // error that is close enough.
    private double previousOutput;
      
    public MaxAccelProportionalControl( double targetValue, double maxAccel, double Kp, double maxOutput, 
                                        double minOutput, double minError ) {
        this.targetValue = targetValue;
        this.maxAccel = Math.abs(maxAccel);
        this.Kp = Kp;
        this.maxOutput = Math.abs(maxOutput);
        this.minOutput = Math.abs(minOutput);
        this.minError = Math.abs(minError);
        this.previousOutput = 0.0;
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
        MaxAccelProportionalControl c = new MaxAccelProportionalControl( 20000, 0.10, 0.0001, 0.5, 0.05, 100 );

        int maxRuns = 100;
        double currentPosition = 0;
        double output = 0.0;

        // done only when position is within 100 AND output has settled on zero.
        while( (Math.abs(20000-currentPosition) > 100 || output != 0.0) && maxRuns-- > 0 ) {  
            output = c.getOutput( currentPosition );

            System.out.println("CurrentPosition: " + currentPosition + " Output: " + output );
            currentPosition += output * 4096.0; // 4096 ticks per rev, 1 rev/sec

        }
        System.out.println("CurrentPosition: " + currentPosition );
    }

  }

