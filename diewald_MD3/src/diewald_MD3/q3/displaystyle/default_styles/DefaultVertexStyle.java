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

import diewald_MD3.q3.displaystyle.interfaces.VertexStyle;
import processing.core.PApplet;

public class DefaultVertexStyle implements VertexStyle{
  
  protected PApplet parent_P5;
  public int   color;
  public int   thickness;
  
  public boolean display = true;
  
  public DefaultVertexStyle(PApplet parent_P5){
    this.parent_P5 = parent_P5;
    color = parent_P5.color(255, 155, 0);
    thickness = 5;
  }

  @Override
  public int getColor() {
    return color;
  }

  @Override
  public int getThickness() {
    return thickness;
  }


  @Override
  public boolean display() {
    return display;
  }  
  @Override
  public void display(boolean display) {
    this.display = display;
  }
  
  @Override
  public void setColor(int color) {
    this.color = color;
  }

  @Override
  public void setThickness(int thickness) {
    this.thickness = thickness;
  }

  
}
