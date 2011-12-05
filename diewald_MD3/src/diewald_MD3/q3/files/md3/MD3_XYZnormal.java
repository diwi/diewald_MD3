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


public final class MD3_XYZnormal implements MD3_Logger{
  public final MD3_SurfaceFrame parent_surfaceframe;

  public static boolean LOG_CONSOLE = true;
  public static boolean LOG_FILE    = !true;
  
  public    final int INDEX;
  protected final int start_pos, end_pos;
  protected final static int OBJECT_SIZE = 3*2 + 2;

  protected short S16_3_XYZ[] = new short[3];
  protected short S16_3_norm;
  
  public float F32_3_XYZ[] = new float[3];
  public float F32_3_XYZ_NORM[] = new float[3];
  
  protected final Byter byter;
  
  private final static float  MD3_XYZ_SCALE      = 1f/64;
  private final static float  MD3_XYZ_SCALE_BACK = 64;
  private final static double TWO_PI             = Math.PI*2; 
  private final static double TWO_PI_Div0xFF     = TWO_PI/0xFF; 
  private final static double TWO_PI_Div0xFFinv  = 1/TWO_PI_Div0xFF; 
  private double x,y,z, l;
  private double lng, lat;
  
  
  protected MD3_XYZnormal(MD3_SurfaceFrame parent_surfaceframe, int index){
    this.parent_surfaceframe = parent_surfaceframe;
    this.byter = this.parent_surfaceframe.byter;
    INDEX = index;
    
    start_pos  = byter.getPos();
    S16_3_XYZ  = byter.getShort3(S16_3_XYZ, S16_3_XYZ.length);
    S16_3_norm = byter.getShort(0);
    end_pos    = byter.getPos();
   
    
    
    // prepare vertices as float-array
    F32_3_XYZ[0] = (S16_3_XYZ[0]*MD3_XYZ_SCALE);
    F32_3_XYZ[1] = (S16_3_XYZ[1]*MD3_XYZ_SCALE);
    F32_3_XYZ[2] = (S16_3_XYZ[2]*MD3_XYZ_SCALE);
    
    
    
    // get normal vector
//    lat <- ((normal shift-right 8) binary-and 255) * (2 * pi ) / 255
//    lng <- (normal binary-and 255) * (2 * pi) / 255
//    x <- cos ( lat ) * sin ( lng )
//    y <- sin ( lat ) * sin ( lng )
//    z <- cos ( lng )
    lat = ((S16_3_norm >> 8) & 0xFF) * TWO_PI_Div0xFF;
    lng = ((S16_3_norm >> 0 )& 0xFF) * TWO_PI_Div0xFF;
    F32_3_XYZ_NORM[0] = (float)(Math.cos ( lat ) * Math.sin ( lng ));
    F32_3_XYZ_NORM[1] = (float)(Math.sin ( lat ) * Math.sin ( lng ));
    F32_3_XYZ_NORM[2] = (float)(Math.cos ( lng ));
    
    logToConsole();
  }
  
//  private final void read(){
//  }
//  
  
  
  

  protected final void write(){


    // scale vertex back, in case they were modified
    S16_3_XYZ[0] = (short) (F32_3_XYZ[0] * MD3_XYZ_SCALE_BACK) ;
    S16_3_XYZ[1] = (short) (F32_3_XYZ[1] * MD3_XYZ_SCALE_BACK) ;
    S16_3_XYZ[2] = (short) (F32_3_XYZ[2] * MD3_XYZ_SCALE_BACK) ;
   
    
    //normalize normal
    x = F32_3_XYZ_NORM[0];
    y = F32_3_XYZ_NORM[1];
    z = F32_3_XYZ_NORM[2];
    l = Math.sqrt(x*x + y*y + z*z);
    x /= l;
    y /= l;
    z /= l;
  
    
//    make lng/lat form normal
//    lng <- atan2 ( y / x) * 255 / (2 * pi)
//    lat <- acos ( z )     * 255 / (2 * pi)
//    normal <- (lat shift-left 8) binary-or (lng)
   
    lng = (Math.atan2(y, x) * TWO_PI_Div0xFFinv);
    lat = (Math.acos (   z) * TWO_PI_Div0xFFinv);
    S16_3_norm = (short) (((byte)lat << 8) | (byte)lng);
  
    // write to byter
    byter.setPos(start_pos);
    byter.setShort3(S16_3_XYZ, S16_3_XYZ.length);
    byter.setShort(0, S16_3_norm);
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
    sb.append(newline + "----------------------------<XYZnormal>----------------------------");
    sb.append(newline + this.parent_surfaceframe.parent_surface.parent_reader.file.getAbsolutePath());
    sb.append(newline + this.getClass().getName()+"["+INDEX+"] - on frame["+this.parent_surfaceframe.INDEX+"]");
    sb.append(newline + "INDEX             = "+INDEX);
    sb.append(newline + "start_pos         = "+start_pos);
    sb.append(newline + "S16_3_XYZ         = "+S16_3_XYZ[0]+", "+S16_3_XYZ[1]+", "+S16_3_XYZ[2]);
    sb.append(newline + "S16_3_norm        = "+S16_3_norm +" = "+F32_3_XYZ_NORM[0]+", "+F32_3_XYZ_NORM[1]+", "+F32_3_XYZ_NORM[2]);
    sb.append(newline + "end_pos           = "+end_pos);
    sb.append(newline + "OBJECT_SIZE       = "+OBJECT_SIZE);
    sb.append(newline + "----------------------------</XYZnormal>----------------------------");

    return sb.toString();
  }

  

  
}



