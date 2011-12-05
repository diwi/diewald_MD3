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

import diewald_MD3.q3.Q3_Texture;



public final class MD3_Surface implements MD3_Logger{
  
  public final MD3_File parent_reader;
  
  public static boolean LOG_CONSOLE = true;
  public static boolean LOG_FILE    = !true;
  
  public    final int INDEX;
  protected final int start_pos, end_pos;
  protected final static int MAX_QPATH         = 64;   
  protected final static int MD3_MAX_SHADERS   = 256;
  protected final static int MD3_MAX_VERTS     = 4096;
  protected final static int MD3_MAX_TRIANGLES = 8192;

  protected final static int OBJECT_SIZE = 11*4+64;
  
  public    final int S32_IDENT;
  public    String    STR_IDENT;
  public    String    STR_NAME;
  protected final int S32_FLAGS;
  public    final int S32_NUM_FRAMES;
  public    final int S32_NUM_SHADERS;
  public    final int S32_NUM_VERTS;
  public    final int S32_NUM_TRIANGLES;
  protected final int S32_OFS_TRIANGLES;
  protected final int S32_OFS_SHADERS;
  protected final int S32_OFS_ST;
  protected final int S32_OFS_XYZNORMAL;
  protected final int S32_OFS_END;
  
  
  public final MD3_Shader    shaders[];
  public final MD3_Triangle  triangles[];
  public final MD3_TexCoord  tex_coords[];
  public final MD3_SurfaceFrame surface_frames[];
  
  private Q3_Texture texture;
  
  protected final Byter byter;
  
  protected final int pos_shaders;
  protected final int pos_triangles;
  protected final int pos_texcoords;
  protected final int pos_xyznormals;
  
  
  protected MD3_Surface( MD3_File parent_reader, int index) throws Exception{
    this.parent_reader = parent_reader;
    this.byter = this.parent_reader.byter;
    INDEX = index;
    
    start_pos = byter.getPos();
    S32_IDENT         = byter.getInteger(0);
    STR_IDENT         = byter.backward(4).getString(0, 4);
    byter.byte_reading_counter -=4;
    STR_NAME          = byter.getString(0, MAX_QPATH);              
    STR_NAME = STR_NAME.substring(0, STR_NAME.indexOf(0)); 
    S32_FLAGS         = byter.getInteger(0);
    S32_NUM_FRAMES    = byter.getInteger(0);
    S32_NUM_SHADERS   = byter.getInteger(0);
    S32_NUM_VERTS     = byter.getInteger(0);
    S32_NUM_TRIANGLES = byter.getInteger(0);
    S32_OFS_TRIANGLES = byter.getInteger(0);
    S32_OFS_SHADERS   = byter.getInteger(0);
    S32_OFS_ST        = byter.getInteger(0);
    S32_OFS_XYZNORMAL = byter.getInteger(0);
    S32_OFS_END       = byter.getInteger(0);
    end_pos = byter.getPos();
    
    logToConsole();
    
    if( S32_NUM_FRAMES !=   this.parent_reader.header.S32_NUM_FRAMES  )  throw new Exception("(!CORRPUT MD3!) number of frames incorrect = "+  S32_NUM_FRAMES);
    if( S32_NUM_SHADERS   < 0 | S32_NUM_SHADERS   > MD3_MAX_SHADERS   )  throw new Exception("(!CORRPUT MD3!) S32_NUM_SHADERS = "+  S32_NUM_SHADERS  +" ... min/max = 0/"+MD3_MAX_SHADERS);
    if( S32_NUM_VERTS     < 0 | S32_NUM_VERTS     > MD3_MAX_VERTS     )  throw new Exception("(!CORRPUT MD3!) S32_NUM_VERTS = "+    S32_NUM_VERTS    +" ... min/max = 0/"+MD3_MAX_VERTS);
    if( S32_NUM_TRIANGLES < 0 | S32_NUM_TRIANGLES > MD3_MAX_TRIANGLES )  throw new Exception("(!CORRPUT MD3!) S32_NUM_TRIANGLES = "+S32_NUM_TRIANGLES+" ... min/max = 0/"+MD3_MAX_TRIANGLES);

    
    pos_shaders    = start_pos+S32_OFS_SHADERS;
    pos_triangles  = start_pos+S32_OFS_TRIANGLES;
    pos_texcoords  = start_pos+S32_OFS_ST;
    pos_xyznormals = start_pos+S32_OFS_XYZNORMAL;
    
    
    byter.setPos(pos_shaders);
    shaders = new MD3_Shader[S32_NUM_SHADERS];
    for(int i = 0; i < S32_NUM_SHADERS; i++)
      shaders[i] = new MD3_Shader(this, i);
    

    byter.setPos(pos_triangles);
    triangles = new MD3_Triangle[S32_NUM_TRIANGLES];
    for(int i = 0; i < S32_NUM_TRIANGLES; i++)
      triangles[i] = new MD3_Triangle(this, i);
    

    byter.setPos(pos_texcoords);
    tex_coords = new MD3_TexCoord[S32_NUM_VERTS];
    for(int i = 0; i < S32_NUM_VERTS; i++)
      tex_coords[i] = new MD3_TexCoord(this, i);
    

    byter.setPos(pos_xyznormals);
    surface_frames = new MD3_SurfaceFrame[S32_NUM_FRAMES];
    for(int i = 0; i < S32_NUM_FRAMES; i++)
      surface_frames[i] = new MD3_SurfaceFrame(this, i);
      
    if( (S32_OFS_END+start_pos) != byter.getPos()){
      throw new Exception("(!CORRPUT MD3!): surface size (number of bytes) incorrect");
    }

  }
  
//  private final void read(){
//    
//  }
  
  
  public final Q3_Texture getTexture(){
    return texture;
  }
  public final void setTexture( Q3_Texture texture){
    this.texture = texture;
  }
  

  
  protected final void write(){
    byter.setPos(start_pos);
    byter.setInteger(0, S32_IDENT           );
    byter.setString (0, STR_NAME, MAX_QPATH );              
    byter.setInteger(0, S32_FLAGS           );
    byter.setInteger(0, S32_NUM_FRAMES      );
    byter.setInteger(0, S32_NUM_SHADERS     );
    byter.setInteger(0, S32_NUM_VERTS       );
    byter.setInteger(0, S32_NUM_TRIANGLES   );
    byter.setInteger(0, S32_OFS_TRIANGLES   );
    byter.setInteger(0, S32_OFS_SHADERS     );
    byter.setInteger(0, S32_OFS_ST          );
    byter.setInteger(0, S32_OFS_XYZNORMAL   );
    byter.setInteger(0, S32_OFS_END         );
    byter.setPos(end_pos); 
    
    byter.setPos(pos_shaders);
    for(int i = 0; i < S32_NUM_SHADERS; i++)
      shaders[i].write();

    byter.setPos(pos_triangles);
    for(int i = 0; i < S32_NUM_TRIANGLES; i++)
      triangles[i].write();

    byter.setPos(pos_texcoords);
    for(int i = 0; i < S32_NUM_VERTS; i++)
      tex_coords[i].write();
    
    byter.setPos(pos_xyznormals);
    for(int i = 0; i < S32_NUM_FRAMES; i++)
      surface_frames[i].write();
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
    for(int i = 0; i < S32_NUM_SHADERS; i++)
      shaders[i].logToFile(out);

    for(int i = 0; i < S32_NUM_TRIANGLES; i++)
      triangles[i].logToFile(out);

    for(int i = 0; i < S32_NUM_VERTS; i++)
      tex_coords[i].logToFile(out);

    for(int i = 0; i < S32_NUM_FRAMES; i++)
      surface_frames[i].logToFile(out);
  }
  
  @Override
  public final String dataAsString(){
    StringBuilder sb = new StringBuilder();
    sb.append(newline + "----------------------------<SURFACE>----------------------------");
    sb.append(newline + this.parent_reader.file.getAbsolutePath());
    sb.append(newline + this.getClass().getName()+"["+INDEX+"]");
    sb.append(newline + "start_pos         = "+start_pos);
    sb.append(newline + "S32_IDENT         = "+S32_IDENT);
    sb.append(newline + "STR_IDENT         = "+STR_IDENT);
    sb.append(newline + "STR_NAME          = "+STR_NAME);
    sb.append(newline + "S32_FLAGS         = "+S32_FLAGS        );
    sb.append(newline + "S32_NUM_FRAMES    = "+S32_NUM_FRAMES   );
    sb.append(newline + "S32_NUM_SHADERS   = "+S32_NUM_SHADERS  );
    sb.append(newline + "S32_NUM_VERTS     = "+S32_NUM_VERTS    );
    sb.append(newline + "S32_NUM_TRIANGLES = "+S32_NUM_TRIANGLES);
    sb.append(newline + "S32_OFS_TRIANGLES = "+S32_OFS_TRIANGLES);
    sb.append(newline + "S32_OFS_SHADERS   = "+S32_OFS_SHADERS  );
    sb.append(newline + "S32_OFS_ST        = "+S32_OFS_ST       );
    sb.append(newline + "S32_OFS_XYZNORMAL = "+S32_OFS_XYZNORMAL);
    sb.append(newline + "S32_OFS_END       = "+S32_OFS_END      );
    sb.append(newline + "OBJECT_SIZE       = "+OBJECT_SIZE);
    sb.append(newline + "end_pos           = "+end_pos);
    sb.append(newline + "----------------------------</SURFACE>----------------------------");
    return sb.toString();
  }
}




