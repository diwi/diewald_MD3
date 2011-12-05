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

import diewald_MD3.q3.files.skin.Skin_File;

import processing.core.PApplet;
//import quake3.constants.Display;

public abstract class Q3_Model {
  protected PApplet        parent_p5;
  protected File           model_dir = null;
  protected Q3_TexturePool texture_pool = null;
  protected NodeState      node_system;
  

  protected Q3_Model( PApplet parent_p5, Q3_TexturePool texture_pool){
    this.parent_p5 = parent_p5;
    this.texture_pool = texture_pool;
  }
  
  public final void setPapplet(PApplet parent_p5){
    this.parent_p5 = parent_p5;
  }
  
  protected final Q3_Object loadMD3(String path, String md3_file_name) throws Exception{
    Q3_Object obj = Q3_Object.create(parent_p5, path, md3_file_name, texture_pool );
    if( obj == null )
      throw new Exception("("+this.getClass().getName()+") couldn't create MD3-Object from "+md3_file_name);
    
    return obj;
  }

  
  protected final void loadTexturesFromSkinfile( Q3_Object md3, String path, String skinfile_name){
    if( md3 != null){
      Skin_File skin = Skin_File.Load(path, skinfile_name);
      md3.setTexture( skin );
      md3.tex_invertV = true;
    }
  }
  
  public final Q3_TexturePool getTexturePool(){
    return texture_pool;
  }
  
  
  public abstract boolean loadFromDirectory(String path);
  public abstract void makeNodeSystem();
  public abstract NodeState getNodeSystem();
  public abstract void drawModel(int frame_IDX);
}
