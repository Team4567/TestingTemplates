//Nonworking, experimental code to determine which vision target(contour) is closest to the center of the camera screen to select it as the target.
public ArrayList<Contour> contours= new ArrayList<Contour>();
public Contour a= new Contour((int)random(0,256),(int)random(0,144));
public Contour b= new Contour((int)random(0,256),(int)random(0,144));
public Contour c= new Contour((int)random(0,256),(int)random(0,144));
public Contour ClosestC;
public boolean isFinished=false;
void setup(){
size(256,144);
background(255);
contours.add(a);
contours.add(b);
contours.add(c);
}
void runCheck(){

for(int i=0; i < contours.size();i++){
    if(i-1<0){
         if(contours.get(i).distanceToCenter()>contours.get(contours.size()-1).distanceToCenter()){
        contours.get(contours.size()-1).setClosest(true);
        contours.get(i).setClosest(false);
         ClosestC=contours.get(contours.size()-1);
         
      } else{
        contours.get(i).setClosest(true);
        contours.get(contours.size()-1).setClosest(false);
        ClosestC=contours.get(i);
        
      } 
      } else {
        if(contours.get(i).distanceToCenter()>contours.get(i-1).distanceToCenter()){
        contours.get(i-1).setClosest(true);
        contours.get(i).setClosest(false);
        ClosestC=contours.get(i-1);
        
      } else{
        contours.get(i).setClosest(true);
        contours.get(i-1).setClosest(false);
        ClosestC=contours.get(i);
        
      } 
    
    }
  }
  

}
void draw(){
  fill(0);
  ellipse(width/2,height/2,10,10);
  a.draw();
  b.draw();
  c.draw();
  runCheck();
}
void mousePressed(){


  println(a.isClosest+" "+a.distanceToCenter());
  println(b.isClosest+" "+b.distanceToCenter());
  println(c.isClosest+" "+c.distanceToCenter());
}
