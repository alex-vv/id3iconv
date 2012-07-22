// BinaryFrame
// $Id: BinaryFrame.java,v 1.1 2003/09/02 00:53:17 zf Exp $
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
 * Used for binary frames
 * (a frame with only a <binary data>).
 */
class BinaryFrame
{
    public BinaryFrame(ID3v2 id3v2, String type, TagContent content, boolean use_compression)
    {
	this.id3v2 = id3v2;
	this.type = type;
	this.content = content;
	this.use_compression = use_compression;

	byte []v2cont = content.getBinaryContent();
	try {
	    frame = new ID3v2Frame(type, v2cont, false, false, false, 
				   (use_compression ? ID3v2Frame.DO_COMPRESS : ID3v2Frame.NO_COMPRESSION), 
				   (byte)0, (byte)0);
	} catch (ID3v2DecompressionException e) {
	    // only occurs when using ID3v2Frame with compressed content, so ignore it
	}
    }
    

    /**
     * Write frame to ID3v2 tag
     */
    public void write()
    {
	// remove frame(s)
	try {
	    id3v2.removeFrame(type);
	} catch (ID3v2Exception e) {
	}

	// store frame
	id3v2.addFrame(frame);
    }
    

    /**
     * Read content from ID3v2 tag.
     */
    public static TagContent read(ID3v2 id3v2, String type) throws FrameDamagedException
    {
	TagContent ret = new TagContent();
	
	try {
	    byte []v2cont;
	    v2cont = ((ID3v2Frame)(id3v2.getFrame(type).elementAt(0))).getContent();
	    ret.setContent(v2cont);
	} catch (ID3v2Exception e) {
	    // no info
	} catch (Exception e) {
	    throw new FrameDamagedException();
	} 
	
	return ret;
    }


    protected ID3v2 id3v2;
    protected String type;
    protected TagContent content;
    protected boolean use_compression;
    protected ID3v2Frame frame;

} // class BinaryFrame


