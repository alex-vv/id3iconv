// TextFrameEncoding.java
// $Id: TextFrameEncoding.java,v 1.1 2003/09/02 00:53:17 zf Exp $
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
 * Used for text ID3v2 frames which support encoding.
 * Supports get and set operations
 */
public class TextFrameEncoding extends TextFrame
{
    /**
     * Creates a new TextFrameEncoding with a given content
     * 
     * @param id3v2 ID3v2 tag
     * @param type Type of frame
     * @param content TagContent to write
     * @param use_compression Use compression?
     * @exception TagFormatException If text content field is null
     */
    public TextFrameEncoding(ID3v2 id3v2, String type, TagContent content, boolean use_compression) 
	throws TagFormatException
    {
	super(true, id3v2, type, content, use_compression);
    }


    /**
     * Read content from ID3v2 tag.
     *
     * @param encoding Use encoding?
     * @param id3v2 ID3v2 tag to read from
     * @param type Type of frame to read
     * @exception FrameDamagedException If frame is damaged (e.g. too short)
     */
    public static TagContent read(ID3v2 id3v2, String type) throws FrameDamagedException
    {
	return read(true, id3v2, type);
    }
    
}
