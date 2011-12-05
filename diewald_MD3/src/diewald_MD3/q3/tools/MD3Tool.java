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


package diewald_MD3.q3.tools;

import java.util.ArrayList;

import diewald_MD3.q3.files.md3.MD3_File;
import diewald_MD3.q3.files.md3.MD3_Frame;
import diewald_MD3.q3.files.md3.MD3_Shader;
import diewald_MD3.q3.files.md3.MD3_Surface;
import diewald_MD3.q3.files.md3.MD3_SurfaceFrame;
import diewald_MD3.q3.files.md3.MD3_Tag;
import diewald_MD3.q3.files.md3.MD3_TagFrame;
import diewald_MD3.q3.files.md3.MD3_TexCoord;
import diewald_MD3.q3.files.md3.MD3_Triangle;
import diewald_MD3.q3.files.md3.MD3_XYZnormal;


public class MD3Tool {
  
  public static void setHeaderName(MD3_File md3_file, String new_name){
    md3_file.header.STR_NAME =  new_name;
  }
  
  
  
  /**
   * generates a string, which can be saved to a file named "filename".skin, to 
   * as a skin file. the content is based on the given data from the md3-file.
   * ... shader-information of each surface...
   * 
   * @param md3_file
   * @return content of a new skin file
   */
  public static String makeSkinFile(MD3_File md3_file){
    if( md3_file == null )
      return ""; 
    StringBuilder sb = new StringBuilder();
    String md3_file_name = md3_file.file.getName();
    
    String skin_file_name = md3_file_name.split("[.]")[0] + ".skin";

    sb.append("//.................... skin file ....................");
    sb.append("// .skin: "+skin_file_name);
    sb.append("//");
    sb.append("// .md3: "+md3_file.file);
    sb.append("//");
    MD3_TagFrame tag_frames[] = md3_file.tag_frames;
    if( tag_frames != null && tag_frames.length >= 1){
      sb.append("// tags");
      MD3_Tag tags[] = tag_frames[0].tags;
      if( tags != null ){
        for(int i = 0; i < tags.length; i++){
          sb.append(tags[i].STR_NAME+",");
        }
      }
    }
    sb.append("");
    sb.append("// surfaces/shaders ");
    MD3_Surface md3_surfaces[] = md3_file.surfaces;
    for(int i = 0; i < md3_surfaces.length; i++){
      String surface_name = md3_surfaces[i].STR_NAME;
      MD3_Shader md3_shaders[] = md3_surfaces[i].shaders;
      for(int j = 0; j <  md3_shaders.length; j++){
        String shader_name = md3_shaders[j].STR_NAME;
        shader_name = shader_name.replaceAll("\0", "");
        if(shader_name.startsWith("odels")) shader_name = "m"+shader_name;
        sb.append(surface_name +", "+shader_name );
      }
      if( md3_shaders.length == 0)
        sb.append(surface_name +", (undefined texture)" );
    }
    sb.append("");
    sb.append("//.................... skin file ....................");
    return sb.toString();
  }
  
  
  public static void invertNormals(MD3_File md3_file){
    MD3_Surface[] surface_list = md3_file.surfaces;
    for(int surface_idx = 0; surface_idx < surface_list.length; surface_idx++){
      
      MD3_Surface surface = surface_list[surface_idx];
      MD3_SurfaceFrame[] surface_frame_list = surface.surface_frames;
      
      for(int surface_frame_idx = 0; surface_frame_idx < surface_frame_list.length; surface_frame_idx++){

        MD3_SurfaceFrame surface_frame = surface_frame_list[surface_frame_idx];
        MD3_XYZnormal[] xyz_normals_list = surface_frame.xyz_normals;
        
        for(int xyz_normals_idx = 0; xyz_normals_idx < xyz_normals_list.length; xyz_normals_idx++){   

          float[] normal = xyz_normals_list[xyz_normals_idx].F32_3_XYZ_NORM;
          normal[0] *= -1;
          normal[1] *= -1;
          normal[2] *= -1;
          
        }
      }
    }
  }
  
  
  public static void repairNormals(MD3_File md3_file, VertexNormalMode mode){
    System.out.println("------------------------------------------------------------------------------------");
    System.out.println("------------------------------------------------------------------------------------");
    System.out.println("starting repairing normals");
    System.out.println("md3_file : "+md3_file.file.getAbsolutePath() );
    System.out.println("... ");
    long timer = System.currentTimeMillis();
    
    MD3_Surface[] surface_list = md3_file.surfaces;
    for(int surface_idx = 0; surface_idx < surface_list.length; surface_idx++){
      
      
      MD3_Surface surface = surface_list[surface_idx];
      MD3_SurfaceFrame[] surface_frame_list = surface.surface_frames;
      
      MD3_Triangle triangle_list[] = surface.triangles;
      
      for(int surface_frame_idx = 0; surface_frame_idx < surface_frame_list.length; surface_frame_idx++){
        System.out.println("... frame["+surface_frame_idx+"]" );
        MD3_SurfaceFrame surface_frame = surface_frame_list[surface_frame_idx];
        MD3_XYZnormal[] xyz_normals_list = surface_frame.xyz_normals;
        for(int xyz_normals_idx = 0; xyz_normals_idx < xyz_normals_list.length; xyz_normals_idx++){
          MD3_XYZnormal xyz_normals = xyz_normals_list[xyz_normals_idx];
          
          float[] normal_vec_tmp = new float[3];
          //list of triangles, containing the current vertex
          ArrayList<MD3_Triangle> triangle_list_tmp = getTrianglesWithVertex(xyz_normals_idx, triangle_list);
          
          // add up each normalvector of the triangles ... to get an average
          for( int triangle_idx = 0; triangle_idx < triangle_list_tmp.size(); triangle_idx++){
            MD3_Triangle triangle = triangle_list_tmp.get(triangle_idx);
            float[] normal_vec = getNormalVector(triangle, xyz_normals_idx, xyz_normals_list, mode );
            normal_vec_tmp = f3_add(normal_vec_tmp, normal_vec);
          }
          
          //normalize the normalvector
          normal_vec_tmp = f3_normalize(normal_vec_tmp);
          
          //copy to file
          f3_copy(normal_vec_tmp, xyz_normals.F32_3_XYZ_NORM);    
        }
      }
    }
    

    timer = System.currentTimeMillis()-timer;
    System.out.println("... ");
    System.out.println("finished repairing normals in "+timer+" ms");
    System.out.println("------------------------------------------------------------------------------------");
    System.out.println("------------------------------------------------------------------------------------");
  }
  
  
  
  private static ArrayList<MD3_Triangle> getTrianglesWithVertex(int vertex_idx, MD3_Triangle triangle_list[] ){
    ArrayList<MD3_Triangle> triangle_tmp = new ArrayList<MD3_Triangle>();
    for(int triangle_idx = 0; triangle_idx < triangle_list.length; triangle_idx++){
      MD3_Triangle triangle = triangle_list[triangle_idx];
      if( triangle.S32_3_INDEXES[0] == vertex_idx ||
          triangle.S32_3_INDEXES[1] == vertex_idx ||
          triangle.S32_3_INDEXES[2] == vertex_idx)
      {
        triangle_tmp.add(triangle);
      }
    }
    
    return triangle_tmp;
  }
  
  
  
  
  
  public static void repairFrameData(MD3_File md3_file){
    System.out.println("------------------------------------------------------------------------------------");
    System.out.println("------------------------------------------------------------------------------------");
    System.out.println("starting repairing fram-data (boundingbox, boundingspere, framenames)");
    System.out.println("md3_file : "+md3_file.file.getAbsolutePath() );
    System.out.println("... ");
    long timer = System.currentTimeMillis();
    
    MD3_Frame[] frame_list = md3_file.frames;
    MD3_Surface[] surface_list = md3_file.surfaces;
    
    for(int frame_idx = 0; frame_idx < frame_list.length; frame_idx++){
      MD3_Frame frame = frame_list[frame_idx];
      
      //changes frame names
      frame.STR_NAME = "MD3_diewald"+frame_idx;
      
      //calculate new boundingsbox
      float bb_min[] = {Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE};
      float bb_max[] = {Float.MIN_VALUE, Float.MIN_VALUE, Float.MIN_VALUE};
      float bb_size[] = new float[3];
      float bb_min_tmp[], bb_max_tmp[];
      float radius;
      for(int j = 0; j < surface_list.length; j++){
        MD3_Surface surface = surface_list[j];
        MD3_SurfaceFrame surface_frame = surface.surface_frames[frame_idx];
        bb_min_tmp = surface_frame.bb_minXYZ;
        bb_max_tmp = surface_frame.bb_maxXYZ;
        for(int i = 0; i < 3; i++) if( bb_min[i] > bb_min_tmp[i] ) bb_min[i] = bb_min_tmp[i];
        for(int i = 0; i < 3; i++) if( bb_max[i] < bb_max_tmp[i] ) bb_max[i] = bb_max_tmp[i];
      }
      
      System.arraycopy(bb_min, 0, frame.VEC3_MIN_BOUNDS, 0, 3);
      System.arraycopy(bb_max, 0, frame.VEC3_MAX_BOUNDS, 0, 3);
      
      
      //calculate simple radius
      bb_size = f3_sub(bb_max, bb_min);
//      println("BOUNDINGBOX = "+bb_size[0]+", "+bb_size[1]+", "+bb_size[2]);
      radius = (float) (Math.sqrt(bb_size[0]*bb_size[0] + bb_size[1]*bb_size[1] + bb_size[2]*bb_size[2])/2.0);
      frame.F32_RADIUS = radius;
    }

    timer = System.currentTimeMillis()-timer;
    System.out.println("... ");
    System.out.println("finished repairing frame-data in "+timer+" ms");
    System.out.println("------------------------------------------------------------------------------------");
    System.out.println("------------------------------------------------------------------------------------");
  }
  
  public static void invertTexCoords(MD3_File md3_file, boolean invert_u, boolean invert_v){
    MD3_Surface surface_list[] = md3_file.surfaces;
    for(MD3_Surface surface : surface_list){

      //mirror textcoords
      MD3_TexCoord tex_coord_list[] = surface.tex_coords;
      for(MD3_TexCoord tex_coord : tex_coord_list){
        if( invert_u )
          tex_coord.F32_2_INDEXES[0] = 1f-tex_coord.F32_2_INDEXES[0];
        if( invert_v )
          tex_coord.F32_2_INDEXES[1] = 1f-tex_coord.F32_2_INDEXES[1];
      }
    }
  }
  
  
  
  
  
  
  
  
  public static float angleBetween(float[] a, float[] b){
    return (float) Math.acos( f3_dot(a,b)/(f3_mag(a)*f3_mag(b)) );
  }
  
  
  
  
  public static float[] getNormalVector(MD3_Triangle triangle, int current_vertex_idx, MD3_XYZnormal[] xyz_normals_list, VertexNormalMode mode){
    if( mode == null ) mode = VertexNormalMode.WEIGHT_BY_ANGLE;
    
    int idx_a = current_vertex_idx;
    
    int idx_b = triangle.S32_3_INDEXES[1];
    int idx_c = triangle.S32_3_INDEXES[2];
    
    if( current_vertex_idx == triangle.S32_3_INDEXES[1]){
      idx_b = triangle.S32_3_INDEXES[2];
      idx_c = triangle.S32_3_INDEXES[0];
    }
    if( current_vertex_idx == triangle.S32_3_INDEXES[2]){
      idx_b = triangle.S32_3_INDEXES[0];
      idx_c = triangle.S32_3_INDEXES[1];
    }
    
    float[] a = xyz_normals_list[ idx_a ].F32_3_XYZ;
    float[] b = xyz_normals_list[ idx_b ].F32_3_XYZ;
    float[] c = xyz_normals_list[ idx_c ].F32_3_XYZ;
 
    float[] ab = f3_sub(b,a);
    float[] ac = f3_sub(c,a);
    float[] ab_x_ac = f3_cross(ac, ab);
    
    float mult_factor = 1f;
    
    if( mode == VertexNormalMode.WEIGHT_BY_UNIFORMS ){
      ab_x_ac = f3_normalize(ab_x_ac);
    }
    
    if( mode == VertexNormalMode.WEIGHT_BY_ANGLE ){
      mult_factor = angleBetween(ab, ac);
      ab_x_ac = f3_normalize(ab_x_ac);
      ab_x_ac = f3_mult(ab_x_ac, mult_factor);
    }
    
    if( mode == VertexNormalMode.WEIGHT_BY_AREA ){
      // do nothing, since the magnitude of the cross-product is proportional to the area
    }   
    

    return ab_x_ac;
  }
  
  
  public static void f3_copy(float[] src, float[] dst){
    dst[0] = src[0];
    dst[1] = src[1];
    dst[2] = src[2];
  }
  
  public static float[] f3_sub(float[] a, float[] b){
    return new float[]{a[0]-b[0], a[1]-b[1], a[2]-b[2] };
  }
  
  public static float[] f3_add(float[] a, float[] b){
    return new float[]{a[0]+b[0], a[1]+b[1], a[2]+b[2] };
  }
  public static float[] f3_mult(float[] a, float mult){
    return new float[]{a[0]*mult, a[1]*mult, a[2]*mult };
  }
  
  public static float f3_mag(float[] a){
    return (float)(Math.sqrt(a[0]*a[0]+ a[1]*a[1] +a[2]*a[2] ));
  }
  
  public static float[] f3_normalize(float[] a){
    float mag = f3_mag(a);
    return new float[]{a[0]/mag, a[1]/mag, a[2]/mag };
  }
  
  public static float[] f3_cross(float[] a, float[] b){
    return new float[] { a[1]*b[2] - a[2]*b[1], 
                         a[2]*b[0] - a[0]*b[2], 
                         a[0]*b[1] - a[1]*b[0]};
  }
  
  public static float f3_dot(float[] a, float[] b){
    return a[0]*b[0] + a[1]*b[1] + a[2]*b[2];
  }
  

  
  
  
  

  

  
  
}
