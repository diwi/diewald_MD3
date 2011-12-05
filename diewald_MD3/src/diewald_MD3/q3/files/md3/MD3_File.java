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


import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteOrder;




/**
 * java, MD3-Reader
 * 
 * information:
 * 
 * QUAKE3 SOURCE:
 * quake3-1.32b\code\bspc\q3files.h
 * quake3-1.32b\q3map\misc_model.c
 * 
 * WEB:
 * http://en.wikipedia.org/wiki/MD3_%28file_format%29
 * http://icculus.org/homepages/phaethon/q3/formats/
 * http://icculus.org/homepages/phaethon/q3/formats/md2-schoenblum.html
 * http://icculus.org/homepages/phaethon/q3/formats/md3format.html
 * http://icculus.org/homepages/phaethon/q3/formats/md4format.html
 * 
 * 
 * @author thomas diewald
 *
 */

public final class MD3_File {

  public final File file;
  
  protected Byter byter = null;
  public    ByteOrder byte_order = ByteOrder.LITTLE_ENDIAN;
  
  public MD3_Header   header;
  public MD3_Frame    frames[];
  public MD3_TagFrame tag_frames[];
  public MD3_Surface  surfaces[];
  
  public boolean LOADED = false;
  
  public MD3_File(String path, String filename){;
    file = new File(path, filename);
  }
  
  public final void load(){
    try {
      FileInputStream fis = new FileInputStream(file);
      BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
      byte data[] = new byte[bis.available()];
      bis.read(data);
      byter = new Byter(data); 
      bis.close();
      fis.close();
      
      
      readFromByter();
      LOADED = true;
      
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  
  
  private final void readFromByter() throws Exception{
    long time = System.currentTimeMillis();
    int file_size = byter.available();
    
    if( file_size <= 0)
      throw new Exception("(!CORRPUT MD3!): file_size = "+file_size);

    byter.byte_order = this.byte_order;
  
    header = new MD3_Header(this);
    
    byter.setPos(header.S32_OFS_FRAMES);
    frames = new MD3_Frame[header.S32_NUM_FRAMES];
    for(int i = 0; i < frames.length; i++){
      frames[i] = new MD3_Frame(this, i);
    }
    
    byter.setPos(header.S32_OFS_TAGS);
    tag_frames = new MD3_TagFrame[header.S32_NUM_FRAMES];
    for(int i = 0; i < tag_frames.length; i++){
      tag_frames[i] = new MD3_TagFrame(this, i);
    }
    
    byter.setPos(header.S32_OFS_SURFACES);
    surfaces = new MD3_Surface[header.S32_NUM_SURFACES];
    for(int i = 0; i < surfaces.length; i++){
      surfaces[i] = new MD3_Surface(this, i);
    }
    
    
    System.out.println(">> loaded MD3-File ("+(System.currentTimeMillis() - time)+" ms): "+file);
    
    if( file_size != byter.byte_reading_counter){
      System.out.println(">> WARNING: bytes left: "+(file_size-byter.byte_reading_counter));
      System.out.println(">> WARNING: file may be corrupt");
      throw new Exception("(!CORRPUT MD3!)");
    }
    if( header.S32_OFS_EOF != byter.getPos() ){
      System.out.println(">> WARNING: not at OFS_EOF"+(header.S32_OFS_EOF- byter.getPos()));
      System.out.println(">> WARNING: file may be corrupt");
      throw new Exception("(!CORRPUT MD3!)");
    }
    
  }
  
  
  public final void saveAs(String file_name, boolean overwrite) {
//    private final void saveToByter() throws Exception{
    
    File file2save = new File(file.getParentFile(), file_name);
    
    if( !file2save.canWrite() ){
      System.err.println("cannot write to file: "+file2save.getAbsolutePath());
      return;
    }
    
    if( !overwrite && file2save.compareTo(file ) == 0){
      file2save = new File(file.getParentFile(), "COPY_"+file_name);
    }
    
    try{
      FileOutputStream fos = new FileOutputStream(file2save);
      saveToByter();
      fos.write(byter.getBytes());
      fos.close();
     
    } catch(Exception e){
     e.printStackTrace();
    }
  }
  
  public final void saveCopy() {
    saveAs(file.getName(), false);
  }
  
  public final void save() {
    saveAs(file.getName(), true);
  }
  
  

  
  
  private final void saveToByter() throws Exception{
    long time = System.currentTimeMillis();
    
    int file_size = byter.totalSize();
    
    if( file_size <= 0)
      throw new Exception("(!CORRPUT MD3!): file_size = "+file_size);

    byter.byte_order = this.byte_order;
    byter.setPos(0);
    header.write();

    
    for(int i = 0; i < frames.length; i++){
      frames[i].write();
    }
  
    for(int i = 0; i < tag_frames.length; i++){
      tag_frames[i].write();
    }
    
    for(int i = 0; i < surfaces.length; i++){
      surfaces[i].write();
    }
    
    System.out.println(">> saved MD3-File ("+(System.currentTimeMillis() - time)+" ms): "+file);
    
    if( file_size != byter.byte_reading_counter){
      System.out.println(">> WARNING: bytes left: "+(file_size-byter.byte_reading_counter));
      System.out.println(">> WARNING: file may be corrupt");
      throw new Exception("(!CORRPUT MD3!)");
    }
    if( header.S32_OFS_EOF != byter.getPos() ){
      System.out.println(">> WARNING: not at OFS_EOF"+(header.S32_OFS_EOF- byter.getPos()));
      System.out.println(">> WARNING: file may be corrupt");
      throw new Exception("(!CORRPUT MD3!)");
    }
    
    LOADED = true;
  }
  
  
  
  

  public final void createLogFile(){
    try {
      File MD3_FILE_LOG = new File(file.getAbsolutePath()+".txt");
      FileWriter writer = new FileWriter(MD3_FILE_LOG);
      BufferedWriter out = new BufferedWriter(writer);

      header.logToFile(out);
      
      for(int i = 0; i < frames.length; i++)
        frames[i].logToFile(out);
      
      for(int i = 0; i < tag_frames.length; i++)
        tag_frames[i].logToFile(out);
      
      for(int i = 0; i < surfaces.length; i++)
        surfaces[i].logToFile(out);
      
      out.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  public static final void logConsoleAll(boolean state){
    MD3_Header   .LOG_CONSOLE = state;
    MD3_Frame    .LOG_CONSOLE = state;
    MD3_Tag      .LOG_CONSOLE = state;
    MD3_Surface  .LOG_CONSOLE = state;
    MD3_Shader   .LOG_CONSOLE = state;
    MD3_Triangle .LOG_CONSOLE = state;
    MD3_TexCoord .LOG_CONSOLE = state;
    MD3_XYZnormal.LOG_CONSOLE = state;
  }
  
  public static final void logFileAll(boolean state){
    MD3_Header   .LOG_FILE = state;
    MD3_Frame    .LOG_FILE = state;
    MD3_Tag      .LOG_FILE = state;
    MD3_Surface  .LOG_FILE = state;
    MD3_Shader   .LOG_FILE = state;
    MD3_Triangle .LOG_FILE = state;
    MD3_TexCoord .LOG_FILE = state;
    MD3_XYZnormal.LOG_FILE = state;
  }
  
  
  
  

  
  
  
  
  
  
  
  
  
  
  

}
