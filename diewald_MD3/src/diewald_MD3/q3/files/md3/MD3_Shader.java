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


public final class MD3_Shader implements MD3_Logger{
  public final MD3_Surface parent_surface;
  public static boolean LOG_CONSOLE = true;
  public static boolean LOG_FILE    = !true;
  
  public    final int INDEX;
  protected final static int OBJECT_SIZE = 64 + 4;
  protected final int start_pos, end_pos;
  
  protected final static int MAX_QPATH = 64;
  
  public String STR_NAME;
  public int    S32_SHADER_INDEX;
  
  protected final Byter byter;
  
  protected MD3_Shader(MD3_Surface parent_surface,  int index){

    this.parent_surface = parent_surface;
    this.byter = this.parent_surface.byter;
    
    INDEX = index;
    start_pos = byter.getPos();
    STR_NAME = byter.getString(0, MAX_QPATH);
//    STR_NAME = STR_NAME.substring(0, STR_NAME.indexOf(0));
    S32_SHADER_INDEX = byter.getInteger(0);
    end_pos = byter.getPos();
    
    logToConsole();
  }
  
//  private final void read(){
//  }
  
  protected final void write(){
    byter.setPos(start_pos);
    byter.setString (0, STR_NAME, MAX_QPATH);
    byter.setInteger(0, S32_SHADER_INDEX);
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
    sb.append(newline + "----------------------------<SHADER>----------------------------");
    sb.append(newline + this.parent_surface.parent_reader.file.getAbsolutePath());
    sb.append(newline + this.getClass().getName()+"["+INDEX+"]");
    sb.append(newline + "start_pos         = "+start_pos);
    sb.append(newline + "STR_NAME          = "+STR_NAME);
    sb.append(newline + "S32_SHADER_INDEX  = "+S32_SHADER_INDEX);
    sb.append(newline + "end_pos           = "+end_pos);
    sb.append(newline + "OBJECT_SIZE       = "+OBJECT_SIZE);
    sb.append(newline + "----------------------------</SHADER>----------------------------");
    return sb.toString();
  }
  
}
