//--------------------------------------------------------
//
// author: thomas diewald
// date:   05.12.2011
// 
//--------------------------------------------------------
// description:
// loading a MD3-File which has to be fixed.
// the model "tomturbo.md3" is not part of Quake3, but its a model of mine.
//--------------------------------------------------------
// interaction:
// 1) peasycam controls
//--------------------------------------------------------

import processing.opengl.*;
import peasy.*;

import diewald_MD3.q3.*;
import diewald_MD3.q3.displaystyle.interfaces.*;
import diewald_MD3.q3.constants.*;
import diewald_MD3.q3.tools.*;
import diewald_MD3.q3.files.md3.*;
import diewald_MD3.q3.displaystyle.default_styles.*;
import diewald_MD3.q3.files.skin.*;
import diewald_MD3.q3.files.animation.*;

PeasyCam cam;

Q3_Object md3;

public void setup() {
  int setup_timer = millis();
  size(800, 800, OPENGL);
  initPeasyCam();
  println("FINISHED SETUP: " + (millis() - setup_timer) + " ms");

  MD3_Header       .LOG_CONSOLE = !true;
  MD3_Frame        .LOG_CONSOLE = !true;
  MD3_Tag          .LOG_CONSOLE = !true;
  MD3_Surface      .LOG_CONSOLE = !true;
  MD3_SurfaceFrame .LOG_CONSOLE = !true;
  MD3_TagFrame     .LOG_CONSOLE = !true;
  MD3_Shader       .LOG_CONSOLE = !true;
  MD3_Triangle     .LOG_CONSOLE = !true;
  MD3_TexCoord     .LOG_CONSOLE = !true;
  MD3_XYZnormal    .LOG_CONSOLE = !true;


  String path = sketchPath+"/../tomturbo/models/players/heads/tomturbo";
  String filename = "tomturbo.md3";


  md3 = Q3_Object.create(this, path, filename);
  md3.setTexture( Skin_File.Load(path, "head_default.skin"));
  md3.invertTextureUV(false, false);


  MD3_File tomturbo_file = md3.getMD3_File();
  MD3Tool.setHeaderName  ( tomturbo_file, "diewald_tomturbo" );
  MD3Tool.repairFrameData( tomturbo_file );
  MD3Tool.repairNormals  ( tomturbo_file, VertexNormalMode.WEIGHT_BY_ANGLE );


  tomturbo_file.saveCopy();
}

public void draw() {
  background(255);
  scale(20);

  drawGrid(100, 10, 1);
  bks(10, 2);


  translate(10, 10, 10);

  int frame_IDX = 0;



  md3.drawBKS(10);

  //draw boundingbox
  strokeWeight(1);
  stroke(0);
  md3.drawBoundingbox(frame_IDX);

  //draw nomals
  strokeWeight(1);
  stroke(20, 0, 120);
  md3.drawNormals(frame_IDX, 2);

  //draw surface - shaded
  md3.drawSurfaceShaded(frame_IDX);

  //draw surface - vertices
  strokeWeight(5);
  stroke(255, 155, 0);
  md3.drawSurfaceVertices(frame_IDX);

  //draw surface - wireframe
  strokeWeight(2);
  stroke(100);
  md3.drawSurfaceWireFrame(frame_IDX);

  //draw tags
  md3.drawTags(frame_IDX, 30);


  // println(frameRate);
}

public void bks(int s, int stroke_weight) {
  pushStyle();
  strokeWeight(stroke_weight);
  stroke(255, 0, 0); 
  line(0, 0, 0, s, 0, 0);
  stroke(0, 255, 0); 
  line(0, 0, 0, 0, s, 0);
  stroke(0, 0, 255); 
  line(0, 0, 0, 0, 0, s);
  popStyle();
}

public void drawGrid(int size, int grid_gap, int stroke_weight) {
  pushStyle();
  pushMatrix();
  translate(-size / 2, -size / 2, 0);
  fill(180);
  stroke(150, 0, 0);
  strokeWeight(stroke_weight);

  int i;
  for (i = 0; i < size; i += grid_gap) {
    line(0, i, size, i);
    line(i, 0, i, size);
  }
  line(0, i, size, i);
  line(i, 0, i, size);
  popMatrix();
  popStyle();
}

// ----------------------------------------------------------------------------------------------------
void initPeasyCam() {
  cam = new PeasyCam(this, 0, 0, 0, 1000);
  cam.setMinimumDistance(1);
  cam.setMaximumDistance(100000);

  cam.lookAt(107.79008, 192.91653, 392.28955);
  cam.setRotations(1.1659623, 0.94084644, -2.80176);
  cam.setDistance(630.291055670475, 0);
}

void printlnCam() {
  float  rot[] = cam.getRotations();
  float  pos[] = cam.getPosition();
  float  lat[] = cam.getLookAt();
  double dis   = cam.getDistance();
  println("rot = "+ rot[0]+", "+rot[1] +", "+rot[2]);
  println("pos = "+ pos[0]+", "+pos[1] +", "+pos[2]);
  println("lat = "+ lat[0]+", "+lat[1] +", "+lat[2]);
  println("dis = "+ dis);
}


public void keyReleased() {
  if ( key == 'p') printlnCam();
}

