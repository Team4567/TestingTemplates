package frc.robot;

public class constants{
    public static final int rightMainMC = 2;
    public static final int leftMainMC = 3;
    public static final int rightSlaveMC = 1;
    public static final int leftSlaveMC = 4;
    public static final int rangeFinder = 0;

    public static final int elevatorMainMC=5;
    public static final int elevatorSlaveMC=6;

    public static final int scoreInSplit=0;
    public static final int scoreFlipSplit=0;

    public static final double wheelCirc=1.5625*12;
    public static final double wheelDiameter=wheelCirc/Math.PI;
    
    public static final double minValY=0.1;
    public static final double minValX=0.25;

    //1.5625*12- Hallway Floor
    // - Carpet
    public static final double motorP=0.00005;
    public static final double motorI=0;
    public static final double motorD=0;
    public static final double motorFF=0;
    // - Hallway Floor
    // - Carpet
    public static final double gyroP=0.0015;
    public static final double gyroI=0;
    public static final double gyroD=0;
    public static final double gyroFF=0;
    public static final double elevP=0.001;
    public static final double elevI=0;
    public static final double elevD=0;
    public static final double elevFF=0;
    
    public static final int camW=256;
    public static final int camH=144;
    public static final int camFPS=30;
}