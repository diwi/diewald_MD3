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


package diewald_MD3.q3.displaystyle.default_styles;

import diewald_MD3.q3.displaystyle.interfaces.BoundingboxStyle;
import diewald_MD3.q3.displaystyle.interfaces.DisplayStyle;
import diewald_MD3.q3.displaystyle.interfaces.NormalStyle;
import diewald_MD3.q3.displaystyle.interfaces.ShadingStyle;
import diewald_MD3.q3.displaystyle.interfaces.TagStyle;
import diewald_MD3.q3.displaystyle.interfaces.VertexStyle;
import diewald_MD3.q3.displaystyle.interfaces.WireframeStyle;
import processing.core.PApplet;

public class DefaultDisplayStyle implements DisplayStyle{

  
  public NormalStyle      style_normal;
  public ShadingStyle     style_shading;
  public TagStyle         style_tags;
  public VertexStyle      style_vertices;
  public WireframeStyle   style_wireframe;
  public BoundingboxStyle style_boundingbox;

  public DefaultDisplayStyle(PApplet parent_p5){
    style_normal       = new DefaultNormalStyle     (parent_p5);    
    style_shading      = new DefaultShadingStyle    (parent_p5);   
    style_tags         = new DefaultTagStyle        (parent_p5);      
    style_vertices     = new DefaultVertexStyle     (parent_p5);  
    style_wireframe    = new DefaultWireframeStyle  (parent_p5); 
    style_boundingbox  = new DefaultBoundingboxStyle(parent_p5); 
  }

  @Override
  public VertexStyle styleVertices(){
    return style_vertices;
  }
  @Override
  public NormalStyle styleNormals(){
    return style_normal;
  }
  @Override
  public ShadingStyle styleShaded(){
    return style_shading;
  }
  @Override
  public TagStyle styleTags(){
    return style_tags;
  }
  @Override
  public WireframeStyle styleWireframe(){
    return style_wireframe;
  }
  @Override
  public BoundingboxStyle styleBoundingbox(){
    return style_boundingbox;
  }


}
