package frc.robot;

public class Constants {
    public static final int rightMainMC = 2;
    public static final int leftMainMC = 3;
    public static final int rightSlaveMC = 1;
    public static final int leftSlaveMC = 4;

    public static final double wheelCirc = 1.5625 * 12;
    public static final double wheelDiameter = wheelCirc / Math.PI;

    public static final int platformPCM = 42;
    public static final int scoringPCM = 10;

    public static final int elevatorMainMC=6;
    //public static final int elevatorSlaveMC=6;

    public static final int scoreLMC = 7;
    public static final int scoreRMC = 8;
    public static final int flippyMC = 9;

    
    
    public static final double minValY = 0.1;
    public static final double minValX = 0.0;
    public static final double closeEnough = 100;  // 100 ticks is less than 1/2 inch

    //0.2

    //1.5625*12- Hallway Floor
    // - Carpet
    public static final double motorP = 0.00005;
    public static final double motorI = 0;
    public static final double motorD = 0;
    // - Hallway Floor
    // - Carpet
    public static final double gyroP =0.003;
    public static final double gyroI =0;
    public static final double gyroD =0;

    public static final double elevP = 0.001;
    public static final double elevI = 0;
    public static final double elevD = 0;
}
