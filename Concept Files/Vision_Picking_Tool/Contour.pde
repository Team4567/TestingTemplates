public class Contour{
  public int centerX,centerY;
  public boolean isClosest;
  Contour(int x,int y){
    centerX=x;
    centerY=y;
  }
  public double distanceToCenter(){
    return (double)Math.hypot((centerX-(width/2)),(centerY-(height/2)));
  }
  public void draw(){
    if(isClosest){
      fill(0,255,0);
    }else{
      fill(255,0,0);
    }
    ellipse(centerX,centerY,5,5);
   }
   public void setClosest(boolean c){
    isClosest=c;
  }
}
