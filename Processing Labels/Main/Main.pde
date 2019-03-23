import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

NetworkTableInstance inst;
NetworkTable table;
NetworkTableEntry targetLock;
int checkR, checkG, checkB;
String target;
void setup(){
  size( 450, 768 );
  inst=NetworkTableInstance.getDefault();
}
void draw(){
  if( targetLock.getBoolean( false ) ){
    checkR=0;
    checkG=255;
    checkB=0;
  } else {
    checkR=255;
    checkG=0;
    checkB=0;
  }
  
}
