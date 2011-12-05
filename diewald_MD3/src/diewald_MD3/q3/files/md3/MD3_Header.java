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


public final class MD3_Header implements MD3_Logger{
  
  public final MD3_File parent_reader;
  public final static String MAGIC_NUMBER = "IDP3";
  public static boolean LOG_CONSOLE = true;
  public static boolean LOG_FILE    = !true;
  
  
  protected final int start_pos, end_pos;
  protected final static int MAX_QPATH = 64;
  protected final static int MAX_IDENT = 4;
  protected final static int MD3_MAX_FRAMES   = 1024;
  protected final static int MD3_MAX_TAGS     = 16;
  protected final static int MD3_MAX_SURFACES = 32;


  public final int S32_IDENT;
  public String    STR_IDENT;
  public final int S32_VERSION;
  
  public      String  STR_NAME;
  protected final int S32_FLAGS; // U8_NAME_cstr + S32_FLAGS is 68 byte, 
  public    final int S32_NUM_FRAMES;
  public    final int S32_NUM_TAGS;
  public    final int S32_NUM_SURFACES;
  public    final int S32_NUM_SKINS;
  protected final int S32_OFS_FRAMES;
  protected final int S32_OFS_TAGS;
  protected final int S32_OFS_SURFACES;
  protected final int S32_OFS_EOF;
  
  protected final Byter byter;
  
  protected MD3_Header(MD3_File parent_reader ) throws Exception{
    
    this.parent_reader = parent_reader;
    this.byter = this.parent_reader.byter;
    
    start_pos = byter.getPos();
    S32_IDENT = byter.getInteger(0);
    STR_IDENT = byter.backward(4).getString(0, 4);
    byter.byte_reading_counter -=4;
    
    if( !STR_IDENT.equals(MAGIC_NUMBER))
      throw new Exception("(!CORRPUT MD3!) MAGIC_NUMBER incorrect:\""+MAGIC_NUMBER +"\" != \""+STR_IDENT+"\"");

    S32_VERSION = byter.getInteger(0);

    STR_NAME = byter.getString(0, MAX_QPATH);
    //STR_NAME = STR_NAME.substring(0, STR_NAME.indexOf(0));
    
    S32_FLAGS         = byter.getInteger(0);
    S32_NUM_FRAMES    = byter.getInteger(0);
    S32_NUM_TAGS      = byter.getInteger(0);
    S32_NUM_SURFACES  = byter.getInteger(0);
    S32_NUM_SKINS     = byter.getInteger(0);
    S32_OFS_FRAMES    = byter.getInteger(0);
    S32_OFS_TAGS      = byter.getInteger(0);
    S32_OFS_SURFACES  = byter.getInteger(0);
    S32_OFS_EOF       = byter.getInteger(0);
    end_pos = byter.getPos();
    
    logToConsole();
    
    if( S32_NUM_FRAMES   < 1 | S32_NUM_FRAMES   > MD3_MAX_FRAMES)  throw new Exception("(!CORRPUT MD3!) S32_NUM_FRAMES = "+  S32_NUM_FRAMES  +" ... min/max = 1/"+MD3_MAX_FRAMES);
    if( S32_NUM_TAGS     < 0 | S32_NUM_TAGS     > MD3_MAX_TAGS)    throw new Exception("(!CORRPUT MD3!) S32_NUM_TAGS = "+    S32_NUM_TAGS    +" ... min/max = 0/"+MD3_MAX_TAGS);
    if( S32_NUM_SURFACES < 0 | S32_NUM_SURFACES > MD3_MAX_SURFACES)throw new Exception("(!CORRPUT MD3!) S32_NUM_SURFACES = "+S32_NUM_SURFACES+" ... min/max = 0/"+MD3_MAX_SURFACES);
    
    if( S32_OFS_EOF != byter.totalSize())
      throw new Exception("(!CORRPUT MD3!) FILE_SIZE incorrect: expected '"+S32_OFS_EOF +" bytes', but got '"+byter.totalSize()+" bytes'");
  }
  
//  private final void read(){
//   
//  }
  
  protected final void write(){
    byter.setPos(start_pos);
    byter.setInteger(0, S32_IDENT);
    byter.setInteger(0, S32_VERSION);
    byter.setString(0, STR_NAME, MAX_QPATH);
    byter.setInteger(0, S32_FLAGS         );
    byter.setInteger(0, S32_NUM_FRAMES    );
    byter.setInteger(0, S32_NUM_TAGS      );
    byter.setInteger(0, S32_NUM_SURFACES  );
    byter.setInteger(0, S32_NUM_SKINS     );
    byter.setInteger(0, S32_OFS_FRAMES    );
    byter.setInteger(0, S32_OFS_TAGS      );
    byter.setInteger(0, S32_OFS_SURFACES  );
    byter.setInteger(0, S32_OFS_EOF       );
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
    StringBuilder sb = new StringBuilder();
    sb.append(newline + "----------------------------<HEADER>----------------------------");
    sb.append(newline + this.parent_reader.file.getAbsolutePath());
    sb.append(newline + "start_pos        = "+start_pos);
    sb.append(newline + "S32_IDENT        = "+S32_IDENT);
    sb.append(newline + "STR_IDENT        = "+STR_IDENT);
    sb.append(newline + "S32_VERSION      = "+S32_VERSION);
    sb.append(newline + "STR_NAME         = "+STR_NAME);
    sb.append(newline + "S32_FLAGS        = "+S32_FLAGS);
    sb.append(newline + "S32_NUM_FRAMES   = "+S32_NUM_FRAMES);
    sb.append(newline + "S32_NUM_TAGS     = "+S32_NUM_TAGS);
    sb.append(newline + "S32_NUM_SURFACES = "+S32_NUM_SURFACES);
    sb.append(newline + "S32_NUM_SKINS    = "+S32_NUM_SKINS);
    sb.append(newline + "S32_OFS_FRAMES   = "+S32_OFS_FRAMES);
    sb.append(newline + "S32_OFS_TAGS     = "+S32_OFS_TAGS);
    sb.append(newline + "S32_OFS_SURFACES = "+S32_OFS_SURFACES);
    sb.append(newline + "S32_OFS_EOF      = "+S32_OFS_EOF);
    sb.append(newline + "end_pos          = "+end_pos);
    sb.append(newline + "----------------------------</HEADER>----------------------------");
    return sb.toString();
  }
  
}
