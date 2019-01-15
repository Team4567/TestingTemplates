//WORKING.
//Once CenterC is found, all commands to align robot will base off of that.
public ArrayList<Contour> contours= new ArrayList<Contour>();
// The reason these are definted outside of setup is because otherwise contours.size() gave nullpointerexception
public Contour a= new Contour((int)random(0,256),(int)random(0,144));
public Contour b= new Contour((int)random(0,256),(int)random(0,144));
public Contour c= new Contour((int)random(0,256),(int)random(0,144));
public Contour ClosestC;
public double min;
public int minIndex;
public boolean isDone;
void setup(){
  size(256,144);
  background(255);
// Having contours be automatically made into classes and added to the array will have to be tested in a real environment
  contours.add(a);
  contours.add(b);
  contours.add(c);
  min = 999999999; //The largest # I could fit in a double
  minIndex = -1;
  isDone=true;

}
void runCheck(){
  //isDone makes it happen forever
  if(isDone){
    isDone=false;
    for (int i=0; i<contours.size(); i++){
     if (contours.get(i).distanceToCenter() < min){
        min = contours.get(i).distanceToCenter();
        minIndex = i;
     }
     isDone=true;
    }
  }
}
void draw(){
  runCheck();
  for(int i=0; i<contours.size();i++){
    if(i==minIndex){
      ClosestC=contours.get(i);
      contours.get(i).setClosest(true);
    } else{
      contours.get(i).setClosest(false);
    }
  }
  a.draw();
  b.draw();
  c.draw();
  fill(0);
  ellipse(width/2,height/2,10,10);
  
  
}
void mousePressed(){
  println(a.isClosest);
  println(b.isClosest);
  println(c.isClosest);
}
