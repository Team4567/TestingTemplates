import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

NetworkTableInstance inst;
NetworkTable table;
NetworkTableEntry targetLock, resetEncoder, currentCamera;
int checkR, checkG, checkB;
String target;
void setup(){
  size( 450, 500 );
  inst = NetworkTableInstance.getDefault();
  table = inst.getTable( "Vision" );
  targetLock = table.getEntry( "Target" );
  resetEncoder = table.getEntry( "Reset Encoder" );
  currentCamera = table.getEntry( "Current Camera" );
}
void draw(){
  if( targetLock.getBoolean( false ) ){
    checkR=0;
    checkG=255;
    checkB=0;
    target= "Target Locked";
  } else {
    checkR=255;
    checkG=0;
    checkB=0;
    target= "Missing Target";
  }
  fill( checkR, checkG, checkB );
  rect( 0, 0, 450, 300 );
  fill( 0 );
  stroke( 0 );
  textSize( 50 );
  text( target, 50, 150 );
  textSize( 25 );
  // Row 1
  fill( 0, 0, 255 );
  rect( 0, 300, 225, 75 );
  fill( 255, 0 , 255);
  rect( 225, 300, 225, 75 );
  // Row 1 Text
  fill( 0 );
  text( "Reset Encoder", 25, 350 );
  text( "Extra Button 1", 260, 350 );
  // Row 2
  fill( 0, 0, 255 );
  rect( 0, 375, 225, 75 );
  fill( 255, 0 , 255);
  triangle( 225, 375, 450, 375, 338, 413 );
  triangle( 225, 450, 450, 450, 338, 413 );
  triangle( 225, 375, 225, 450, 338, 413 );
  triangle( 450, 375, 450, 450, 338, 413 );
  //rect( 225, 375, 225, 75 );
  // Row 2 Text
  fill( 0 );
  text( "Extra Button 2", 25, 420 );
  text( "0", 330, 400 );
  text( "270", 390, 420 );
  text ( "180", 315, 445 );
  text ( "90", 250, 420 );
  // Camera
  fill( 0, 255, 255 );
  rect ( 0, 450, 450, 50 );
  fill( 0 );
  text( "Current Camera: " + currentCamera.getString( "Unknown" ), 50, 485 );
}
void mousePressed(){
  double mouseYNorm = height - mouseY;
  System.out.println( "(" + mouseX + ", " + mouseYNorm + ")");
  if( mouseY > 300 && mouseY < 375 ){
    if( mouseX > 0 && mouseX < 225){
      resetEncoder.setBoolean( true );
      System.out.println( "Resetting Encoder" );
    } else {
      System.out.println( 1 );
    }
  }
  if( mouseY > 375 && mouseY < 450 ){
    if( mouseX > 0 && mouseX < 225){
      System.out.println( 2 );
    } else {
      if( mouseX > 225 && mouseX < 338 ){
        if( mouseYNorm > ( -mouseX / 3 )  + 200 ){
          System.out.println( " Upper Button " );  
        } else if( mouseYNorm < ( -mouseX / 3 )  + 200 && mouseYNorm > ( mouseX / 3 ) - 25 ){
          System.out.println( " Middle Button " );  
        } else {
          System.out.println( "Lower" );
        }
      } else {
        if( mouseYNorm > ( mouseX / 3 )  - 25 ){
          System.out.println( " Upper Button " );  
        } else if( mouseYNorm < ( mouseX / 3 )  - 25 && mouseYNorm > ( -mouseX / 3 ) + 200 ){
          System.out.println( " Middle Button " );  
        } else {
          System.out.println( "Lower" );
        }
      }
    }
  }
}
