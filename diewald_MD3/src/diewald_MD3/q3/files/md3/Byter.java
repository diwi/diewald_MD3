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

import java.nio.ByteOrder;

public final class Byter {
  private byte[] b;
  private int idx = 0;
  private int total_size = 0;
  
  protected int byte_reading_counter = 0;
  protected int byte_writing_counter = 0;
  
  public ByteOrder byte_order = ByteOrder.BIG_ENDIAN;
  

  public Byter(byte[] bytes) throws Exception{
    wrapBytes(bytes);
  }
  
  private final void wrapBytes(byte[] bytes) throws Exception{
    idx = 0;
    total_size = 0;
    b = bytes;
    if( b == null)
      throw new Exception("(!CORRPUT MD3!): bytes = null");
     
    total_size = b.length;
    byte_reading_counter = 0;
    byte_writing_counter = 0;
  }
  
  
  public final byte[] getBytes(){
    return b;
  }
  
  
  
  
  
  
  
  
  //----------------------------------------------------------------------------
  //----------------------------------------------------------------------------
  //                             FLOAT
  //----------------------------------------------------------------------------
  //----------------------------------------------------------------------------
  
  
  //----------------------------------------------------------------------------
  // getFloat
  public final float getFloat(final int offset){
    return Float.intBitsToFloat(getInteger(offset));
  }
  
  //----------------------------------------------------------------------------
  // setFloat
  public final void setFloat(final int offset, final float value){
    setInteger(offset, Float.floatToIntBits(value));
  }
  
  
  

  
  
  
  
  
  
  
  
  //----------------------------------------------------------------------------
  //----------------------------------------------------------------------------
  //                             INTEGER
  //----------------------------------------------------------------------------
  //----------------------------------------------------------------------------
  
  
  //----------------------------------------------------------------------------
  // getInteger
  public final int getInteger(final int offset) {
    idx += offset;
    byte_reading_counter+= 4;
    if( byte_order == ByteOrder.BIG_ENDIAN )
      return (b[idx++]& 0xff) << 24 | (b[idx++]& 0xff) << 16 | (b[idx++]& 0xff) <<  8 | (b[idx++]& 0xff);
    else 
      return (b[idx++]& 0xff) <<  0 | (b[idx++]& 0xff) <<  8 | (b[idx++]& 0xff) << 16 | (b[idx++]& 0xff)<<24;
  }
  
  
  
  //----------------------------------------------------------------------------
  // setInteger
  public final void setInteger(final int offset, final int value){
    idx += offset;
    byte_writing_counter+= 4;
    if( byte_order == ByteOrder.BIG_ENDIAN ){
      b[idx++] = (byte)((value >> 24)& 0xFF);
      b[idx++] = (byte)((value >> 16)& 0xFF);
      b[idx++] = (byte)((value >>  8)& 0xFF);
      b[idx++] = (byte)((value >>  0)& 0xFF);
    }
    else {
      b[idx++] = (byte)((value >>  0)& 0xFF);
      b[idx++] = (byte)((value >>  8)& 0xFF);
      b[idx++] = (byte)((value >> 16)& 0xFF);
      b[idx++] = (byte)((value >> 24)& 0xFF);
    }
  }
  
  
  
  
  
  
  
  
  
  
  //----------------------------------------------------------------------------
  //----------------------------------------------------------------------------
  //                             SHORT
  //----------------------------------------------------------------------------
  //----------------------------------------------------------------------------
  
  //----------------------------------------------------------------------------
  // getShort
  public final short getShort(final int offset){
    idx += offset;
    byte_reading_counter+= 2;
    if( byte_order == ByteOrder.BIG_ENDIAN )
      return (short)((b[idx++]& 0xff) << 8 | (b[idx++]& 0xff) << 0);
    else 
      return (short)((b[idx++]& 0xff) << 0 | (b[idx++]& 0xff) << 8);
  }
  
  //----------------------------------------------------------------------------
  // setShort
  public final void setShort(final int offset, final short value){
    idx += offset;
    byte_writing_counter+= 2;
    if( byte_order == ByteOrder.BIG_ENDIAN ){
      b[idx++] = (byte)((value >>  8)& 0xFF);
      b[idx++] = (byte)((value >>  0)& 0xFF);
    }else {
      b[idx++] = (byte)((value >>  0)& 0xFF);
      b[idx++] = (byte)((value >>  8)& 0xFF);
    }
  }
  
  
  
  
  //----------------------------------------------------------------------------
  //----------------------------------------------------------------------------
  //                             CHAR
  //----------------------------------------------------------------------------
  //----------------------------------------------------------------------------
  
  //----------------------------------------------------------------------------
  // getUByteAsChar
  public final char getUByteAsChar(final int offset){
    idx += offset;
    byte_reading_counter+= 1;
    return (char)(b[idx++] & 0xff);
  }
  
  //----------------------------------------------------------------------------
  // getByteAsChar
  public final char getByteAsChar(final int offset){
    idx += offset;
    byte_reading_counter+= 1;
    return (char)(b[idx++]);
  }
  
  public final void setChar(final int offset, final char value){
    idx += offset;
    byte_writing_counter+= 1;
    b[idx++] = (byte) value;
  }
  
  
  
  
  
  
  
  
  
  //----------------------------------------------------------------------------
  //----------------------------------------------------------------------------
  //                             CHAR-ARRAY
  //----------------------------------------------------------------------------
  //----------------------------------------------------------------------------
  
  //----------------------------------------------------------------------------
  // getUByteAsCharArray
  public final char[] getUByteAsCharArray(final int offset, char[] chars, final int length){
    idx += offset;
    if( chars == null )
      chars = new char[length];
    byte_reading_counter += length;
    for(int i = 0; i < chars.length; i++){
      chars[i] = (char) (b[idx++] & 0xff);
    }
    return chars;
  }
  
  //----------------------------------------------------------------------------
  // getCharArray
  public final char[] getByteCharArray(final int offset, char[] chars, final int length){
    idx += offset;
    if( chars == null)
      chars = new char[length];
    byte_reading_counter += length;
    for(int i = 0; i < chars.length; i++){
      chars[i] = (char) b[idx++];
    }
    return chars;
  }
  
  
  //----------------------------------------------------------------------------
  // setCharArray
  public final void setCharArray(final int offset, final char[] c, final int length){
    idx += offset;
    byte_writing_counter += length;
    for(int i = 0; i < length; i++){
      if( c!= null && i < c.length )
        b[idx++] = (byte) c[i];
      else
        b[idx++] = 0;
    }
    b[idx-1] = 0; //terminate with 0
  }
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  //----------------------------------------------------------------------------
  //----------------------------------------------------------------------------
  //                             STRING
  //----------------------------------------------------------------------------
  //----------------------------------------------------------------------------
  
  //----------------------------------------------------------------------------
  // getString
  public final String getString(final int offset, final int length){
    char[] c = new char[length];
    getUByteAsCharArray( offset, c, length);
    return new String(c);
  }
  
  //----------------------------------------------------------------------------
  // setString
  public final void setString(final int offset, String value, final int length){
    setCharArray(offset, value.toCharArray(), length);
  }
  
  
  
  
  
  //----------------------------------------------------------------------------
  // get/set Int3
  public final int[] getInt3( int[] values, final int length){
    if( values == null || values.length != length)
      values = new int[length];
    for(int i = 0; i < length; i++)
      values[i] = getInteger(0);
    return values;
  }
  public final void setInt3( int[] values, final int length){
    if( values == null || values.length != length)
      values = new int[length];
    for(int i = 0; i < length; i++)
      setInteger(0, values[i]);
  }
  
  
  //----------------------------------------------------------------------------
  // get/set Short3
  public final short[] getShort3(short[] values, final int length){
    if( values == null || values.length != length)
      values = new short[length];
    for(int i = 0; i < length; i++)
      values[i] = getShort(0);
    return values;
  }
  public final void setShort3(short[] values, final int length){
    if( values == null || values.length != length)
      values = new short[length];
    for(int i = 0; i < length; i++)
      setShort(0, values[i]);
  }
  
  
  //----------------------------------------------------------------------------
  // get/set Float3
  public final float[] getFloat3(float[] values,  final int length){
    if( values == null || values.length != length)
      values = new float[length];
    for(int i = 0; i < length; i++)
      values[i] = getFloat(0);
    return values;
  } 
  public final void setFloat3(float[] values,  final int length){
    if( values == null || values.length != length)
      values = new float[length];
    for(int i = 0; i < length; i++)
      setFloat(0, values[i]);
  } 
  
  
  
  
  

  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  public final Byter forward(final int offset){
    idx += offset;
    return this;
  }
  public final Byter backward(final int offset){
    idx -= offset;
    return this;
  }
  
  public final int available(){
    return total_size - idx;
  }
  public final int totalSize(){
    return total_size;
  }
  
  
  public final int getPos(){
    return idx;
  }
  public final void setPos(final int newpos){
    idx = newpos;
  }
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  //conversion
  public static final long convert_UInteger2Long(int v){
    return v & 0xffffffffL;
  }
  
  public static final int convert_UShort2Integer(short v){
    return v & 0xffff;
  }
 

}
