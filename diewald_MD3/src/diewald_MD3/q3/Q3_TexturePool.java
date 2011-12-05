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
import java.util.ArrayList;

import processing.core.PApplet;

public final class Q3_TexturePool {
  private PApplet parent_p5;
  private final ArrayList<Q3_Texture> texture_pool = new ArrayList<Q3_Texture>();
  
  public Q3_TexturePool(PApplet parent_p5){
    this.parent_p5 = parent_p5;
  }
  
  
  public final Q3_Texture getTexture(File texture_path){
    if( texture_path == null )
      return null;
    for( Q3_Texture tex : texture_pool ){
      if ( tex.compareTo(texture_path) == 0){
        return tex;
      }
    }
    return null;
  }
  
  public final Q3_Texture getTexture(Q3_Texture texture){
    if( texture == null )
      return null;
    for( Q3_Texture tex : texture_pool ){
      if ( tex.compareTo(texture) == 0){
        return tex;
      }
    }
    return null;
  }
  
  public final Q3_Texture addTexture(File texture_path){
    Q3_Texture tex = getTexture(texture_path);
    
    if( tex == null ){
      try {
        tex = new Q3_Texture(parent_p5, texture_path);
        texture_pool.add( tex );
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return tex;
  }
  
  
  public final Q3_Texture addTexture(Q3_Texture texture){
    if( texture == null )
      return null;
    Q3_Texture tex = getTexture( texture );
    if( tex == null ){
      texture_pool.add( texture );
      return texture;
    }
    return tex;
  }
  
  public final Q3_Texture removeTexture(Q3_Texture texture){
    Q3_Texture tex = getTexture(texture);
    texture_pool.remove(tex);
    return tex;
  }
  
  public final Q3_Texture removeTexture(File texture_path){
    return removeTexture (getTexture(texture_path));
  }
  
  
  public final ArrayList<Q3_Texture> getTextureList(){
    return this.texture_pool;
  }
 
}
