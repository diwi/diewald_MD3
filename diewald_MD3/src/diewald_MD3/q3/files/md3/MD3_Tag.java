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

import processing.core.PMatrix3D;


public final class MD3_Tag implements MD3_Logger{
  
  public final MD3_TagFrame parent_tagframe;
  
  public static boolean LOG_CONSOLE = true;
  public static boolean LOG_FILE    = !true;
  
  public    final int INDEX;
  protected final static int OBJECT_SIZE = 64 + 3*4+3*3*4;
  
  protected final int start_pos, end_pos;
  protected final static int MAX_QPATH = 64;
  
  public String         STR_NAME;
  public final float[]   VEC3_ORIGIN   = new float[3];
  public final float[][] VEC3_3_AXIS   = new float[3][3];
  
  protected final Byter byter;
  
  private PMatrix3D mat4x4;
  private PMatrix3D mat4x4_inv;
  
  
  protected MD3_Tag(MD3_TagFrame parent_tagframe,  int index){
    this.parent_tagframe = parent_tagframe;
    this.byter = this.parent_tagframe.byter;
    
    INDEX = index;
    
    start_pos = byter.getPos();
    STR_NAME = byter.getString(0, MAX_QPATH);
    STR_NAME = STR_NAME.substring(0, STR_NAME.indexOf(0));
  
    float[] tmp = byter.getFloat3(VEC3_ORIGIN, VEC3_ORIGIN.length);
    VEC3_ORIGIN[0] = tmp[0];
    VEC3_ORIGIN[1] = tmp[1];
    VEC3_ORIGIN[2] = tmp[2];

    VEC3_3_AXIS[0] = byter.getFloat3(VEC3_3_AXIS[0], VEC3_3_AXIS[0].length);
    VEC3_3_AXIS[1] = byter.getFloat3(VEC3_3_AXIS[1], VEC3_3_AXIS[1].length);
    VEC3_3_AXIS[2] = byter.getFloat3(VEC3_3_AXIS[2], VEC3_3_AXIS[2].length);
    end_pos = byter.getPos();
    
    
    mat4x4 = new PMatrix3D
    (
        VEC3_3_AXIS[0][0], VEC3_3_AXIS[1][0], VEC3_3_AXIS[2][0], VEC3_ORIGIN[0],
        VEC3_3_AXIS[0][1], VEC3_3_AXIS[1][1], VEC3_3_AXIS[2][1], VEC3_ORIGIN[1],
        VEC3_3_AXIS[0][2], VEC3_3_AXIS[1][2], VEC3_3_AXIS[2][2], VEC3_ORIGIN[2],
                 0,          0,          0,       1
    );
    
    mat4x4_inv = new PMatrix3D(mat4x4);
    mat4x4_inv.invert();
    
    
    
//    if( parent_tagframe.INDEX < 5)
      logToConsole();
  }
  
  

  
//  private final void read(){
//  }
  
  
  
  public final PMatrix3D getMatrix(){
//    if( mat4x4 == null ){
//      mat4x4 = new PMatrix3D
//      (
//          VEC3_3_AXIS[0][0], VEC3_3_AXIS[1][0], VEC3_3_AXIS[2][0], VEC3_ORIGIN[0],
//          VEC3_3_AXIS[0][1], VEC3_3_AXIS[1][1], VEC3_3_AXIS[2][1], VEC3_ORIGIN[1],
//          VEC3_3_AXIS[0][2], VEC3_3_AXIS[1][2], VEC3_3_AXIS[2][2], VEC3_ORIGIN[2],
//                   0,          0,          0,       1
//      );
//    }
 
    return mat4x4;
  }
  
  public final PMatrix3D getMatrixInverted(){
//    if( mat4x4_inv == null){
//      mat4x4_inv = new PMatrix3D( getMatrix() );
//      mat4x4_inv.invert();
//    }
    return mat4x4_inv;
  }
  
  protected final void write(){
    byter.setPos(start_pos);
    byter.setString(0, STR_NAME, MAX_QPATH);
    byter.setFloat3(VEC3_ORIGIN, VEC3_ORIGIN.length);
    byter.setFloat3(VEC3_3_AXIS[0], VEC3_3_AXIS[0].length);
    byter.setFloat3(VEC3_3_AXIS[1], VEC3_3_AXIS[1].length);
    byter.setFloat3(VEC3_3_AXIS[2], VEC3_3_AXIS[2].length);
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
    sb.append(newline + "----------------------------<TAG>----------------------------");
    sb.append(newline + this.parent_tagframe.parent_reader.file.getAbsolutePath());
    sb.append(newline + this.getClass().getName()+"["+INDEX+"] - on frame["+this.parent_tagframe.INDEX+"]");
    sb.append(newline + "start_pos         = "+start_pos);
    sb.append(newline + "STR_NAME          = "+STR_NAME);
    sb.append(newline + "VEC3_ORIGIN       = "+VEC3_ORIGIN[0]+", "+VEC3_ORIGIN[1]+", "+VEC3_ORIGIN[2]);
    sb.append(newline + "VEC3_3_AXIS[0]    = "+VEC3_3_AXIS[0][0]+", "+VEC3_3_AXIS[0][1]+", "+VEC3_3_AXIS[0][2]);
    sb.append(newline + "VEC3_3_AXIS[1]    = "+VEC3_3_AXIS[1][0]+", "+VEC3_3_AXIS[1][1]+", "+VEC3_3_AXIS[1][2]);
    sb.append(newline + "VEC3_3_AXIS[2]    = "+VEC3_3_AXIS[2][0]+", "+VEC3_3_AXIS[2][1]+", "+VEC3_3_AXIS[2][2]);
    sb.append(newline + "end_pos           = "+end_pos);
    sb.append(newline + "OBJECT_SIZE       = "+OBJECT_SIZE);
    sb.append(newline + "----------------------------</TAG>----------------------------");
    return sb.toString();
  }

  
}
