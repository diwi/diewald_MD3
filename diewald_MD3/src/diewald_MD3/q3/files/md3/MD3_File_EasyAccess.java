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


import diewald_MD3.q3.Q3_Texture;

public class MD3_File_EasyAccess {
  private final MD3_File md3;
  
  
  public MD3_File_EasyAccess(MD3_File md3){
    this.md3 = md3;
  }

  
  
  
  
  //------------------------------------------------------------------------------
  //SIMPLE ACESS
  //
  //public final File file;
  //
  //protected Byter byter = null;
  //public    ByteOrder byte_order = ByteOrder.LITTLE_ENDIAN;
  //
  //public MD3_Header   header;
  //public MD3_Frame    frames[];
  //public MD3_TagFrame tag_frames[];
  //public MD3_Surface  surfaces[];
  //
  //public boolean LOADED = false;
  //------------------------------------------------------------------------------
  
  public final MD3_Header getHeader(){
    return md3.header;
  }
  
  //frames
  public final int getFramesCount(){
    return md3.header.S32_NUM_FRAMES;
  }
  public final MD3_Frame[] getFrames(){
    return md3.frames;
  }


  // surfaces
  public final int getSurfacesCount(){
    return md3.header.S32_NUM_SURFACES;
  }
  public final MD3_Surface[] getSurfaces(){
    return md3.surfaces;
  }
  
  
  
  // Triangles
  public final int getSurfaceTrianglesCount(int surface_index){
    return md3.surfaces[surface_index].S32_NUM_TRIANGLES;
  }
  
  public final MD3_Triangle[] getSurfaceTriangles(int surface_index){
    return md3.surfaces[surface_index].triangles;
  }

  
  
  //Texture, Texcoords
  public final int getSurfaceTextureCoordsCount(int surface_index){
    return md3.surfaces[surface_index].S32_NUM_VERTS;
  }
  public final MD3_TexCoord[] getSurfaceTextureCoords(int surface_index){
    return md3.surfaces[surface_index].tex_coords;
  }
  public final Q3_Texture getSurfaceTexture(int surface_index){
    return md3.surfaces[surface_index].getTexture();
  }
  
  
  // vertices
  public final int getSurfaceVerticesCount( int surface_index ){
    return md3.surfaces[surface_index].S32_NUM_VERTS;
  }
  public final MD3_XYZnormal[] getSurfaceVertices(int frame_index, int surface_index){
    return md3.surfaces[surface_index].surface_frames[frame_index].xyz_normals;
  }
//  public final float[] getSurfaceVertex(int frame_index, int surface_index, int vertex_index){
//    return md3.surfaces[surface_index].surface_frames[frame_index].xyz_normals[vertex_index].F32_3_XYZ;
//  }
//  public final float[] getSurfaceVertexNormal(int frame_index, int surface_index, int vertex_index){
//    return md3.surfaces[surface_index].surface_frames[frame_index].xyz_normals[vertex_index].F32_3_XYZ_NORM;
//  }
  
  

  


  
  
  public final int getTagsCount(){
    return md3.header.S32_NUM_TAGS;
  }
  
  public final MD3_Tag[] getTags(int frame_index){
    return md3.tag_frames[frame_index].tags;
  }
  
  


}
