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

import diewald_MD3.q3.displaystyle.default_styles.DefaultDisplayStyle;
import diewald_MD3.q3.displaystyle.interfaces.BoundingboxStyle;
import diewald_MD3.q3.displaystyle.interfaces.DisplayStyle;
import diewald_MD3.q3.displaystyle.interfaces.NormalStyle;
import diewald_MD3.q3.displaystyle.interfaces.ShadingStyle;
import diewald_MD3.q3.displaystyle.interfaces.TagStyle;
import diewald_MD3.q3.displaystyle.interfaces.VertexStyle;
import diewald_MD3.q3.displaystyle.interfaces.WireframeStyle;
import diewald_MD3.q3.files.md3.MD3_Tag;

import processing.core.PApplet;
import processing.core.PMatrix3D;


public class NodeState {
  
  // child/parent list
  private NodeState parent_node = null;
  private final ArrayList<NodeState> child_nodes = new ArrayList<NodeState>();
  
  // wrapped md3-objecz
  private  Q3_Object md3;
  
  //transformation matrix
  private final PMatrix3D transformation;
  
  // tag-connection to parent
  private String tag_name = ""; //name of the tag, that builds the connection to its parent
  private int tag_IDX_current = -1;
  private int tag_IDX_parent  = -1;
  
  // current frame, the transformation is based on
  private int frame_IDX_cur   = -1;
  
  // display style, used for drawing
  private DisplayStyle display_style;
  
  


  
  //----------------------------------------------------------------------------
  // CONSTRUCTOR
  //----------------------------------------------------------------------------
  public NodeState(Q3_Object md3){
    this.md3 = md3;
    this.transformation = new PMatrix3D();
    if( md3 != null){
      display_style = new DefaultDisplayStyle(md3.parent_p5);
    }
  }
  

  
  
  

  
  
  
  
  //----------------------------------------------------------------------------
  // SET/GET OBJECT
  //----------------------------------------------------------------------------
  public final boolean setObject(Q3_Object md3_new){

    String tmp_tag_names_new[] = md3_new.getNamesOfTags();
    String tmp_tag_names_cur[] = this.md3.getNamesOfTags();
    
   // check if the current childs tagnames are available within the new object
    for( String tag_cur : tmp_tag_names_cur ){
      boolean match = false;
      for( String tag_new : tmp_tag_names_new ){
        if( tag_new.equals(tag_cur)){
          match = true;
          break;
        }
      }
      if( !match ){
        return false;
      }
    }
    //ok, all tags exists in both objects
    
    this.md3 = md3_new;
    
    //update this tag_IDX_current
    if( this.parent_node != null)
      this.tag_IDX_current = this.md3.getTagByName(0, tag_name).INDEX;
    
    // update childs tag_IDX_parent
    for( NodeState child : child_nodes){
      child.tag_IDX_parent =  this.md3.getTagByName(0, child.tag_name).INDEX;
    }

    return true;
  }
  
  public final Q3_Object getObject(){
    return md3;
  }
  
  
  
  
  
  //----------------------------------------------------------------------------
  // MAKE/BREAK CONNECTIONS
  //----------------------------------------------------------------------------
  private final void clearParentConnection(){
    if( this.parent_node != null){
      this.parent_node.child_nodes.remove(this);
    }
    this.parent_node     = null;
    this.tag_name        = "";
    this.tag_IDX_current = -1;
    this.tag_IDX_parent  = -1;
  }
  
  private final void makeParentConnection(NodeState new_parent_node, String tag_name, int tag_idx_cur, int tag_idx_par){
    this.parent_node     = new_parent_node;
    this.tag_name        = tag_name;
    this.tag_IDX_current = tag_idx_cur;
    this.tag_IDX_parent  = tag_idx_par;
    this.parent_node.child_nodes.add(this);
  }
  
  public final boolean setParentNode(NodeState new_parent_node, String tag_name){
    // nape parent node is null
    if( new_parent_node == null){
      clearParentConnection();
      return true;
    }
    // tagname is null
    if( tag_name == null ){
      return false;
    }
    
    if( new_parent_node == this ){ //STUPID, but DAU's welcome  ;)
      return false;
    }
    
    // everything stays the same
    if( this.parent_node == new_parent_node && tag_name.equals(this.tag_name)){
      return true;
    }
      
    // check if tag_name exists in in this and parent
    if( this.md3 == null || new_parent_node.md3 == null){
      return false;
    }
    MD3_Tag tag_current =            this.md3.getTagByName(0, tag_name);
    MD3_Tag tag_parent  = new_parent_node.md3.getTagByName(0, tag_name);
    if( tag_current == null || tag_parent == null) {
      return false;
    }

    // ready for new connection:
    // delete old connection
    clearParentConnection();
    // make new connection
    makeParentConnection(new_parent_node, tag_name, tag_current.INDEX, tag_parent .INDEX);
    return true;
  }

  public final NodeState setAsTopLevelNode(){
    if( parent_node != null ){
      parent_node.setToChildOf(this, this);
      this.setParentNode(null, null);
    }
    return this;
  }

  public final NodeState getTopLevelNode(){
    NodeState top_level_node = this;
    while( top_level_node.parent_node != null ){
      top_level_node = top_level_node.parent_node;
      if( top_level_node == this ) // !!!! circular nodesystem
        return this;               // so "this" is returned as the toplevelnode
    }
    return top_level_node;
  }
  
  private final void setToChildOf(NodeState new_parent, NodeState new_top_level ){
    if( parent_node != null && this != new_top_level){
      parent_node.setToChildOf(this, new_top_level);
    }
    this.setParentNode(new_parent, new_parent.tag_name);
  }
  
  public final boolean isInSameNodeSystemAs(NodeState node_2_find){
    return getTopLevelNode().isParentOf(node_2_find);
  }

  public final boolean isParentOf(NodeState node_2_find){
    if( this == node_2_find )
      return true;
    
    for( NodeState node : child_nodes){
      if ( node.isParentOf(node_2_find) )
        return true;
    }
    return false;
  }
  
  public final boolean isChildOf(NodeState node_2_find){
    NodeState current = this;
    
    while( current.parent_node != null ){
      current = current.parent_node;
      if( current == this ) // !!!! circular nodesystem
        return false;       // so node_2_find cant be in list
      if( current == node_2_find )
        return true;
    }
    return false;
  }

  public final NodeState getParentNode(){
    return parent_node;
  }
  public final ArrayList<NodeState> getChilds(){
    return child_nodes;
  }
  
  
  
  

  

  
  
  
  //----------------------------------------------------------------------------
  // PRINT TREE
  //----------------------------------------------------------------------------
  public void printNodeSystem(){
    getTopLevelNode().printChilds(1);
  }
  
  public void printChilds(int hierachy_indent){
    String tag  = tag_name == "" ? "null": tag_name;
    File f = md3.getMD3_File().file;
    String path = "../"+f.getParentFile().getName() +"/"+f.getName();
    
    System.out.printf("%"+hierachy_indent+"s%s (%s)  -  %s\n", ".", md3.getName(), tag, path);
    
    hierachy_indent += 3;
    for( NodeState node : child_nodes){
      node.printChilds(hierachy_indent);
    }
  }
  
  

  
  
  
  
  
  
  

  //----------------------------------------------------------------------------
  // FRAME_IDX
  //----------------------------------------------------------------------------
  public final void setNodeSystemFrameIDX(int frame_IDX){
    getTopLevelNode().setChildsFrameIDX(frame_IDX);
  }
  public final void setChildsFrameIDX(int frame_IDX){
    setFrameIDX(frame_IDX);
    for( NodeState node : child_nodes){
      node.setChildsFrameIDX(frame_IDX);
    }
  }
  
  public final void setFrameIDX(int frame_IDX){
    if( frame_IDX < 0 || frame_IDX >= md3.getNumberOfFrames())
      frame_IDX = 0;
    
//    frame_IDX_prev = frame_IDX_cur;
    frame_IDX_cur = frame_IDX;
  }
  
  public final int getFrameIDX(){
    return frame_IDX_cur;
  }
  
  
  
  
  //----------------------------------------------------------------------------
  // UPDATE - TRANSFORMATION
  //----------------------------------------------------------------------------
  public NodeState updateNodeSystem(){
    return getTopLevelNode().updateChilds();
  }
  public NodeState updateNodeSystem(int frame_IDX){
    return getTopLevelNode().updateChilds(frame_IDX);
  }
  public NodeState updateChilds(){
    updateTransformation();
    for( NodeState node : child_nodes){
      node.updateChilds();
    }
    return this;
  }
  
  public NodeState updateChilds(int frame_IDX){
    updateTransformation(frame_IDX);
    for( NodeState node : child_nodes){
      node.updateChilds(frame_IDX);
    }
    return this;
  }
  
  public final PMatrix3D updateTransformation(){
    if ( parent_node == null )
      return transformation;
    
    PMatrix3D tag_matrix_parent  = parent_node.getLocalTagMatrix( tag_IDX_parent );
    PMatrix3D tag_matrix_current = getLocalTagMatrixInverted( tag_IDX_current );

    transformation.set(parent_node.transformation);
    transformation.apply(tag_matrix_parent);
    transformation.apply(tag_matrix_current);
    return null;
  }
  
  
  public final PMatrix3D updateTransformation( int frame_IDX){
    setFrameIDX(frame_IDX);
    return updateTransformation();
  }
  
  protected final PMatrix3D getLocalTagMatrix( int tag_idx ){
    return md3.getTags(frame_IDX_cur)[tag_idx].getMatrix();
  }
  
  protected final PMatrix3D getLocalTagMatrixInverted( int tag_idx ){
    return md3.getTags(frame_IDX_cur)[tag_idx].getMatrixInverted();
  }
  
  public void setTransformation(PMatrix3D transformation){
    transformation.set(transformation);
  }
  
  public PMatrix3D getTransformation(){
    return transformation;
  }
  
  
  
  
  
  
  
  //----------------------------------------------------------------------------
  // DISPLAY STYLE
  //----------------------------------------------------------------------------
  public final void setNodeSystemDisplayStyle(DisplayStyle display_style){
    getTopLevelNode().setChildsDisplayStyle(display_style);
  }
  
  public final void setChildsDisplayStyle(DisplayStyle display_style){
    setDisplayStyle(display_style);
    for( NodeState node : child_nodes){
      node.setChildsDisplayStyle(display_style);
    }
  }
  public final void setDisplayStyle(DisplayStyle display_style){
    if( display_style != null ){
      this.display_style = display_style;
    }
  }
  
  public final DisplayStyle getDisplayStyle(){
    return display_style;
  }
  
  
  
  
  
  
  //----------------------------------------------------------------------------
  // DRAW NODES
  //----------------------------------------------------------------------------
  public final void drawNodeSystem(){
    getTopLevelNode().drawChilds();
  }
  
  public final void drawChilds(){
    draw();
    for( NodeState node : child_nodes){
      node.drawChilds();
    }
  }
  
  public void draw( ){
    if ( display_style.styleNormals()    .display() ) drawNormals();
    if ( display_style.styleShaded()     .display() ) drawShaded();
    if ( display_style.styleTags()       .display() ) drawTags();
    if ( display_style.styleVertices()   .display() ) drawVertices();
    if ( display_style.styleWireframe()  .display() ) drawWireframe();
    if ( display_style.styleBoundingbox().display() ) drawBoundingbox();
  }

  public void drawShaded(){
    ShadingStyle style = display_style.styleShaded();
    
    PApplet p5 = md3.parent_p5;
    p5.pushStyle();
    p5.pushMatrix();
    
      p5.applyMatrix(transformation);

      md3.drawSurfaceShaded(this.frame_IDX_cur);
      
    p5.popMatrix();
    p5.popStyle();
  }
  public void drawWireframe(){
    WireframeStyle style = display_style.styleWireframe();
    
    PApplet p5 = md3.parent_p5;
    p5.pushStyle();
    p5.pushMatrix();
    
      p5.applyMatrix(transformation);
      
      p5.stroke      ( style.getColor() );
      p5.strokeWeight( style.getThickness() );
      
      md3.drawSurfaceWireFrame(this.frame_IDX_cur);
      
    p5.popMatrix();
    p5.popStyle();
  }
  public void drawTags(){
    TagStyle style = display_style.styleTags();
    PApplet p5 = md3.parent_p5;
    p5.pushStyle();
    p5.pushMatrix();
    
      p5.applyMatrix(transformation);
      
      p5.strokeWeight( style.getThickness() );
      float scale = style.getScale();
      
      md3.drawTags (this.frame_IDX_cur, scale);
      
    p5.popMatrix();
    p5.popStyle();
  }
  public void drawNormals(){
    NormalStyle style = display_style.styleNormals();
    PApplet p5 = md3.parent_p5;
    p5.pushStyle();
    p5.pushMatrix();
    
      p5.applyMatrix(transformation);
      
      p5.stroke      ( style.getColor() );
      p5.strokeWeight( style.getThickness() );
      float scale = style.getScale();
      
      md3.drawNormals(this.frame_IDX_cur, scale);
      
    p5.popMatrix();
    p5.popStyle();
  }
  public void drawVertices(){
    VertexStyle style = display_style.styleVertices();
    PApplet p5 = md3.parent_p5;
    p5.pushStyle();
    p5.pushMatrix();
    
      p5.applyMatrix(transformation);
      
      p5.stroke      ( style.getColor() );
      p5.strokeWeight( style.getThickness() );
 
      md3.drawSurfaceVertices(this.frame_IDX_cur);
      
    p5.popMatrix();
    p5.popStyle();
  }
  
  public void drawBoundingbox(){
    BoundingboxStyle style = display_style.styleBoundingbox();
    PApplet p5 = md3.parent_p5;
    p5.pushStyle();
    p5.pushMatrix();
    
      p5.applyMatrix(transformation);
      
      p5.stroke      ( style.getColor() );
      p5.strokeWeight( style.getThickness() );
      
      md3.drawBoundingbox(this.frame_IDX_cur);
      
    p5.popMatrix();
    p5.popStyle();
  }
  
}
