
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import java.awt.Image;
import javax.imageio.ImageIO;
import java.awt.Graphics;
import javax.swing.JPanel;
import java.awt.Point;

public class Canvas extends JPanel {
  public Idx idx;
  private int offset_x=0;
  private int offset_y=0;

  public Canvas(int width, int height){
    this.idx = new Idx(width, height);
    this.redraw();
  }

  public Canvas(int x, int y, int width, int height){
    this.idx = new Idx(width, height);
    this.offset_x = x;
    this.offset_y = y;
    this.redraw();
  }

  public Canvas( Idx inp ){
    this.idx = inp;
    this.redraw();
  }

  public Canvas( Idx inpIdx, int dx, int dy ){
    this.idx = inpIdx;
    this.offset_x = dx;
    this.offset_y = dy;
    this.redraw();
  }

  public int getWidth(){
    return this.idx.getWidth();
  }

  public int getHeight(){
    return this.idx.getHeight();
  }

  public void setPos(int dx, int dy){
    this.setBounds(this.offset_x = dx, this.offset_y = dy, this.getWidth(), this.getHeight());
  }

  public void setPos( Point p ){
    this.setPos(p.x,p.y);
  }

  public int getX(){
    return this.offset_x;
  }

  public int getY(){
    return this.offset_y;
  }

  public Point getPos(){
    return new Point(this.offset_x, this.offset_y);
  }

  public void redraw(){
    this.setBounds(0,0,0,0);
    this.setBounds(this.offset_x, this.offset_y, this.getWidth(), this.getHeight());
  }

  public void redraw( boolean req){
    if(req){
      this.reRender = true;
    }
    this.setBounds(0,0,0,0);
    this.setBounds(this.offset_x, this.offset_y, this.getWidth(), this.getHeight());
  }

  private boolean reRender = true;
  public void paint(Graphics g){
    g.drawImage( this.idx.getImage(true), 0, 0, this);
    /*
    //g.drawImage( this.img, 0, 0, this);
    if(this.reRender){
      this.reRender = false;
      g.drawImage( this.idx.getImage(true), 0, 0, this);
    }else{
      g.drawImage( this.idx.getImage(), 0, 0, this);
    }
    */
  }
}

class Line{
  Point pos;
  int step = 0;
  double fac = 0;
  double x = 1;
  double y = 1;
  double px,py;
  double i = 0;

  public Line(Point p1, Point p2){
    //Point v = new Point(p2.x-p1.x, p2.y-p1.y );
    double vx = p2.x-p1.x;
    double vy = p2.y-p1.y;
    if(vy < 0){
      this.y = -1;
      vy*=-1;
    }
    if(vx < 0){
      this.x = -1;
      vx*=-1;
    }
    if(vx > vy){
      this.i = vx;
      this.y *= vy/ vx;
    }else{
      this.i = vy;
      this.x *= vx/vy;
    }
    this.px = p1.x;
    this.py = p1.y;
    this.pos = new Point(p1.x, p1.y);
    //try out if this does work
    //this.pos = p1;
  }

  public Line(Point p2){
    Point p1 = new Point(0,0);
    //Point v = new Point(p2.x-p1.x, p2.y-p1.y );
    double vx = p2.x-p1.x;
    double vy = p2.y-p1.y;
    if(vy < 0){
      this.y = -1;
      vy*=-1;
    }
    if(vx < 0){
      this.x = -1;
      vx*=-1;
    }
    if(vx > vy){
      this.i = vx;
      this.y *= vy/ vx;
    }else{
      this.i = vy;
      this.x *= vx/vy;
    }
    this.px = p1.x;
    this.py = p1.y;
    this.pos = new Point(p1.x, p1.y);
    //try out if this does work
    //this.pos = p1;
  }

  public boolean next(){
    if(this.step > this.i){
      return false;
    }
    //factor implementation probably lossy
    this.fac = this.step/this.i;
    this.px+= this.x;
    this.py+= this.y;
    this.step++;
    this.pos.setLocation(this.px, this.py);
    return true;
  }
}

class Idx{
  private int width;
  private int height;
  public int[] data;
  Image img;

  public Idx(){
    this.width= 1;
    this.height=1;
    this.data = new int[4];
    int i=0;
    this.data[i++]=0;
    this.data[i++]=0;
    this.data[i++]=0;
    this.data[i]=255;
  }

  public Idx(int sx){
    this.width = sx;
    this.height= sx;
    this.data = new int[ this.width*this.height*4];
    int i=0;
    while(i< this.data.length){
      this.data[i++]=0;
      this.data[i++]=0;
      this.data[i++]=0;
      this.data[i++]=255;
    }
  }

  public Idx(int sx, int sy){
    this.width = sx;
    this.height= sy;
    this.data = new int[ this.width*this.height*4];
    int i=0;
    while(i< this.data.length){
      this.data[i++]=0;
      this.data[i++]=0;
      this.data[i++]=0;
      this.data[i++]=255;
    }
  }

  public Idx(BufferedImage img){
    this.width = img.getWidth();
    this.height = img.getHeight();
    this.data = new int[ this.width*this.height*4];
    int x,y;
    int p,a,r,g,b;
    int m = 0;
    for(y=0;y<this.height;y++){
      for(x=0;x<this.width;x++){
        p = img.getRGB(x,y);
        a = (p>>24) & 0xff;
        r = (p>>16) & 0xff;
        g = (p>>8) & 0xff;
        b = p & 0xff;
        this.data[m++] =r;
        this.data[m++] =g;
        this.data[m++] =b;
        this.data[m++] =a;
      }
    }
  }

  public Idx(String imageFilePath){
    //if closeApp == true, the application will shut down,
    //in case image-file / image-data cannot be received
    boolean closeApp=false;
    try{
      File a = new File(imageFilePath);
      BufferedImage b = ImageIO.read( a );

      this.fromBufferedImage( b );
      // this.fromBufferedImage( ImageIO.read( a ) );
    }catch (IOException e){
      System.out.print("image ["+ imageFilePath +"] not found\n");
      if(closeApp){
        System.exit(0);
      }else{
        this.width= 1;
        this.height=1;
        this.data = new int[4];
        int i=0;
        this.data[i++]=0;
        this.data[i++]=0;
        this.data[i++]=0;
        this.data[i]=255;
      }
    }
  }

  public Idx(String imageFilePath, boolean closeApp ){
    //if ( input parameter ) closeApp == true, the application will shut down,
    //in case image-file / image-data cannot be received

    try{
      File a = new File(imageFilePath);
      BufferedImage b = ImageIO.read( a );

      this.fromBufferedImage( b );
      // this.fromBufferedImage( ImageIO.read( a ) );
    }catch (IOException e){
      System.out.print("image ["+ imageFilePath +"] not found\n");
      if(closeApp){
        System.exit(0);
      }else{
        this.width= 1;
        this.height=1;
        this.data = new int[4];
        int i=0;
        this.data[i++]=0;
        this.data[i++]=0;
        this.data[i++]=0;
        this.data[i]=255;
      }
    }
  }

  public int getWidth(){
    return this.width;
  }

  public int getHeight(){
    return this.height;
  }

  public int mx(int x, int y){
    x = Math.min(x,this.width);
    y = Math.min(y,this.height);
    if(x<0)x=0;
    if(y<0)y=0;
    return (x+ y*this.width )*4;
  }

  public int mx(double x, double y){
    return this.mx( (int)x, (int)y );
  }
  public int mx(Point xy){
    return this.mx( xy.x, xy.y );
  }
  public int mx(Point a, Point b, double i){
    //definitly looks lossy -> Crap
    double x = a.x*(1- i ) + b.x*i;
    double y = a.y*(1- i ) + b.y*i;
    return this.mx( x, y );
  }

  public int co(double x, double y){
    if(x<0 || x > 1 || y<0 || y> 1){
      return 0;
    }
    return this.mx( (int)(x*this.width), (int)(y*this.height) );
  }
  public int co(Point xy){
    return this.co(xy.x, xy.y);
  }

  public Pixel getChoordPixel(double x, double y){
    int m = 4*(((int)(this.width*x))%this.width + (((int)(this.height*y))%this.height)*this.width);
    return new Pixel( this.data[m++], this.data[m++], this.data[m]);
  }

  public Pixel getPixel(int x, int y){
    Pixel p = new Pixel();
    int m = this.mx(x,y);
    p.r = this.data[m++];
    p.g = this.data[m++];
    p.b = this.data[m++];
    return p;
  }

  public Pixel getPixel(double x, double y){
    Pixel p = new Pixel();
    int m = this.mx(x,y);
    p.r = this.data[m++];
    p.g = this.data[m++];
    p.b = this.data[m++];
    return p;
  }

  public Pixel getPixel(int m){
    Pixel p = new Pixel();
    p.r = this.data[m++];
    p.g = this.data[m++];
    p.b = this.data[m++];
    return p;
  }

  public Pixel getPixel(Point xy){
    Pixel p = new Pixel();
    int m = this.mx(xy);
    p.r = this.data[m++];
    p.g = this.data[m++];
    p.b = this.data[m++];
    return p;
  }

  void setPixel(int x, int y, Pixel p ){
    int m = this.mx(x,y);
    this.data[m++] = p.r;
    this.data[m++] = p.g;
    this.data[m++] = p.b;
  }

  void setPixel(double x, double y, Pixel p ){
    int m = this.mx(x,y);
    this.data[m++] = p.r;
    this.data[m++] = p.g;
    this.data[m++] = p.b;
  }

  void setPixel(Point a, Pixel p ){
    int m = this.mx(a);
    this.data[m++] = p.r;
    this.data[m++] = p.g;
    this.data[m++] = p.b;
  }

  void setPixel(int m, Pixel p ){
    this.data[m++] = p.r;
    this.data[m++] = p.g;
    this.data[m++] = p.b;
  }

  void drawLine(Point a, Point b, Pixel col){
    Line l = new Line( a, b);
    while(l.next()){
      this.setPixel(l.pos, col);
    }
  }

  void drawLine(double[] x, double[] y, Pixel col){
    Point a = new Point( (int)x[0], (int)x[1]);
    Point b = new Point( (int)y[0], (int)y[1]);
    Line l = new Line( a, b);
    while(l.next()){
      this.setPixel(l.pos, col);
    }
  }

  void drawPath(Point[] a, Pixel col, boolean closePath){
    int len = a.length;
    int n=1;
    if(closePath) n=0;
    for(int id=0;id< len - n;){
      Line l = new Line( a[id++], a[ id%len] );
      while(l.next()){
        this.setPixel(l.pos, col);
      }
    }
  }

  Point[] getLinePoints(Point a, Point b){
    Line l = new Line( a, b);
    int len = (int)(l.i);
    Point[] ray = new Point[len];
    ray[l.step] = a;
    while(l.next()){
      ray[l.step] = l.pos;
    }
    return ray;
  }

  public Idx getRect(int x, int y, int sx, int sy){
    Idx n = new Idx(sx, sy);
    int i,j;
    int m=0;
    int p;
    for(j=y; j< y+sy; j++){
      for(i=x; i<x+sx;i++){
        p = this.mx(i,j);
        n.data[m++] += this.data[p++];
        n.data[m++] += this.data[p++];
        n.data[m++] += this.data[p++];
        n.data[m++] = this.data[p++];
      }
    }
    return n;
  }

  //BLEND MODES !
  public void putIdx( Idx in, int lx, int ly){
    int ix,iy;
    int m=0;
    int nx =0;
    for(iy=0;iy<in.height;iy++){
      for(ix=0;ix<in.width;ix++){
        m = this.mx(ix+lx,iy+ly);
        nx = (ix+iy*in.width)*4;
        this.data[m++] = in.data[nx++];
        this.data[m++] = in.data[nx++];
        this.data[m++] = in.data[nx++];
        this.data[m++] = in.data[nx++];
      }
    }
  }

  public void putIdx( Idx in ){
    this.putIdx(in,0,0);
  }

  public void fromBufferedImage(BufferedImage img){
    this.width = img.getWidth();
    this.height = img.getHeight();
    this.data = new int[ this.width*this.height*4];
    int x,y;
    int p,a,r,g,b;
    int m = 0;
    for(y=0;y<this.height;y++){
      for(x=0;x<this.width;x++){
        p = img.getRGB(x,y);
        a = (p>>24) & 0xff;
        r = (p>>16) & 0xff;
        g = (p>>8) & 0xff;
        b = p & 0xff;
        this.data[m++] =r;
        this.data[m++] =g;
        this.data[m++] =b;
        this.data[m++] =a;
      }
    }
  }

  public void fromBufferedImage(String imageFilePath){
    //if closeApp == true, the application will shut down,
    //in case image-file / image-data cannot be received
    boolean closeApp=false;
    try{
      File a = new File(imageFilePath);
      BufferedImage b = ImageIO.read( a );

      this.fromBufferedImage( b );
      // this.fromBufferedImage( ImageIO.read( a ) );
    }catch (IOException e){
      System.out.print("image ["+ imageFilePath +"] not found\n");
      if(closeApp){
        System.exit(0);
      }else{
        this.width= 1;
        this.height=1;
        this.data = new int[4];
        int i=0;
        this.data[i++]=0;
        this.data[i++]=0;
        this.data[i++]=0;
        this.data[i]=255;
      }
    }
  }

  public void fromBufferedImage(String imageFilePath, boolean closeApp){
    try{
      File a = new File(imageFilePath);
      BufferedImage b = ImageIO.read( a );

      this.fromBufferedImage( b );
      // this.fromBufferedImage( ImageIO.read( a ) );
    }catch (IOException e){
      System.out.print("image ["+ imageFilePath +"] not found\n");
      if(closeApp){
        System.exit(0);
      }else{
        this.width= 1;
        this.height=1;
        this.data = new int[4];
        int i=0;
        this.data[i++]=0;
        this.data[i++]=0;
        this.data[i++]=0;
        this.data[i]=255;
      }
    }
  }

  public Image getImage(){
    if(this.img == null){
      this.img = this.toImage();
    }

    return this.img;
  }

  public Image getImage(boolean req){
    if(this.img == null || req){
      this.img = this.toImage();
    }

    return this.img;
  }

  public BufferedImage toImage(){
    BufferedImage bufferedImage = new BufferedImage(this.width,this.height,BufferedImage.TYPE_INT_RGB);
    int x;
    int y;
    int r,g,b;
    int m = 0;
    for(y=0; y<this.height; y++){
      for(x=0; x<this.width; x++){
        r = this.data[m++];
        g = this.data[m++];
        b = this.data[m++];
        m++;
        bufferedImage.setRGB(x,y, irgb(r,g,b));
      }
    }
    return bufferedImage;
  }

  public Canvas toCanvas(){
    return new Canvas(this);
  }

  public Canvas toCanvas(int dx, int dy){
    return new Canvas(this, dx, dy);
  }

  int irgb(int r, int g, int b){
    return ((r&0x0ff)<<16)|((g&0x0ff)<<8)|(b&0x0ff);
  }
}
