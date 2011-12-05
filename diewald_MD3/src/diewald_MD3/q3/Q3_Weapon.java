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


import processing.core.PApplet;


public class Q3_Weapon extends Q3_Model{

  private Q3_Object weapon;
  private Q3_Object hand;
  private Q3_Object flash;
  private Q3_Object barrel;
  

  private String weapon_name = null;
  
  private String MD3_FORMAT        = ".md3";
  private String SKIN_FORMAT       = ".skin";
  
  private String WEAPON_MD3_SUFFIX = "";
  private String HAND_MD3_SUFFIX   = "_hand";
  private String FLASH_MD3_SUFFIX  = "_flash";
  private String BARREL_MD3_SUFFIX = "_barrel";
  
  private NodeState n_hand  ; 
  private NodeState n_weapon; 
  private NodeState n_flash ; 
  private NodeState n_barrel; 

  
  
  public Q3_Weapon( PApplet parent_p5 ){
    this(parent_p5, null);
  }
  
  public Q3_Weapon( PApplet parent_p5, Q3_TexturePool texture_pool){
    super(parent_p5, texture_pool);
  }
  
  
  
  
  @Override
  public final boolean loadFromDirectory(String path){
//    long time_head_model = 0, time_upper_model = 0, time_lower_model = 0;
    
    long time = System.currentTimeMillis();
    model_dir = new File(path);


    if (!model_dir.isDirectory()){
      System.err.println("loading Weapon: directory doesn't exist" + model_dir);
      return false;
    }
    
    weapon_name = model_dir.getName();

    // try loading weapon
    try {
//      time_head_model = System.currentTimeMillis();
      weapon = loadMD3(model_dir.getAbsolutePath(), weapon_name + WEAPON_MD3_SUFFIX + MD3_FORMAT);
      loadTexturesFromSkinfile(weapon, model_dir.getAbsolutePath(), weapon_name + WEAPON_MD3_SUFFIX + SKIN_FORMAT);
//      time_head_model = System.currentTimeMillis() - time_head_model;
    } catch (Exception e) {
      e.printStackTrace();
    }
    
    
    // try loading hand
    try {
//      h = System.currentTimeMillis();
      hand = loadMD3(model_dir.getAbsolutePath(), weapon_name + HAND_MD3_SUFFIX + MD3_FORMAT);
      loadTexturesFromSkinfile(hand, model_dir.getAbsolutePath(), weapon_name+"_hand.skin");
//      time_upper_model = System.currentTimeMillis() - time_upper_model;
    } catch (Exception e) {
      e.printStackTrace();
    }
    
    // try loading flash
    try {
//      time_lower_model = System.currentTimeMillis();
      flash = loadMD3(model_dir.getAbsolutePath(), weapon_name + FLASH_MD3_SUFFIX + MD3_FORMAT);
      loadTexturesFromSkinfile(flash, model_dir.getAbsolutePath(), weapon_name + FLASH_MD3_SUFFIX + SKIN_FORMAT);
//      time_lower_model = System.currentTimeMillis() - time_lower_model;
    } catch (Exception e) {
      e.printStackTrace();
    }
    
    // try loading barrel
    try {
//      time_lower_model = System.currentTimeMillis();
      barrel = loadMD3(model_dir.getAbsolutePath(), weapon_name + BARREL_MD3_SUFFIX + MD3_FORMAT);
      loadTexturesFromSkinfile(barrel, model_dir.getAbsolutePath(), weapon_name + BARREL_MD3_SUFFIX + SKIN_FORMAT);
//      time_lower_model = System.currentTimeMillis() - time_lower_model;
    } catch (Exception e) {
      e.printStackTrace();
    }
    
    
    time = System.currentTimeMillis() - time;
    System.out.println("LOADED WEAPON: \""+model_dir.getName() +"\"   (loadingtime: "+time+" ms)");
//    System.out.println("head:  "+time_head_model+" ms");
//    System.out.println("upper: "+time_upper_model+" ms");
//    System.out.println("lower: "+time_lower_model+" ms");
    
    
    

    makeNodeSystem();
    return true;
  }
  
  

  @Override
  public final void makeNodeSystem(){
    n_hand   = new NodeState(hand  ); n_hand   .setParentNode(null,  null );
    n_weapon = new NodeState(weapon); n_weapon .setParentNode(n_hand,   "tag_weapon" );
    n_flash  = new NodeState(flash ); n_flash  .setParentNode(n_weapon, "tag_flash"  );
    n_barrel = new NodeState(barrel); n_barrel .setParentNode(n_weapon, "tag_barrel" );
    n_hand.setAsTopLevelNode();
    node_system = n_hand.getTopLevelNode();
  }
  
  
  @Override
  public final NodeState getNodeSystem(){
    return node_system;
  }

  public final NodeState getNodeHand(){
    return n_hand;
  }
  public final NodeState getNodeWeapon(){
    return n_weapon;
  }
  public final NodeState getNodeFlash(){
    return n_flash;
  }
  public NodeState getNodeBarrel(){
    return n_barrel;
  }
  
  

  
  public final void setHand(Q3_Object md3){
    this.hand = md3;
    n_hand.setObject(this.hand);
  }
  public final void setWeapon(Q3_Object md3){
    this.weapon = md3;
    n_weapon.setObject(this.weapon);
  }
  public final void setFlash(Q3_Object md3){
    this.flash = md3;
    n_flash.setObject(this.flash);
  }
  public final void setBarrel(Q3_Object md3){
    this.barrel = md3;
    n_barrel.setObject(this.barrel);
  }
  
  public final Q3_Object getHand(){
    return hand;
  }
  public final Q3_Object getWeapon(){
    return weapon;
  }
  public final Q3_Object getFlash(){
    return flash;
  }
  public final Q3_Object getBarrel(){
    return barrel;
  }
  
  
  @Override
  public void drawModel(int frame_IDX){
    node_system.updateNodeSystem(frame_IDX);
    node_system.drawChilds();
  }
  
  
  
}
