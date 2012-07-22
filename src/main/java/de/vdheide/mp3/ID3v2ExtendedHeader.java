// ID3v2ExtendedHeader.java
//
// $Id: ID3v2ExtendedHeader.java,v 1.1 2003/09/02 00:53:17 zf Exp $
//
// de.vdheide.mp3: Access MP3 properties, ID3 and ID3v2 tags
// Copyright (C) 1999 Jens Vonderheide <jens@vdheide.de>
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Library General Public
// License as published by the Free Software Foundation; either
// version 2 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Library General Public License for more details.
//
// You should have received a copy of the GNU Library General Public
// License along with this library; if not, write to the
// Free Software Foundation, Inc., 59 Temple Place - Suite 330,
// Boston, MA  02111-1307, USA.

/**
 * This class contains an ID3v2 extended header
 */

package de.vdheide.mp3;

import java.io.InputStream;
import java.io.IOException;

public class ID3v2ExtendedHeader
{

  /********** Constructors **********/

  /**
   * Creates an extended header with preferences
   *
   * @params head Header an array of bytes
   */
  public ID3v2ExtendedHeader()
    {
      this(false, 0, 0);
    }


  /**
   * Creates an extended header
   *
   * @param use_crc Use CRC?
   * @param crc CRC of frames (will be set to 0 if <code>use_crc</code> == false)
   * @param padding_size Size of padding
   */
  public ID3v2ExtendedHeader(boolean use_crc, int crc, int padding_size)
    {
      crc_present = use_crc;
      if (crc_present == true)
	{
	  this.crc = crc;
	}
      else
	{ 
	  this.crc = 0;
	}
      this.padding_size = padding_size;
    }


  /**
   * Creates an extended header from a stream.
   * Stream must be positioned to
   * the first byte of the extended header.
   *
   * @param in Stream to read from
   * @param use_unsynch If true, unsynchronization is used
   * @exception IOException If an I/O error occurs
   */
  public ID3v2ExtendedHeader(InputStream in) throws IOException
    {
      // read ext header
      byte []head = new byte[10];
      in.read(head);

      // decode size
      int size = (int)(new pri.nightmare.utils.Bytes(head, 0, 4).getValue());

      // decode extended flags
      if (((head[4] & 0xff) & FLAG_CRC_PRESENT) > 0)
	{
	  crc_present = true;
	}

      // decode size of padding
      padding_size = (int)(new pri.nightmare.utils.Bytes(head, 6, 4).getValue());

      // read crc if present
      if (crc_present == true)
	{
	  byte []crc_array = new byte[4];
	  in.read(crc_array);
	  crc = (int)new pri.nightmare.utils.Bytes(crc_array).getValue();
	}

    }


  /********** Public methods **********/

  /**
   * @return Size of extended header
   */
  public int getSize()
    {
      return (crc_present==true ? 10 : 6);
    }


  /**
   * @return Size of padding
   */
  public int getPaddingSize()
    {
      return padding_size;
    }


  /**
   * Set size of padding
   * 
   * @param size Size of padding
   */
  public void setPaddingSize(int size)
    {
      padding_size = size;
    }


  /**
   * @return true if CRC is used
   */
  public boolean hasCRC()
    {
      return crc_present;
    }


  /**
   * Set if CRC is used
   *
   * @param crc True: CRC is used
   */
  public void setHasCRC(boolean act)
    {
      if (act = false)
	{
	  crc = 0;
	}
      crc_present = act;
    }


  /**
   * @return CRC stored in extended header
   */
  public long getCRC()
    {
      return crc;
    }


  /**
   * Set CRC. This automatically sets <code>hasCRC</code>
   * to return <code>true</code>.
   *
   * @param crc CRC to set
   */
  public void setCRC(int crc)
    {
      this.crc = crc;
      crc_present = true;
    }
  
  
  /**
   * Returns an array of bytes representing this extended header.
   * <p>
   * Note: This is not unsynchronized!
   *
   * @return Extended header as bytes, ready to write
   */
  public byte []getBytes()
    {
      byte ret[];

      if (crc_present == true)
	{
	  // extended header needs 10 + 4 bytes
	  ret = new byte[14];

	  // write size
	  ret[0] = 0;
	  ret[1] = 0;
	  ret[2] = 0;
	  ret[3] = 10;
	  
	  // write flags
	  ret[4]=(byte)(1 << 7);
	  ret[5]=0;
	}
      else
	{
	  ret = new byte[10];

	  // write size
	  ret[0] = 0;
	  ret[1] = 0;
	  ret[2] = 0;
	  ret[3] = 10;

	  // write flags
	  ret[4] = 0;
	  ret[5] = 0;
	}

      // write size of padding
      byte []pad_byte = (new pri.nightmare.utils.Bytes(padding_size, 4)).getBytes();
      System.arraycopy(pad_byte, 0, ret, 6, 4);

      // write crc if present
      if (crc_present == true)
	{
	  byte []crc_byte = (new pri.nightmare.utils.Bytes(crc, 4)).getBytes();
	  System.arraycopy(crc_byte, 0, ret, 10, 4);
	}

      return ret;
    }



  /********** Private variables **********/

  private int padding_size = 0;
  private boolean crc_present = false;
  private int crc = 0;

  private final static byte FLAG_CRC_PRESENT = (byte)(1<<7);

}
