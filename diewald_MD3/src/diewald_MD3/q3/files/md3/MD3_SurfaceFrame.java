/**
 * 
 * diewald_MD3 v1.0
 * 
 * loading and editing quake3-Models (MD3-Files of players, weapons, ...) .
 * 
 * 
 * 
 *   (C) 2011    Thomas Diewald
 *               http://www.thomasdiewald.com
 *   
 *   last built: 12/05/2011
 *   
 *   download:   http://thomasdiewald.com/processing/libraries/diewald_MD3/
 *   source:     https://github.com/diwi/diewald_MD3 
 *   
 *   tested OS:  osx,windows
 *   processing: 1.5.1, 2.04
 *
 *
 *
 *
 * This source is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This code is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 * 
 * A copy of the GNU General Public License is available on the World
 * Wide Web at <http://www.gnu.org/copyleft/gpl.html>. You can also
 * obtain it by writing to the Free Software Foundation,
 * Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 */


package diewald_MD3.q3.files.md3;

import java.io.BufferedWriter;
import java.io.IOException;


public final class MD3_SurfaceFrame implements MD3_Logger{
  public final MD3_Surface parent_surface;
  
  public static boolean LOG_CONSOLE = true;
  public static boolean LOG_FILE    = !true;
  
  public    final int INDEX;
  protected final int start_pos, end_pos;
  
  public final MD3_XYZnormal xyz_normals[];
  
  
  public final float bb_minXYZ[] = {Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE};
  public final float bb_maxXYZ[] = {Float.MIN_VALUE, Float.MIN_VALUE, Float.MIN_VALUE};
  public final float bb_size[] = {0, 0, 0};

  protected final Byter byter;
  
  protected MD3_SurfaceFrame(MD3_Surface parent_surface, int index){
    
    this.parent_surface = parent_surface;
    this.byter = this.parent_surface.byter;
    INDEX = index;
    start_pos = byter.getPos();
    
    xyz_normals = new MD3_XYZnormal[parent_surface.S32_NUM_VERTS];
    for(int i = 0; i < xyz_normals.length; i++)
      xyz_normals[i] = new MD3_XYZnormal(this,  i);
    
    end_pos = byter.getPos();
    
    logToConsole();

    //
    recalculateBounds();
  }
  
//  private final void read(){
//  }
  
  protected final void write(){
    byter.setPos(start_pos);
    for(int i = 0; i < xyz_normals.length; i++)
      xyz_normals[i].write();
    byter.setPos(end_pos); 
  }
  
  @Override
  public final boolean LOG_FILE() {
    return LOG_FILE;
  }

  @Override
  public final boolean LOG_CONSOLE() {
    return LOG_CONSOLE;
  }


  @Override
  public final void logToConsole() {
    if( !LOG_CONSOLE ) return;
    System.out.println(dataAsString());
  }

  @Override
  public final void logToFile(BufferedWriter out) {
    if( !LOG_FILE || out == null) return;
    try {
      out.write( dataAsString() );
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  @Override
  public final String dataAsString(){
    StringBuilder sb = new StringBuilder();
    sb.append(newline + "----------------------------<SURFACE_FRAME>----------------------------");
    sb.append(newline + this.parent_surface.parent_reader.file.getAbsolutePath());
    sb.append(newline + this.getClass().getName()+"["+INDEX+"]");
    sb.append(newline + "start_pos         = "+start_pos);
    sb.append(newline + "end_pos           = "+end_pos);
    sb.append(newline + "----------------------------</SURFACE_FRAME>----------------------------");
    return sb.toString();
  }
  
  
  
  
  
  
  
  
  public void calculateBoundingBox(){
    int S32_NUM_VERTS = parent_surface.S32_NUM_VERTS;
    for(int i = 0; i < S32_NUM_VERTS; i++){
      float[] xyz = xyz_normals[i].F32_3_XYZ;
      if( bb_minXYZ[0] > xyz[0]) bb_minXYZ[0] = xyz[0];
      if( bb_minXYZ[1] > xyz[1]) bb_minXYZ[1] = xyz[1];
      if( bb_minXYZ[2] > xyz[2]) bb_minXYZ[2] = xyz[2];
                                 
      if( bb_maxXYZ[0] < xyz[0]) bb_maxXYZ[0] = xyz[0];
      if( bb_maxXYZ[1] < xyz[1]) bb_maxXYZ[1] = xyz[1];
      if( bb_maxXYZ[2] < xyz[2]) bb_maxXYZ[2] = xyz[2];
    }
    
    bb_size[0] = bb_maxXYZ[0] - bb_minXYZ[0];
    bb_size[1] = bb_maxXYZ[1] - bb_minXYZ[1];
    bb_size[2] = bb_maxXYZ[2] - bb_minXYZ[2];
  }
  
  
  
  
  
  
  

  
  // version 1 
  // center of boudningsphere = center of boundingbox
  // radius = distance to corner
  public float calculateRadius1(){
    float dx, dy, dz;
    double radius;

    dx = bb_maxXYZ[0] - bb_minXYZ[0];
    dy = bb_maxXYZ[1] - bb_minXYZ[1];
    dz = bb_maxXYZ[2] - bb_minXYZ[2];
    radius = Math.sqrt(dx*dx + dy*dy + dz*dz)/2f;
    return (float)radius;
  }
  
  
  
  
  // version 2 
  // center is at the mean position of all
  // radius = distance to vertex with the biggest distance
  public float calculateRadius2(){
    int S32_NUM_VERTS = parent_surface.S32_NUM_VERTS;
    float dx, dy, dz;
    double radius;

    float syz_center[] = new float[3];
    for(int i = 0; i < S32_NUM_VERTS; i++){
      float[] xyz = xyz_normals[i].F32_3_XYZ;
      syz_center[0] += xyz[0];
      syz_center[1] += xyz[1];
      syz_center[2] += xyz[2];
    }
    syz_center[0] /= S32_NUM_VERTS;
    syz_center[1] /= S32_NUM_VERTS;
    syz_center[2] /= S32_NUM_VERTS;
    
    radius = 0;
    for(int i = 0; i < S32_NUM_VERTS; i++){
      float[] xyz = xyz_normals[i].F32_3_XYZ;
      dx = syz_center[0] - xyz[0];
      dy = syz_center[1] - xyz[1];
      dz = syz_center[2] - xyz[2];
      double radius_tmp = Math.sqrt(dx*dx + dy*dy + dz*dz);
      if( radius < radius_tmp ) radius = radius_tmp;
    }
    return (float)radius;
  }
  
  
  
  // version 3 
  // center is at 0 0 0
  // radius = distance to vertex with the biggest distance
  public float calculateRadius3(){
    int S32_NUM_VERTS = parent_surface.S32_NUM_VERTS;
    float dx, dy, dz;
    double radius = 0;
    float syz_center[] = {0, 0, 0};
    //TODO: add real center from parent object
    for(int i = 0; i < S32_NUM_VERTS; i++){
      float[] xyz = xyz_normals[i].F32_3_XYZ;
      dx = syz_center[0] - xyz[0];
      dy = syz_center[1] - xyz[1];
      dz = syz_center[2] - xyz[2];
      double radius_tmp = Math.sqrt(dx*dx + dy*dy + dz*dz);
      if( radius < radius_tmp ) radius = radius_tmp;
    }
    return (float)radius;
  }
  
  // version 4 
  // center is center of tag
  // radius = distance to vertice with the biggest distance
  public float calculateRadius4(){
    int S32_NUM_VERTS = parent_surface.S32_NUM_VERTS;
    float dx, dy, dz;
    double radius = 0;
    MD3_Tag tag = parent_surface.parent_reader.tag_frames[INDEX].tags[0];

    float syz_center[] = tag.VEC3_ORIGIN;
    for(int i = 0; i < S32_NUM_VERTS; i++){
      float[] xyz = xyz_normals[i].F32_3_XYZ;
      dx = syz_center[0] - xyz[0];
      dy = syz_center[1] - xyz[1];
      dz = syz_center[2] - xyz[2];
      double radius_tmp = Math.sqrt(dx*dx + dy*dy + dz*dz);
      if( radius < radius_tmp ) radius = radius_tmp;
    }
    return (float)radius;
  }
  
  
  
  public void recalculateBounds(){
//    System.out.println("----------------------------<SURFACE_FRAME_BOUNDS>----------------------------");
//    System.out.println(this.getClass().getName()+"["+INDEX+"]");

    calculateBoundingBox();
    
    
//    System.out.println("minXYZ  = " +bb_minXYZ[0]+", "+bb_minXYZ[1] +", "+ bb_minXYZ[2]);
//    System.out.println("maxXYZ  = " +bb_maxXYZ[0]+", "+bb_maxXYZ[1] +", "+ bb_maxXYZ[2]);
//    System.out.println("bb_size = " +bb_size[0]  +", "+bb_size[1]   +", "+ bb_size[2]  );   
//    
//    calculateRadius1();
//    
//    calculateRadius2();
//    
//    calculateRadius3();
//    
//    calculateRadius4();
    
  }
}



