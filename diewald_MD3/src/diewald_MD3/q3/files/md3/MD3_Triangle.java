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


public final class MD3_Triangle implements MD3_Logger{
  public final MD3_Surface parent_surface;
  public static boolean LOG_CONSOLE = true;
  public static boolean LOG_FILE    = !true;
  
  public    final int INDEX;
  protected final int start_pos, end_pos;
  protected final static int OBJECT_SIZE = 3*4;

  public final int S32_3_INDEXES[] = new int[3];
  protected final Byter byter;
  
  protected MD3_Triangle(MD3_Surface parent_surface, int index) throws Exception{
    
    this.parent_surface = parent_surface;
    this.byter = this.parent_surface.byter;
    INDEX = index;
    
    start_pos = byter.getPos();
    int[] tmp= byter.getInt3(S32_3_INDEXES, S32_3_INDEXES.length);
    S32_3_INDEXES[0] = tmp[0];
    S32_3_INDEXES[1] = tmp[1];
    S32_3_INDEXES[2] = tmp[2];
    end_pos = byter.getPos();
    
    logToConsole();
    
    int number_of_vertices = this.parent_surface.S32_NUM_VERTS;
    if( S32_3_INDEXES[0] < 0 || S32_3_INDEXES[0] > number_of_vertices || 
        S32_3_INDEXES[1] < 0 || S32_3_INDEXES[1] > number_of_vertices ||
        S32_3_INDEXES[2] < 0 || S32_3_INDEXES[2] > number_of_vertices
        )
      throw new Exception("(!CORRPUT MD3!) invalid triangle indices = "+  S32_3_INDEXES[0] +", "+S32_3_INDEXES[1] +", "+S32_3_INDEXES[2] +" (vertice-count = "+number_of_vertices+")");
   
    
  }
  
//  private final void read(){
//
//  }
  
  protected final void write(){
    byter.setPos(start_pos);
    byter.setInt3(S32_3_INDEXES, S32_3_INDEXES.length);
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
    sb.append(newline + "----------------------------<TRIANGLE>----------------------------");
    sb.append(newline + this.parent_surface.parent_reader.file.getAbsolutePath());
    sb.append(newline + this.getClass().getName()+"["+INDEX+"]");
    sb.append(newline + "start_pos         = "+start_pos);
    sb.append(newline + "S32_3_INDEXES     = "+S32_3_INDEXES[0]+", "+S32_3_INDEXES[1]+", "+S32_3_INDEXES[2]);
    sb.append(newline + "end_pos           = "+end_pos);
    sb.append(newline + "OBJECT_SIZE       = "+OBJECT_SIZE);
    sb.append(newline + "----------------------------</TRIANGLE>----------------------------");
    return sb.toString();
  }

}
