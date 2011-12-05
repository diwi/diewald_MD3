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

import diewald_MD3.q3.constants.Team;
import diewald_MD3.q3.files.animation.AnimationCfg;


import processing.core.PApplet;



public class Q3_Player extends Q3_Model{

  private Q3_Object head;
  private Q3_Object upper;
  private Q3_Object lower;
  
  private Q3_Weapon weapon;
  
  private String HEAD_MD3  = "head.md3";
  private String UPPER_MD3 = "upper.md3";
  private String LOWER_MD3 = "lower.md3";
  
  private String HEAD_SKIN_SW  = "head_";
  private String UPPER_SKIN_SW = "upper_";
  private String LOWER_SKIN_SW = "lower_";
  
  private Team team = Team.DEFAULT;
  
  private NodeState n_lower;
  private NodeState n_upper;
  private NodeState n_head ;
  
  
  private ArrayList<Q3_Animation> animations;

  
  public Q3_Player(PApplet parent_p5, Team team){
    this(parent_p5, team, null);
  }
  
  public Q3_Player(PApplet parent_p5, Team team, Q3_TexturePool texture_pool){
    super(parent_p5, texture_pool);

    this.team = team == null ? Team.DEFAULT : team;
  }
  

  @Override
  public boolean loadFromDirectory(String path){
    long time_head_model = 0, time_upper_model = 0, time_lower_model = 0;
    
    long time = System.currentTimeMillis();
    model_dir = new File(path);

    
    if (!model_dir.isDirectory()){
      System.err.println("loading Weapon: directory doesn't exist" + model_dir);
      return false;
    }
    
    
    try {
      time_head_model = System.currentTimeMillis();
      head = loadMD3(model_dir.getAbsolutePath(), HEAD_MD3);
      loadTexturesFromSkinfile(head, path, HEAD_SKIN_SW+ team.getSubFix()+".skin");
      time_head_model = System.currentTimeMillis() - time_head_model;
    } catch (Exception e) {
      e.printStackTrace();
    }
    
    try {
      time_upper_model = System.currentTimeMillis();
      upper = loadMD3(model_dir.getAbsolutePath(), UPPER_MD3);
      loadTexturesFromSkinfile(upper, path, UPPER_SKIN_SW + team.getSubFix()+".skin");
      time_upper_model = System.currentTimeMillis() - time_upper_model;
    } catch (Exception e) {
      e.printStackTrace();
    }
    
    try {
      time_lower_model = System.currentTimeMillis();
      lower = loadMD3(model_dir.getAbsolutePath(), LOWER_MD3);
      loadTexturesFromSkinfile(lower, path, LOWER_SKIN_SW + team.getSubFix()+".skin");
      time_lower_model = System.currentTimeMillis() - time_lower_model;
    } catch (Exception e) {
      e.printStackTrace();
    }
    time = System.currentTimeMillis() - time;
    System.out.println("LOADED PLAYER: \""+model_dir.getName() +"\"   (loadingtime: "+time+" ms)");
    System.out.println("head:  "+time_head_model+" ms");
    System.out.println("upper: "+time_upper_model+" ms");
    System.out.println("lower: "+time_lower_model+" ms");
    
    
    AnimationCfg animation = AnimationCfg.Load(model_dir.getAbsolutePath(), "animation.cfg");
    animations = animation.getAnimations();
    
    makeNodeSystem();
    return true;
  }
  
 
  public void setHead(Q3_Object md3){
    this.head = md3;
    n_head.setObject(this.head);
  }
  public void setUpper(Q3_Object md3){
    this.upper = md3;
    n_upper.setObject(this.upper);
  }
  public void setLower(Q3_Object md3){
    this.lower = md3;
    n_lower.setObject(this.lower);
  }
  
  public Q3_Object getHead(){
    return head;
  }
  public Q3_Object getUpper(){
    return upper;
  }
  public Q3_Object getLower(){
    return lower;
  }

 
  public final void setWeapon(Q3_Weapon weapon){
    this.weapon = weapon;
    if( this.weapon != null && this.weapon.getNodeSystem() != null){
      this.weapon.getNodeSystem().getTopLevelNode().setParentNode(n_upper, "tag_weapon");
    }
  }
  public final Q3_Weapon getWeapon(){
    return weapon;
  }
  
  public final ArrayList<Q3_Animation> getAnimations(){
    return animations;
  }
  
  


  @Override
  public void makeNodeSystem(){
    n_lower  = new NodeState(lower ); n_lower  .setParentNode(null,  null  );
    n_upper  = new NodeState(upper ); n_upper  .setParentNode(n_lower,  "tag_torso"  );
    n_head   = new NodeState(head  ); n_head   .setParentNode(n_upper,  "tag_head"   );
    
    n_lower.setAsTopLevelNode();
    node_system = n_head.getTopLevelNode();
  }
  @Override
  public NodeState getNodeSystem(){
    return node_system;
  }
  public NodeState getNodeHead(){
    return n_head;
  }
  public NodeState getNodeUpper(){
    return n_upper;
  }
  public NodeState getNodeLower(){
    return n_lower;
  }

  
  @Override
  public void drawModel(int frame_IDX){
    node_system.updateNodeSystem(frame_IDX);
    node_system.drawChilds();
  }
  

}
