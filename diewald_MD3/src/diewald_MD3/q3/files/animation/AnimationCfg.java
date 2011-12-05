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


package diewald_MD3.q3.files.animation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import diewald_MD3.q3.Q3_Animation;



public class AnimationCfg {
  
  public static boolean LOG_CONSOLE = false;
  
  protected File animation_file;
  
  protected ArrayList<String> lines = new ArrayList<String>(20);
  
  protected ArrayList<Q3_Animation> animations = new ArrayList<Q3_Animation>();

  
  public final static AnimationCfg Load(String path, String file_name){
    try {
      return new AnimationCfg(path, file_name);
    } catch(Exception e){
      e.printStackTrace();
      return null;
    }
  }
  
  private AnimationCfg(String path, String file_name) throws Exception {
    animation_file = new File(path, file_name);
    loadFileContent();
    extractFileContent();
   
    if( LOG_CONSOLE )
      printData();
  }
  
  private void loadFileContent() throws IOException{
    BufferedReader br = new BufferedReader(new FileReader(animation_file));
    String line;
      
    while ((line = br.readLine()) != null) {
      lines.add(line);
    }
    br.close();
  }
  

  
  private void extractFileContent() throws Exception{
 
    int first_frame, num_frames, looping_frames, frames_per_second; //data from file
    int end_frame; 
    String description = "";
    
    
    for( int i = 0; i < lines.size(); i++){
      String line = lines.get(i);
      line = line.trim();
      if( line.length() == 0) continue;
      if( line.startsWith("//")) continue;
    
   
      String tokens[] = line.split("\t");
//      System.out.println(parts.length);
      if( tokens.length >= 4){
        try{
          
          // get frame-data
          first_frame       = Integer.parseInt(tokens[0]);
          num_frames        = Integer.parseInt(tokens[1]);
          looping_frames    = Integer.parseInt(tokens[2]);
          frames_per_second = Integer.parseInt(tokens[3]);
          
          end_frame = first_frame + num_frames; //TODO: set the first frame of next animation
          
          // get description
          for(int j = 4; j < tokens.length; j++ ){
            tokens[j] = tokens[j].trim();
            if( tokens[j].length() == 0) continue;
            if( tokens[j].startsWith("//")){
              tokens[j] = tokens[j].replaceAll("[/]", "");
              tokens[j] = tokens[j].trim();
              description = tokens[j];
            }
          }
          
          // build new animation from data
          Q3_Animation anim = new Q3_Animation
          (
              first_frame, 
              num_frames, 
              looping_frames, 
              frames_per_second,
              description,
              end_frame
          );
          
          // add animation to list
          animations.add(anim);
        } catch (NumberFormatException e){
          e.printStackTrace();
        }
      }
    }
  }
  
  public final ArrayList<Q3_Animation> getAnimations(){
    return animations;
  }
  

  public void printData(){
    System.out.println("---------------------<AnimationCfg_File>-------------------------");
    System.out.println(animation_file.getAbsolutePath());
    
    for( Q3_Animation a: animations)
      a.printData();
    System.out.println("---------------------</AnimationCfgn_File>-------------------------");
  }
}
