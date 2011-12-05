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


package diewald_MD3.q3.files.skin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public final class Skin_File {
  
  public static boolean LOG_CONSOLE = false;
  
  protected File skin_file;
  
  protected ArrayList<String> lines = new ArrayList<String>(20);
  protected ArrayList<String> tags  = new ArrayList<String>(5);
  protected ArrayList<String> surface_names  = new ArrayList<String>(5);
  protected ArrayList<File>   texture_paths  = new ArrayList<File>(5);
  
  
  public final static Skin_File Load(String path, String file_name){
    try {
      return new Skin_File(path, file_name);
    } catch(Exception e){
      e.printStackTrace();
      return null;
    }
  }
  
  private Skin_File(String path, String file_name) throws Exception {
    skin_file = new File(path, file_name);
//    long time = System.currentTimeMillis();
    
    loadFileContent();
    extractFileContent();
    cleanUp();
    if( LOG_CONSOLE )
      printData();
//    System.out.println("loading_time = "+(System.currentTimeMillis()-time)+" ms");
  }
  
  private void loadFileContent() throws IOException{
    BufferedReader br = new BufferedReader(new FileReader(skin_file));
    String line;
      
    while ((line = br.readLine()) != null) {
      lines.add(line);
    }
    br.close();
  }
  
  private void extractFileContent() throws Exception{
    boolean expecting_texture = false;
    for( String ln : lines ){
      ln = ln.trim();
      String ln_tokens[] = ln.split(",");
      
      for( String token : ln_tokens){
        token = token.trim();
        if( token.length() == 0 || token.startsWith("//"))
          continue;
        // add tag to list
        if( !expecting_texture && token.startsWith("tag_")){
          if( !tags.contains(token))
           tags.add(token);
        // add surface-name to list
        } else if( !expecting_texture ){
            surface_names.add(token);
          expecting_texture = true;
        // add surface-texture to list
        } else if( expecting_texture ){
          texture_paths.add(new File(token));
          expecting_texture = false;
        } else {
          System.out.println("--------------------------------problem------------------------------");
        }
      }
    }
    if (surface_names.size() != texture_paths.size() ){
      throw new Exception("!CORRPUT SKIN-File!) number of surfaces != number of textures");
    }
  }
  
  
  
  private void cleanUp(){

    File SKIN_DIR = skin_file.getParentFile();

    for(int i = surface_names.size()-1; i >= 0; i--){
      File texture = texture_paths.get(i);

      File parent = texture;

      //search for the right entry folder in the given path of the skinfile
      ArrayList<String> folder_names = new ArrayList<String>();

      while( parent != null){
//        System.out.println("parent..." + parent.getAbsolutePath());
        String folder_name = parent.getName();
        
        if( SKIN_DIR.getName().equals(folder_name)){
          break;
        }
        folder_names.add(folder_name);
        parent = parent.getParentFile();
      }
      
      //didnt find any match
      if( parent == null ){
        System.err.println("("+skin_file.getName()+") texture unavailable: "+surface_names.get(i)+", "+texture);
        surface_names.remove(i);
        texture_paths.remove(i);
        continue;
      }
      

      // rebuilding path, to search for right file
      File child = SKIN_DIR;
      for(int j = folder_names.size()-1; j >= 0; j--){
        child = new File(child, folder_names.get(j));
        if( !child.exists() ){
          break;
        }
      }
      if( !child.exists() ){
        System.err.println("("+skin_file.getName()+") texture unavailable: "+surface_names.get(i)+", "+texture);
        surface_names.remove(i);
        texture_paths.remove(i);
        continue;
      }
      
      //if the file exists:
      // change current path in "surface_textures" to real path.
      texture_paths.set(i, child);
    } 
  }
  

  public void printData(){
    System.out.println("---------------------<Skin_File>-------------------------");
    System.out.println(skin_file.getAbsolutePath());
    for( String t : tags )
      System.out.println(t);
    for(int i = 0; i < surface_names.size(); i++){
      String surf = surface_names.get(i);
      File   text = texture_paths.get(i);
      System.out.println(surf+", "+text.getAbsolutePath());
    } 
    System.out.println("---------------------</Skin_File>-------------------------");
  }
  
  
  public File getTextureFileBySurfaceName(String surface_name){
    int tex_idx = getSurfaceNameIDX(surface_name);
    if( tex_idx == -1)
      return null;
    return texture_paths.get(tex_idx);
  }
  
  public ArrayList<String> getTags(){
    return tags;
  }
  public ArrayList<String> getSurfaceNames(){
    return surface_names;
  }
  public ArrayList<File> getTexturePaths(){
    return texture_paths;
  }
  
  private int getSurfaceNameIDX(String surface_name){
    for(int i = 0; i < surface_names.size(); i++){
      String s = surface_names.get(i);
      if( s.equals(surface_name))
        return i;
    }
    return -1;
  }
  

 

}
