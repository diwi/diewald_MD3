//--------------------------------------------------------
//
// author: thomas diewald
// date:   05.12.2011
// 
//--------------------------------------------------------
// description:
// loading a quake3-player and quake3-weapon by loading 
// md3-files and building a custom nodesystem.
//--------------------------------------------------------
// interaction:
// 1) peasycam controls
// 2) view animation : press any key and move mouse in X-direction
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

//  Q3_Player player;
//  Q3_Weapon weapon;
Q3_TexturePool texture_pool;

//player
Q3_Object head, upper, lower;
NodeState n_head, n_upper, n_lower;


//weapon
Q3_Object hand, weapon, barrel, flash;
NodeState n_hand, n_weapon, n_barrel, n_flash;

int frame_IDX = 0;

public void setup() {
  int setup_timer = millis();
  size(800, 800, OPENGL);
  initPeasyCam();

  // some console-logging settings, ... useful for debugging
  Skin_File.LOG_CONSOLE = false;
  AnimationCfg.LOG_CONSOLE = false;
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

  // make a new texturepool
  // if no texture pool is used, it can happen that the same texture 
  // gets loaded multiple times, depending on the model
  texture_pool = new Q3_TexturePool(this);


  String path_player = sketchPath+ "/../models_Q3DEMO/players/sarge";
  String path_weapon = sketchPath+ "/../models_Q3DEMO/weapons2/rocketl";

  //loading player parts
  head = Q3_Object.create(this, path_player, "head.md3", texture_pool);
  head.setTexture( Skin_File.Load(path_player, "head_default.skin"));

  upper = Q3_Object.create(this, path_player, "upper.md3", texture_pool);
  upper.setTexture( Skin_File.Load(path_player, "upper_default.skin"));

  lower = Q3_Object.create(this, path_player, "lower.md3", texture_pool);
  lower.setTexture( Skin_File.Load(path_player, "lower_default.skin"));


  //loading weapon parts
  hand = Q3_Object.create(this, path_weapon, "rocketl_hand.md3", texture_pool);
  hand.setTexture( Skin_File.Load(path_weapon, "rocketl_hand.skin"));

  weapon = Q3_Object.create(this, path_weapon, "rocketl.md3", texture_pool);
  weapon.setTexture( Skin_File.Load(path_weapon, "rocketl.skin"));

  flash = Q3_Object.create(this, path_weapon, "rocketl_flash.md3", texture_pool);
  flash.setTexture( Skin_File.Load(path_weapon, "rocketl_flash.skin"));


  //making nodes for player
  n_lower  = new NodeState(lower );
  n_upper  = new NodeState(upper );
  n_head   = new NodeState(head  );

  //making nodes for weapon
  n_hand   = new NodeState( hand   );
  n_weapon = new NodeState( weapon );
  n_flash  = new NodeState( flash  );


  // making the node tree (node connections)
  // the nodes are connected by given tag-names
  // e.g. tag_torso", is a given tag, inside the md3-files, lower.md3 and 
  // upper.md3, to make a connection for both objects.
  n_lower  .setParentNode(null, null  );          
  n_upper  .setParentNode(n_lower, "tag_torso"  );
  n_head   .setParentNode(n_upper, "tag_head"   );

  n_hand   .setParentNode(n_upper, "tag_weapon");
  n_weapon .setParentNode(n_hand, "tag_weapon" );
  n_flash  .setParentNode(n_weapon, "tag_flash"  );

  // n_lower will be the parent node,... all other node-transformation will be affected by this
  n_lower.setAsTopLevelNode();


  // display-style settings
  n_lower.setNodeSystemDisplayStyle( new DefaultDisplayStyle(this));
  n_lower.getDisplayStyle().styleShaded()     .display(true);
  n_lower.getDisplayStyle().styleNormals()    .display(false);
  n_lower.getDisplayStyle().styleBoundingbox().display(false);
  n_lower.getDisplayStyle().styleVertices()   .display(false);
  n_lower.getDisplayStyle().styleWireframe()  .display(false);
  n_lower.getDisplayStyle().styleTags()       .display(false);

  println("FINISHED SETUP: " + (millis() - setup_timer) + " ms");
}

public void draw() {

  if ( keyPressed ) {
    int dif = (mouseX-pmouseX)/2;
    if ( dif < 0) frame_IDX--;
    if ( dif > 0) frame_IDX++;
    if ( frame_IDX < 0 ) frame_IDX = 0;
    println("frame_IDX = "+frame_IDX);
  } 

  background(255);

  scale(10);

  drawGrid(100, 10, 1);
  bks(10, 2);
  translate(0, 0, 25);

  n_lower.updateNodeSystem(frame_IDX).drawNodeSystem();
  // println(frameRate);
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

// ----------------------------------------------------------------------------------------------------
void initPeasyCam() {
  cam = new PeasyCam(this, 0, 0, 0, 1000);
  cam.setMinimumDistance(1);
  cam.setMaximumDistance(100000);

  cam.lookAt(16.837477, 193.56741, 406.83347);
  cam.setRotations(1.4167833, 0.48761615, -3.1252024);
  cam.setDistance(900, 0);
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
