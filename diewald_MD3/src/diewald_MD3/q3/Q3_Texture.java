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


package diewald_MD3.q3;

import java.io.File;

import processing.core.PApplet;
import processing.core.PImage;

public class Q3_Texture implements Comparable<Q3_Texture>{
  
  private PApplet parent_P5;
  private PImage texture;
  
  private File texture_path;
  private String texture_name;
  
  private int width, height;
  
  public Q3_Texture(PApplet parent_P5, File texture_path) throws Exception{
    
    this.texture_path = texture_path;
    this.parent_P5    = parent_P5;
    
    if( this.texture_path == null ||!this.texture_path.isFile() )
      throw new Exception("(Q3_Texture) cannot find image-file: "+texture_path.getAbsolutePath() );
    
    texture_name = texture_path.getName();
    
    load();
  }
  
  
  private void load(){
    texture = parent_P5.loadImage(texture_path.getAbsolutePath());
//    texture = parent_P5.requestImage(texture_path.getAbsolutePath());
    width = texture.width;
    width = texture.height;
  }
  
  
  public PImage getTexture(){
    return texture;
  }
  
  public File getTexturePath(){
    return texture_path;
  }
  
  public final int getWidth(){
    return width;
  }
  public final int getHeight(){
    return height;
  }
  public final String getTextureName(){
    return texture_name;
  }


  @Override
  public int compareTo(Q3_Texture tex) {
    return texture_path.compareTo(tex.texture_path);
  }
  
  public int compareTo(File file_name) {
    return texture_path.compareTo(file_name);
  }
  
  
  
}
