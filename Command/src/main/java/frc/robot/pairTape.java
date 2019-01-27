/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import org.opencv.core.RotatedRect;
import java.util.ArrayList;
import edu.wpi.first.networktables.*;
import frc.robot.Robot;
/**
 * Arranges Left and Right Retro-Reflective Tapes into pairs, Lf.get(x) and Rf.get(x) will be a pair.
 */
public class pairTape {
    ArrayList<RotatedRect> L,R;
    public ArrayList<RotatedRect> Lf,Rf;
    
    pairTape(ArrayList<RotatedRect> L, ArrayList<RotatedRect> R){
        this.L=L;
        this.R=R;
        Lf= new ArrayList<RotatedRect>();
        Rf= new ArrayList<RotatedRect>();
        
    }
    public void process(){
        
        for(int i=0;i<L.size();i++){
            int o;
            double min=99999999;
            int minI=-1;
            double xDL=L.get(i).center.x;

            for(o=0;i<R.size();o++){
               double xDR=R.get(o).center.x; 
               if(Math.abs(xDL-xDR)<min&&xDL-xDR<0){
                    min=Math.abs(xDL-xDR);
                    minI=o;
               }
            }
            Lf.add(L.get(i));
            for(o=0;i<R.size();o++){
                if(o==minI){
                    Rf.add(R.get(o));
                }
            }
        }
        if (!Lf.isEmpty()&&!Rf.isEmpty()&&Lf.size()==Rf.size()){
            for(int i=0;i<Lf.size();i++){
              Robot.tableL.delete("x of rect "+i);
              Robot.tableL.delete("y of rect "+i);
              Robot.tableL.delete("angle of rect "+i);
              NetworkTableEntry xLEntry = Robot.tableL.getEntry("x of rect " + i);
              NetworkTableEntry yLEntry = Robot.tableL.getEntry("y of rect " + i);
              NetworkTableEntry angleLEntry = Robot.tableL.getEntry("angle of rect " + i);
              xLEntry.setDouble(Lf.get(i).center.x);
              yLEntry.setDouble(Lf.get(i).center.y);
              angleLEntry.setDouble(Lf.get(i).angle);
              //R Chunk
              Robot.tableR.delete("x of rect "+i);
              Robot.tableR.delete("y of rect "+i);
              Robot.tableR.delete("angle of rect "+i);
              NetworkTableEntry xREntry = Robot.tableR.getEntry("x of rect " + i);
              NetworkTableEntry yREntry = Robot.tableR.getEntry("y of rect " + i);
              NetworkTableEntry angleREntry = Robot.tableR.getEntry("angle of rect " + i);
              xREntry.setDouble(Rf.get(i).center.x);
              yREntry.setDouble(Rf.get(i).center.y);
              angleREntry.setDouble(Rf.get(i).angle);
            }
          }else{
            System.out.println("Something's going on here chief.");
          }
    }
    public double centerMidpointX(){
        double min=99999999;
        int minI=-1;
        
        for(int i=0;i<Lf.size();i++){
            double midpoint=(Lf.get(i).center.x+Rf.get(i).center.x)/2;
            double midpointDist= Math.abs(midpoint-(constants.camW/2)); 
            if(midpointDist<min){
                min=midpointDist;
                minI=i;
            }
        }
        return (Lf.get(minI).center.x+Rf.get(minI).center.x)/2;
    }
    public int midPairPos(){
        double min=99999999;
        int minI=-1;
        
        for(int i=0;i<Lf.size();i++){
            double midpoint=(Lf.get(i).center.x+Rf.get(i).center.x)/2;
            double midpointDist= Math.abs(midpoint-(constants.camW/2)); 
            if(midpointDist<min){
                min=midpointDist;
                minI=i;
            }
        }
        return minI;
    }
}
