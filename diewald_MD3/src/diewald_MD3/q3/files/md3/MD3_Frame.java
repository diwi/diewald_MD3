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


public final class MD3_Frame implements MD3_Logger{
  
  public final MD3_File parent_reader;
  
  public static boolean LOG_CONSOLE = true;
  public static boolean LOG_FILE    = !true;
  
  public    final int INDEX;
  protected final int        start_pos, end_pos;
  protected final static int OBJECT_SIZE = 3*4+3*4+3*4+4+16;
  protected final int        STR_NAME_MAX_LENGTH = 16;
  
  
  public float[]  VEC3_MIN_BOUNDS   = new float[3];
  public float[]  VEC3_MAX_BOUNDS   = new float[3];
  public float[]  VEC3_LOCAL_ORIGIN = new float[3];
  public float    F32_RADIUS;
  public String   STR_NAME;
  
  protected final Byter byter;
  

  
  protected MD3_Frame(MD3_File parent_reader, int index){
    
    this.parent_reader = parent_reader;
    this.byter = this.parent_reader.byter;
    INDEX = index;
    
    
    start_pos = byter.getPos();
    VEC3_MIN_BOUNDS   = byter.getFloat3(VEC3_MIN_BOUNDS, VEC3_MIN_BOUNDS.length);
    VEC3_MAX_BOUNDS   = byter.getFloat3(VEC3_MAX_BOUNDS, VEC3_MAX_BOUNDS.length);
    VEC3_LOCAL_ORIGIN = byter.getFloat3(VEC3_LOCAL_ORIGIN, VEC3_LOCAL_ORIGIN.length);
 
    F32_RADIUS = byter.getFloat(0);
    STR_NAME = byter.getString(0, STR_NAME_MAX_LENGTH);
    STR_NAME = STR_NAME.substring(0, STR_NAME.indexOf(0));
    end_pos = byter.getPos();
    
//    if( INDEX < 5)
    logToConsole();
  }
  
//  private final void read(){
//
//  }
  
  protected final void write(){
    byter.setPos(start_pos);
    byter.setFloat3(VEC3_MIN_BOUNDS, VEC3_MIN_BOUNDS.length);
    byter.setFloat3(VEC3_MAX_BOUNDS, VEC3_MAX_BOUNDS.length);
    byter.setFloat3(VEC3_LOCAL_ORIGIN, VEC3_LOCAL_ORIGIN.length);
 
    byter.setFloat(0, F32_RADIUS);
    byter.setString(0, STR_NAME, STR_NAME_MAX_LENGTH);
    byter.setPos(end_pos); 
  }
  
  
  @Override
  public boolean LOG_FILE() {
    return LOG_FILE;
  }

  @Override
  public boolean LOG_CONSOLE() {
    return LOG_CONSOLE;
  }


  @Override
  public void logToConsole() {
    if( !LOG_CONSOLE ) return;
    System.out.println(dataAsString());
  }

  @Override
  public void logToFile(BufferedWriter out) {
    if( !LOG_FILE || out == null) return;
    try {
      out.write( dataAsString() );
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  @Override
  public final String dataAsString(){
    
    float bb_size[] = new float[3];
    bb_size[0] = VEC3_MAX_BOUNDS[0] - VEC3_MIN_BOUNDS[0];
    bb_size[1] = VEC3_MAX_BOUNDS[1] - VEC3_MIN_BOUNDS[1];
    bb_size[2] = VEC3_MAX_BOUNDS[2] - VEC3_MIN_BOUNDS[2];
    
    StringBuilder sb = new StringBuilder();
    sb.append(newline + "----------------------------<FRAME>----------------------------");
    sb.append(newline + this.parent_reader.file.getAbsolutePath());
    sb.append(newline + this.getClass().getName()+"["+INDEX+"]");
    sb.append(newline + "start_pos         = "+start_pos);
    sb.append(newline + "VEC3_MIN_BOUNDS   = "+VEC3_MIN_BOUNDS[0]+", "+VEC3_MIN_BOUNDS[1]+", "+VEC3_MIN_BOUNDS[2]);
    sb.append(newline + "VEC3_MAX_BOUNDS   = "+VEC3_MAX_BOUNDS[0]+", "+VEC3_MAX_BOUNDS[1]+", "+VEC3_MAX_BOUNDS[2]);
    sb.append(newline + "(bb_size           = " +bb_size[0]  +", "+bb_size[1]   +", "+ bb_size[2]  +")");    
    sb.append(newline + "VEC3_LOCAL_ORIGIN = "+VEC3_LOCAL_ORIGIN[0]+", "+VEC3_LOCAL_ORIGIN[1]+", "+VEC3_LOCAL_ORIGIN[2]);
    sb.append(newline + "F32_RADIUS        = "+F32_RADIUS);
    sb.append(newline + "STR_NAME          = "+STR_NAME);
    sb.append(newline + "end_pos           = "+end_pos);
    sb.append(newline + "OBJECT_SIZE       = "+OBJECT_SIZE);
    sb.append(newline + "----------------------------</FRAME>----------------------------");
    return sb.toString();
  }

}
