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

import diewald_MD3.q3.files.md3.MD3_File;
import diewald_MD3.q3.files.md3.MD3_Frame;
import diewald_MD3.q3.files.md3.MD3_Surface;
import diewald_MD3.q3.files.md3.MD3_Tag;
import diewald_MD3.q3.files.md3.MD3_TexCoord;
import diewald_MD3.q3.files.md3.MD3_Triangle;
import diewald_MD3.q3.files.md3.MD3_XYZnormal;
import diewald_MD3.q3.files.skin.Skin_File;


import processing.core.PApplet;
import processing.core.PConstants;

public class Q3_Object {
  
  protected PApplet parent_p5;
  private MD3_File MD3_FILE;
  private MD3_Surface[] surfaces;
  
  public boolean tex_invertV = true;
  public boolean tex_invertU = false;
  
  
  private String name = "none";
  
  private Q3_TexturePool texture_pool = null;
  
  
  public final void setName( String name){
    this.name =  name;
  }
  public final String getName(){
    return name;
  }

 
  
  protected Q3_Object(PApplet parent_p5, Q3_TexturePool texture_pool){
    this.parent_p5 = parent_p5;
    this.texture_pool = texture_pool;
  }
  
  public void setPapplet(PApplet parent_p5){
    if( parent_p5 == null )
      return;
    this.parent_p5 = parent_p5;
  }
  

  public final static Q3_Object create(PApplet parent_p5, String path, String filename){
    return create(parent_p5, path, filename, null);
  }
  
  public final static Q3_Object create(PApplet parent_p5, String path, String filename, Q3_TexturePool texture_pool){
    Q3_Object md3_tmp = new Q3_Object(parent_p5, texture_pool).FILE_READER(path, filename);
    if( !md3_tmp.MD3_FILE.LOADED )
      return null;

    return md3_tmp;
  }
  
  
  
  protected final Q3_Object FILE_READER(String path, String filename){
    MD3_FILE = new MD3_File( path, filename);
    MD3_FILE.load();
    name = MD3_FILE.file.getName();
    this.surfaces = MD3_FILE.surfaces;
    return this;
  }
  
  
  public final MD3_File getMD3_File(){
    return MD3_FILE; 
  }
  
  
  
  

  //----------------------------------------------------------------------------
  // SET TEXTURE
  //----------------------------------------------------------------------------
  
//  // texture from given path for all surfaces

  public final void setTexture(String path){
    setTexture(new File(path));
  }
  public final void setTexture(File path){
    setTexture(path, MD3_FILE.surfaces);
  }
  
//  //texture from given Q3_Texture for all surfaces
  public final void setTexture(Q3_Texture texture){
    setTexture(texture, MD3_FILE.surfaces);
  }
  
  
//  //texture from path for all index-defined surfaces
  public final void setTexture(String path, int ... surface_indices){
    setTexture(new File(path) , surface_indices);
  }
  
  //texture from Q3_Texture for all index-defined surfaces
  public final void setTexture(File texture_path, int ... surface_indices){
    if( MD3_FILE.surfaces == null ){
      return;
    }
    for(int idx : surface_indices){
      if( idx >= 0 && idx < surfaces.length)
        assignTexture(texture_path, MD3_FILE.surfaces[idx]);
    }
  }
  
  //texture from Q3_Texture for all index-defined surfaces
  public final void setTexture(Q3_Texture texture, int ... surface_indices){
    if( MD3_FILE.surfaces == null ){
      return;
    }
    for(int idx : surface_indices){
      if( idx >= 0 && idx < surfaces.length)
        assignTexture(texture, MD3_FILE.surfaces[idx]);
    }
  }
  
  //texture from path for all defined surfaces
  public final void setTexture(String path, MD3_Surface ... surfaces){
    setTexture( new File(path), surfaces );
  }
  
  //texture from PImage for all defined surfaces
  public final void setTexture(File texture_path, MD3_Surface ... surfaces){
    if( surfaces == null){
      return;
    }
    for( MD3_Surface surface : surfaces){
      MD3_Surface surface_in_list = getSurfaceByName(surface.STR_NAME);
      if( surface_in_list != null)
        assignTexture(texture_path, surface_in_list);
    }
  }
  
  //texture from PImage for all defined surfaces
  public final void setTexture(Q3_Texture texture, MD3_Surface ... surfaces){
    if(  surfaces == null){
      return;
    }
    for( MD3_Surface surface : surfaces){
      MD3_Surface surface_in_list = getSurfaceByName(surface.STR_NAME);
      if( surface_in_list != null)
        assignTexture(texture, surface_in_list);
    }
  }
  
  public final void setTexture(Skin_File skin_file){
    if( skin_file == null ){
      return;
    }
    
    for(int i = 0; i < surfaces.length; i++){
      File texture_file = skin_file.getTextureFileBySurfaceName(surfaces[i].STR_NAME);
//      System.out.println("texture_file = "+texture_file);
      //TODO: error handling!!!
      if (texture_file == null || !texture_file.exists())
        continue;
      assignTexture(texture_file, surfaces[i] );
    }
  }

  private final void assignTexture(File texture_path, MD3_Surface surface){
//    surface.texture = generateTexture(texture_path);
    surface.setTexture( generateTexture(texture_path) );
  }
  
  private final void assignTexture(Q3_Texture texture, MD3_Surface surface){
//    surface.texture = generateTexture(texture);
    surface.setTexture( generateTexture(texture) );
  }
 
  

  
  private final Q3_Texture generateTexture(File texture_path){
    Q3_Texture texture = null;
    if( texture_pool != null ){
      texture = texture_pool.addTexture(texture_path);
    } else {
      try {
        texture = new Q3_Texture(parent_p5, texture_path );
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return texture;
  }
  
  private final Q3_Texture generateTexture(Q3_Texture texture){
    if( texture_pool != null ){
      return texture_pool.addTexture(texture);
    } else {
      return texture;
    }
  }
  
  public final void invertTextureUV(boolean tex_invertU, boolean tex_invertV){
    this.tex_invertU = tex_invertU;
    this.tex_invertV = tex_invertV;
  }
  
  


  


  
  
  
  
  
  public final int getNumberOfTags(){
    if( MD3_FILE.tag_frames == null || MD3_FILE.tag_frames.length == 0)
      return 0;
    return MD3_FILE.tag_frames[0].tags.length;
  }
  public final int getNumberOfSurfaces(){
    if( surfaces == null )
      return 0;
    return surfaces.length;
  }

  public final int getNumberOfFrames(){
    if( MD3_FILE.frames == null )
      return 0;
    return MD3_FILE.frames.length;
  }

  
  public final String[] getNamesOfSurfaces(){
    int count = getNumberOfSurfaces();
    String names[] = new String[count];
    for(int i = 0; i < count; i++){
      names[i] = surfaces[i].STR_NAME; 
    }
    return names;
  }
  public final String[] getNamesOfTags(){
    int count = getNumberOfTags();
    String names[] = new String[count];
    for(int i = 0; i < count; i++){
      names[i] = MD3_FILE.tag_frames[0].tags[i].STR_NAME; 
    }
    return names;
  }
  //public final String[] getShaderNames(){
  //int count = numberOfShaders();
  //String names[] = new String[count];
  //for(int i = 0; i < count; i++){
  //  names[i] = tag_frames[0].tags[i].STR_NAME; 
  //}
  //return names;
  //}
  
  

  
  
  
  //----------------------------------------------------------------------------
  // GET STUFF FROM MD3 FILE
  //----------------------------------------------------------------------------
  
  public final MD3_Triangle[] getTriangles( int surface_IDX ){
    return MD3_FILE.surfaces[surface_IDX].triangles;
  }
  public final MD3_XYZnormal[] getVertices( int frame_IDX, int surface_IDX ){
    return MD3_FILE.surfaces[surface_IDX].surface_frames[frame_IDX].xyz_normals;
  }
  
  public final MD3_Tag[] getTags( int frame_IDX ){
    return MD3_FILE.tag_frames[frame_IDX].tags;
  }
  
  public final MD3_TexCoord[] getTexCoords(  int surface_IDX){
    return MD3_FILE.surfaces[surface_IDX].tex_coords;
  }
  public final Q3_Texture getTexture(  int surface_IDX){
    return surfaces[surface_IDX].getTexture();
  }
  
  
  
  
  public MD3_Surface getSurfaceByName( String surface_name ){
    MD3_Surface surface_list[] = MD3_FILE.surfaces;
    for(  MD3_Surface surface : surface_list){
      if(  surface.STR_NAME.equals(surface_name) )
        return surface;
    }
    return null;
  }
  
  public MD3_Tag getTagByName( int frame_IDX, String tag_name ){
    MD3_Tag tag_list[] = getTags(frame_IDX);
    for(  MD3_Tag tag : tag_list){
      if(  tag.STR_NAME.equals(tag_name) )
        return tag;
    }
    return null;
  }
  
  
  
  
  
  
  
  
  

  //----------------------------------------------------------------------------
  // DRAW OBJECT - SURFACE
  //----------------------------------------------------------------------------
  public final void drawSurfaceShaded(int frame_IDX){
    if( frame_IDX < 0 || frame_IDX > getNumberOfFrames()-1)
      frame_IDX = 0;
    
    for(int i = 0; i <  this.surfaces.length; i++){
      drawSurfaceShaded(frame_IDX, i);
    }
  }
  
  public final void drawSurfaceShaded( int frame_IDX, int surface_IDX  ){
    parent_p5.pushStyle();
    float[] v1, v2, v3, uv1, uv2, uv3;
    int idx[];
    
    MD3_XYZnormal[] vertices   = getVertices(frame_IDX, surface_IDX );
    MD3_TexCoord[]  tex_coords = getTexCoords( surface_IDX );
    MD3_Triangle[]  triangles  = getTriangles( surface_IDX );
    Q3_Texture      texture    = getTexture( surface_IDX );
    
    parent_p5.noStroke();
    parent_p5.beginShape(PApplet.TRIANGLES);
      if(  texture != null){
        parent_p5.textureMode(PApplet.NORMAL);
        parent_p5.texture(texture.getTexture());
        
        if( !tex_invertU && !tex_invertV){
          for(int j = 0; j < triangles.length; j++){
            idx = triangles[j].S32_3_INDEXES;
            v1  = vertices   [ idx[0] ].F32_3_XYZ;     v2 = vertices    [ idx[1] ].F32_3_XYZ;     v3  = vertices   [ idx[2] ].F32_3_XYZ;
            uv1 = tex_coords [ idx[0] ].F32_2_INDEXES; uv2 = tex_coords [ idx[1] ].F32_2_INDEXES;uv3 = tex_coords [ idx[2] ].F32_2_INDEXES;
            parent_p5.vertex(v1[0], v1[1], v1[2],   uv1[0], uv1[1]);
            parent_p5.vertex(v2[0], v2[1], v2[2],   uv2[0], uv2[1]);
            parent_p5.vertex(v3[0], v3[1], v3[2],   uv3[0], uv3[1]);
          }
        }
        
        if( tex_invertU && !tex_invertV){
          for(int j = 0; j < triangles.length; j++){
            idx = triangles[j].S32_3_INDEXES;
            v1  = vertices   [ idx[0] ].F32_3_XYZ;     v2 = vertices    [ idx[1] ].F32_3_XYZ;     v3  = vertices   [ idx[2] ].F32_3_XYZ;
            uv1 = tex_coords [ idx[0] ].F32_2_INDEXES; uv2 = tex_coords [ idx[1] ].F32_2_INDEXES;uv3 = tex_coords [ idx[2] ].F32_2_INDEXES;
            parent_p5.vertex(v1[0], v1[1], v1[2],   1-uv1[0], uv1[1]);
            parent_p5.vertex(v2[0], v2[1], v2[2],   1-uv2[0], uv2[1]);
            parent_p5.vertex(v3[0], v3[1], v3[2],   1-uv3[0], uv3[1]);
          }
        }
        if( !tex_invertU && tex_invertV){
          for(int j = 0; j < triangles.length; j++){
            idx = triangles[j].S32_3_INDEXES;
            v1  = vertices   [ idx[0] ].F32_3_XYZ;     v2 = vertices    [ idx[1] ].F32_3_XYZ;     v3  = vertices   [ idx[2] ].F32_3_XYZ;
            uv1 = tex_coords [ idx[0] ].F32_2_INDEXES; uv2 = tex_coords [ idx[1] ].F32_2_INDEXES;uv3 = tex_coords [ idx[2] ].F32_2_INDEXES;
            parent_p5.vertex(v1[0], v1[1], v1[2],   uv1[0], 1-uv1[1]);
            parent_p5.vertex(v2[0], v2[1], v2[2],   uv2[0], 1-uv2[1]);
            parent_p5.vertex(v3[0], v3[1], v3[2],   uv3[0], 1-uv3[1]);
          }
        }
        if( tex_invertU && tex_invertV){
          for(int j = 0; j < triangles.length; j++){
            idx = triangles[j].S32_3_INDEXES;
            v1  = vertices   [ idx[0] ].F32_3_XYZ;     v2 = vertices    [ idx[1] ].F32_3_XYZ;     v3  = vertices   [ idx[2] ].F32_3_XYZ;
            uv1 = tex_coords [ idx[0] ].F32_2_INDEXES; uv2 = tex_coords [ idx[1] ].F32_2_INDEXES;uv3 = tex_coords [ idx[2] ].F32_2_INDEXES;
            parent_p5.vertex(v1[0], v1[1], v1[2],   1-uv1[0], 1-uv1[1]);
            parent_p5.vertex(v2[0], v2[1], v2[2],   1-uv2[0], 1-uv2[1]);
            parent_p5.vertex(v3[0], v3[1], v3[2],   1-uv3[0], 1-uv3[1]);
          }
        }
        
      } else {
        for(int j = 0; j < triangles.length; j++){
          idx = triangles[j].S32_3_INDEXES;
          
          v1  = vertices[ idx[0] ].F32_3_XYZ;
          v2  = vertices[ idx[1] ].F32_3_XYZ;
          v3  = vertices[ idx[2] ].F32_3_XYZ;
  
          parent_p5.vertex(v1[0], v1[1], v1[2] );
          parent_p5.vertex(v2[0], v2[1], v2[2] );
          parent_p5.vertex(v3[0], v3[1], v3[2] );
        }
      }
    parent_p5.endShape();
    parent_p5.popStyle();
  }
  
  
  //----------------------------------------------------------------------------
  // DRAW OBJECT - MESH
  //----------------------------------------------------------------------------
  public final void drawSurfaceWireFrame(int frame_IDX){
    if( frame_IDX < 0 || frame_IDX > getNumberOfFrames()-1)
      frame_IDX = 0;
    
    for(int i = 0; i <  this.surfaces.length; i++){
      drawSurfaceWireFrame(frame_IDX, i);
    }
  }
  public final void drawSurfaceWireFrame( int frame_IDX, int surface_IDX  ){
    float[] v1, v2, v3;
    int idx[];
    
    MD3_XYZnormal[] vertices   = getVertices(frame_IDX, surface_IDX );
    MD3_Triangle[]  triangles  = getTriangles( surface_IDX );
    parent_p5.pushStyle();
    parent_p5.noFill();
    parent_p5.beginShape(PApplet.TRIANGLES);
      for(int j = 0; j < triangles.length; j++){
        idx = triangles[j].S32_3_INDEXES;
        
        v1  = vertices[ idx[0] ].F32_3_XYZ;
        v2  = vertices[ idx[1] ].F32_3_XYZ;
        v3  = vertices[ idx[2] ].F32_3_XYZ;
        parent_p5.vertex(v1[0], v1[1], v1[2] );
        parent_p5.vertex(v2[0], v2[1], v2[2] );
        parent_p5.vertex(v3[0], v3[1], v3[2] );
      }
    parent_p5.endShape();
    parent_p5.popStyle();
    
  }
  
  
  //----------------------------------------------------------------------------
  // DRAW OBJECT - VERTICES
  //----------------------------------------------------------------------------
  public final void drawSurfaceVertices(int frame_IDX){
    if( frame_IDX < 0 || frame_IDX > getNumberOfFrames()-1)
      frame_IDX = 0;
  
    for(int i = 0; i <  this.surfaces.length; i++){
      drawSurfaceVertices(frame_IDX, i);
    }
  }
  public final void drawSurfaceVertices( int frame_IDX, int surface_IDX  ){
    parent_p5.pushStyle();
    MD3_XYZnormal[] vertices   = getVertices(frame_IDX, surface_IDX );
    
    parent_p5.noFill();
    parent_p5.beginShape(PApplet.POINTS);
      for(MD3_XYZnormal v : vertices){
        parent_p5.vertex(v.F32_3_XYZ[0], v.F32_3_XYZ[1], v.F32_3_XYZ[2] );
      }
    parent_p5.endShape();
    parent_p5.popStyle();
  }
  


  //----------------------------------------------------------------------------
  // DRAW NORMALS
  //----------------------------------------------------------------------------
  public final void drawNormals(int frame_IDX, float scale){
    for(int i = 0; i < getNumberOfSurfaces(); i++){
      drawNormals(frame_IDX, i, scale);
    }
  }
  
  public final void drawNormals(int frame_IDX, int surface_IDX, float scale){
    if( frame_IDX < 0 || frame_IDX > getNumberOfFrames()-1)
      frame_IDX = 0;
     
    float xyz_s[] = new float[3]; 
    float xyz_e[] = new float[3];

    MD3_XYZnormal[] vertices   = getVertices(frame_IDX, surface_IDX );
    parent_p5.beginShape(PApplet.LINES);
    
      for(int j = 0; j < vertices.length; j++){
        xyz_s = vertices[j].F32_3_XYZ;
        xyz_e[0] = xyz_s[0] + vertices[j].F32_3_XYZ_NORM[0]*scale;
        xyz_e[1] = xyz_s[1] + vertices[j].F32_3_XYZ_NORM[1]*scale; 
        xyz_e[2] = xyz_s[2] + vertices[j].F32_3_XYZ_NORM[2]*scale; 
    
        parent_p5.vertex(xyz_s[0], xyz_s[1], xyz_s[2]);
        parent_p5.vertex(xyz_e[0], xyz_e[1], xyz_e[2]);
      }

    parent_p5.endShape();
  }
  
  
  

  

  //----------------------------------------------------------------------------
  // DRAW TAGS
  //----------------------------------------------------------------------------
  public final void drawTags(int frame_IDX, float tag_size){
    if( frame_IDX < 0 || frame_IDX > getNumberOfFrames()-1)
      frame_IDX = 0;
    MD3_Tag tags[] = getTags(frame_IDX);
    
    for(int i = 0; i <  tags.length; i++){
      parent_p5.pushMatrix();
        parent_p5.applyMatrix(tags[i].getMatrix());
        drawBKS(tag_size);
      parent_p5.popMatrix();
    }
  }
  

  //----------------------------------------------------------------------------
  // DRAW BOUNDINGBOX
  //----------------------------------------------------------------------------
  public final void drawBoundingbox(int frame_IDX){
    if( frame_IDX < 0 || frame_IDX > getNumberOfFrames()-1)
      frame_IDX = 0;
     
    if( MD3_FILE.surfaces.length == 0)
      return;
    
    MD3_Frame frame = MD3_FILE.frames[frame_IDX];
    float[] bb_min = frame.VEC3_MIN_BOUNDS;
    float[] bb_max = frame.VEC3_MAX_BOUNDS;
    float bb_s[] = {bb_max[0]-bb_min[0], bb_max[1]-bb_min[1], bb_max[2]-bb_min[2]};
    
//    System.out.println("bb: "+MD3_FILE.file.getName());
//    System.out.println("bb_min: "+bb_min[0]+", "+bb_min[1]+", "+bb_min[2]);
//    System.out.println("bb_max: "+bb_max[0]+", "+bb_max[1]+", "+bb_max[2]);
    parent_p5.pushStyle();
    parent_p5.noFill();
    parent_p5.pushMatrix();
    parent_p5.beginShape(PConstants.QUAD_STRIP);
      parent_p5.vertex(bb_min[0], bb_min[1], bb_min[2] );
      parent_p5.vertex(bb_min[0], bb_max[1], bb_min[2] );
      
      parent_p5.vertex(bb_max[0], bb_min[1], bb_min[2] );
      parent_p5.vertex(bb_max[0], bb_max[1], bb_min[2] );
      
      parent_p5.vertex(bb_max[0], bb_min[1], bb_max[2] );
      parent_p5.vertex(bb_max[0], bb_max[1], bb_max[2] );
      
      parent_p5.vertex(bb_min[0], bb_min[1], bb_max[2] );
      parent_p5.vertex(bb_min[0], bb_max[1], bb_max[2] );
      
      parent_p5.vertex(bb_min[0], bb_min[1], bb_min[2] );
      parent_p5.vertex(bb_min[0], bb_max[1], bb_min[2] );
      
    parent_p5.endShape();
    
    float sf = 1/5f; //scale
    float os = .25f; //offset
    parent_p5.translate(bb_min[0]+os, bb_min[1]+os, bb_min[2]+os);
    parent_p5.strokeWeight(2);
    drawBKS(bb_s[0]*sf, bb_s[1]*sf, bb_s[2]*sf);
    
    parent_p5.popMatrix();
    parent_p5.popStyle();
 
  }
  
  
  
  //----------------------------------------------------------------------------
  // DRAW BKS
  //----------------------------------------------------------------------------
  public final void drawBKS(float s){
    drawBKS(s, s, s);
  }
  
  public final void drawBKS(float sizex, float sizey, float sizez){
    parent_p5.pushStyle();
    parent_p5.stroke(255, 0, 0); parent_p5.line(0, 0, 0, sizex, 0, 0);
    parent_p5.stroke(0, 255, 0); parent_p5.line(0, 0, 0, 0, sizey, 0);
    parent_p5.stroke(0, 0, 255); parent_p5.line(0, 0, 0, 0, 0, sizez);
    parent_p5.popStyle();
  }
  


  
}
