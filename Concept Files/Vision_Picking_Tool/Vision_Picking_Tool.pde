//Nonworking, experimental code to determine which vision target(contour) is closest to the center of the camera screen to select it as the target.

public ArrayList<Contour> contours= new ArrayList<Contour>();
public Contour a= new Contour(200,10);
public Contour b= new Contour(100,50);
public Contour c= new Contour(50,72);

public Contour ClosestC;
public int x= contours.size()+1;
void setup(){
size(256,144);
background(255);
contours.add(a);
contours.add(b);
contours.add(c);
}
void runCheck(){
  for(int i=0; i < contours.size();i++){
    x=i;
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
  if(x>contours.size()){
  runCheck();
  
  }
}
void mousePressed(){
  //println(contours.size());
  //All of these come up as false no matter what.
  println(a.isClosest);
  println(b.isClosest);
  println(c.isClosest);
}
