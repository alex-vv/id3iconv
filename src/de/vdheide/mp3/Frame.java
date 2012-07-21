// Frame.java
// $Id: Frame.java,v 1.1 2003/09/02 00:53:17 zf Exp $
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

package de.vdheide.mp3;

/**
 * A wrapper for the ID3v2Frame class. Can only build a frame and set it.
 */
class Frame
{
    /**
     * Constructor.
     *
     * @param id3v2 ID3v2 tag to add frame to. Must already be connected to file.
     * @param type ID3v2 frame type
     * @param content Content as byte array
     * @param tag_preservation If true, set flag so that frame is removed if unknown to programs
     *                         and tag is changed
     * @param file_preservation Same as <code>tag_preservation</code> but frame is removed if
     *                          file is changed.
     * @param use_compression Should frame be compressed?
     */
    public Frame(ID3v2 id3v2, String type, byte []content, boolean tag_preservation, boolean file_preservation, 
		 boolean use_compression)
    {
	try {
	    frame =  new ID3v2Frame(type, content, tag_preservation, file_preservation, false, 
				    (use_compression ? ID3v2Frame.DO_COMPRESS : ID3v2Frame.NO_COMPRESSION),
				    (byte)0, (byte)0);
	} catch (ID3v2DecompressionException e) {}

	this.id3v2 = id3v2;
	this.type = type;
    }

    
    /**
     * Remove all frames of this type from ID3v2 tag and write frame
     */
    public void write()
    {
	// remove frame
	try {
	    id3v2.removeFrame(type);
	} catch (ID3v2Exception e) {
	}

	// store frame
	id3v2.addFrame(frame);
    }


    public static byte[] read(ID3v2 id3v2, String type)
    {
	try {
	    return ((ID3v2Frame)(id3v2.getFrame(type).elementAt(0))).getContent();
	} catch (ID3v2Exception e) {
	    return null;
	}
    }

     
    protected ID3v2Frame frame;
    protected ID3v2 id3v2;
    protected String type;
}
