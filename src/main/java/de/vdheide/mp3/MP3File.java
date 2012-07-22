// MP3.java
// $Id: MP3File.java,v 1.1 2003/09/02 00:53:17 zf Exp $
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
 * Instances of this class contain an MP3 file, giving access to its
 * ID3 and ID3v2 tags and other mp3 properties.
 * <p>
 * It provides a common interface to both tags, e.g. <code>setTitle(title)</code>
 * updates the title field in both tags. When reading (e.g. <code>getTitle()</code>,
 * it tries to provide as much information as possible (this means returning the
 * ID3v2 infos if tag and requested field are present).
 * <p>
 * Information stored in tags is always returned as a <code>TagContent</code>, the
 * description of the respective get Methods state which fields are used.
 * The more complex frames are not parsed into fields, but rather returned as a
 * byte array. It is up to the user of this class to make sense of it. Usage of
 * a special decode class is recommended.
 * <p>
 * It is assumed that each ID3v2 frame is unique, as is the case for nearly all
 * frame types
 *
 * @author Jens Vonderheide <jens@vdheide.de>
 */

package de.vdheide.mp3;

import java.io.File;
import java.io.IOException;

//For the main() method
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileInputStream;

public class MP3File extends java.io.File
{
    /********** Constructors **********/

    /**
     * Creates a new instance.
     * Tag information is completely read the first time it is requested
     * and written after <code>update()</code>.
     *
     * @param filename File name
     * @exception IOException If I/O error occurs
     * @exception NoMP3FrameException If file does not contain at least one mp3 frame
     * @exception ID3v2WrongCRCException If the ID3v2 tag fails CRC
     * @exception ID3v2DecompressionException If the ID3v2 tag cannot be decompressed
     * @exception ID3v2IllegalVersionException If the ID3v2 tag has a wrong (too high) version
     */
    public MP3File(String filename) throws IOException, NoMP3FrameException, ID3v2WrongCRCException,
      ID3v2DecompressionException, ID3v2IllegalVersionException
    {
	super(filename);

	// read properties and tags
	prop  = new MP3Properties(this);
	id3   = new ID3(this);
	id3v2 = new ID3v2(this);
    }


    /**
     * Creates a MP3File instance that represents the file with the specified
     * name in the specified directory.
     * Tag information is completely read the first time it is requested
     * and written after <code>update()</code>.
     *
     * @param dir Directory
     * @param filename File name
     * @exception IOException If I/O error occurs
     * @exception NoMP3FrameException If file does not contain at least one mp3 frame
     * @exception ID3v2WrongCRCException If the ID3v2 tag fails CRC
     * @exception ID3v2DecompressionException If the ID3v2 tag cannot be decompressed
     * @exception ID3v2IllegalVersionException If the ID3v2 tag has a wrong (too high) version
     */
    public MP3File(File dir, String filename) throws IOException, NoMP3FrameException, ID3v2WrongCRCException,
      ID3v2DecompressionException, ID3v2IllegalVersionException
    {
	super(dir, filename);

	// read properties and tags
	prop  = new MP3Properties(this);
	id3   = new ID3(this);
	id3v2 = new ID3v2(this);
    }


    /**
     * Creates a File instance whose pathname is the pathname of the specified directory,
     * followed by the separator character, followed by the name
     * argument.
     * Tag information is completely read the first time it is requested
     * and written after <code>update()</code>.
     *
     * @param dir Name of directory
     * @param filename File name
     * @exception IOException If I/O error occurs
     * @exception NoMP3FrameException If file does not contain at least one mp3 frame
     * @exception ID3v2WrongCRCException If the ID3v2 tag fails CRC
     * @exception ID3v2DecompressionException If the ID3v2 tag cannot be decompressed
     * @exception ID3v2IllegalVersionException If the ID3v2 tag has a wrong (too high) version
     */
    public MP3File(String dir, String filename) throws IOException, NoMP3FrameException, ID3v2WrongCRCException,
      ID3v2DecompressionException, ID3v2IllegalVersionException
    {
	super(dir, filename);

	// read properties and tags
	prop  = new MP3Properties(this);
	id3   = new ID3(this);
	id3v2 = new ID3v2(this);
    }

    public static void main(String[] args)
    {
        TagContent theTagContent = null;
        String theFilename = null;
        String thePlaylistFilename = args[0];
        File thePlaylistFile = new File(thePlaylistFilename);
        int theCount = 0;
        MP3File theMP3File = null;

        System.out.println(new java.util.Date());
        System.out.println("Grabbing playlist...");

        try
        {
            BufferedReader theReader = new BufferedReader(new InputStreamReader(new FileInputStream(thePlaylistFile)));

            while (((theFilename = theReader.readLine()) != null) && theCount < 1000)
            {
                if (theFilename.startsWith("#") == false)
                {
	                try
                    {
                        theFilename = theFilename.trim();
                        theMP3File = new MP3File(theFilename);
                        theCount++;

                        System.out.println("**********");
                        System.out.println(theFilename);

                        theTagContent = theMP3File.getTitle();
                        System.out.println("Title: " + theTagContent.getTextContent());

                        theTagContent = theMP3File.getArtist();
                        System.out.println("Artist: " + theTagContent.getTextContent());

                        theTagContent = theMP3File.getAlbum();
                        System.out.println("Album: " + theTagContent.getTextContent());

                        theTagContent = theMP3File.getTrack();
                        System.out.println("Track: " + theTagContent.getTextContent());

                        //theTagContent = theMP3File.getTime();
                        //System.out.println("Time: " + theTagContent.getTextContent());

                        //theTagContent = theMP3File.getDate();
                        //System.out.println("Date: " + theTagContent.getTextContent());

                        System.out.println("Bitrate: " + theMP3File.getBitrate());
                        System.out.println("**********");
                        //System.out.println("\n");
	                }
                    catch (Exception e1)
                    {
                        System.out.println(e1.toString());
                    }
                }
            }

            System.out.println("Grabbed " + theCount + " MP3Files.");
        }
        catch (Exception e2)
        {
            System.out.println(e2.toString());
        }
    }

    /********** Public methods **********/


    /**
     * Commit information to file
     *
     * @exception ID3Exception If an error occurs when writing the ID3 tag
     * @exception ID3v2Exception If an error ocurrs when writing the ID3v2 tag
     */
    public void update() throws ID3Exception, ID3v2Exception
    {
	// write id3v1
	if (write_id3) {
	    try {
		id3.writeTag();
	    } catch (IOException e) {
		throw new ID3Exception();
	    }
	}

	// write id3v2
	if (write_id3v2) {
	    try {
		id3v2.update();
	    } catch (IOException e) {
		throw new ID3v2Exception();
	    }
	}

	// Properties are read only...
    }


    /**
     * Write ID3 tag?
     *
     * @param write_id3 True: Write ID3 tag on update
     */
    public void setWriteID3(boolean write_id3)
    {
	this.write_id3 = write_id3;
    }


    /**
     * Should an ID3 tag be written
     *
     * @return true if ID3 tag will be written on update
     */
    public boolean getWriteID3()
    {
	return write_id3;
    }


    /**
     * Write ID3v2 tag?
     *
     * @param write_id3v2 True: Write ID3v2 tag on update
     */
    public void setWriteID3v2(boolean write_id3v2)
    {
	this.write_id3v2 = write_id3v2;
    }


    /**
     * Should an ID3v2 tag be written?
     *
     * @return true if ID3v2 tag will be written on update
     */
    public boolean getWriteID3v2()
    {
	return write_id3v2;
    }


    /**
     * Use compression in ID3v2 tag?
     * Frames are compressed only when the compressed content is smaller
     * than the uncompressed content.
     *
     * @param use_compression True: Use compression
     */
    public void setUseCompression(boolean use_compression)
    {
	this.use_compression = use_compression;
    }


    /**
     * @return true if compression will be used in ID3v2 tag
     */
    public boolean getUseCompression()
    {
	return use_compression;
    }


    /**
     * Use CRC in ID3v2 tag?
     *
     * @param use_crc True: Use CRC
     */
    public void setUseCRC(boolean use_crc)
    {
	this.use_crc = use_crc;

	// inform id3v2 tag
	if (id3v2 != null) {
	    id3v2.setUseCRC(use_crc);
	}
    }


    /**
     * @return true if CRC will be used in ID3v2 tag
     */
    public boolean getUseCRC()
    {
	return use_crc;
    }


    /**
     * Use padding in ID3v2 tag?
     *
     * @param use_padding True: Use padding
     */
    public void setUsePadding(boolean use_padding)
    {
	this.use_padding = use_padding;

	// inform id3v2 tag
	if (id3v2 != null) {
	    id3v2.setUsePadding(use_padding);
	}
    }


    /**
     * @return true if padding will be used in ID3v2 tag
     */
    public boolean getUsePadding()
    {
	return use_padding;
    }


    /**
     * Use unsynchronization in ID3v2 tag?
     *
     * @param use_unsynch True: Use unsynchronization
     */
    public void setUseUnsynchronization(boolean use_unsynch)
    {
	this.use_unsynchronization = use_unsynch;

	// inform id3v2 tag
	if (id3v2 != null) {
	    id3v2.setUseUnsynchronization(use_unsynch);
	}
    }


    /**
     * @return true if unsynchronization will be used in ID3v2 tag
     */
    public boolean getUseUnsynchronization()
    {
	return use_unsynchronization;
    }


    // Read MP3 properties

    /**
     * @return MPEG level (1 or 2)
     */
    public int getMPEGLevel()
    {
	return prop.getMPEGLevel();
    }


    /**
     * @return Layer (1..3)
     */
    public int getLayer()
    {
	return prop.getLayer();
    }


    /**
     * @return Bitrate
     */
    public int getBitrate()
    {
	return prop.getBitrate();
    }


    /**
     * @return Samplerate
     */
    public int getSamplerate()
    {
	return prop.getSamplerate();
    }


    /**
     * Returns mode (mono, stereo, etc.) used in MP3 file
     * Better use constants from MP3Properties.
     *
     * @return Mode
     */
    public int getMode()
    {
	return prop.getMode();
    }


    /**
     * Returns emphasis used in MP3 file
     * Better use constants from MP3Properties.
     *
     * @return Emphasis
     */
    public int getEmphasis()
    {
	return prop.getEmphasis();
    }


    /**
     * @return Protection (CRC) set
     */
    public boolean getProtection()
    {
	return prop.getProtection();
    }


    /**
     * @return Private bit set?
     */
    public boolean getPrivate()
    {
	return prop.getPrivate();
    }


    /**
     * @return Padding set?
     */
    public boolean getPadding()
    {
	return prop.getPadding();
    }


    /**
     * @return Copyright set?
     */
    public boolean getCopyright()
    {
	return prop.getCopyright();
    }


    /**
     * @return Original?
     */
    public boolean getOriginal()
    {
	return prop.getOriginal();
    }


    /**
     * @return Length in seconds
     */
    public long getLength()
    {
	return prop.getLength();
    }




    // Tag information (for details see the ID3v2 informal standard)


    /**
     * Read album/movie/show title. Album is stored as text content.
     *
     * @return Album
     * @exception FrameDamagedException If frame is damaged (e.g. too short)
     */
    public TagContent getAlbum() throws FrameDamagedException
    {
	TagContent ret = TextFrameEncoding.read(id3v2, "TALB");
	if (ret.getTextContent() == null) {
	    try {
		ret.setContent(id3.getAlbum());
	    } catch (NoID3TagException e) {
		// do nothing, content just stays at null
	    }
	}

	return ret;
    }


    /**
     * Set album. Album is read from text content.
     *
     * @param album Album to set
     * @exception TagFormatException If information to set is not correctly formatted
     */
    public void setAlbum(TagContent album) throws TagFormatException
    {
	if (album.getTextContent()==null) {
	    throw new TagFormatException();
	}

	// write v1
	id3.setAlbum(album.getTextContent());

	// write v2
	(new TextFrameEncoding(id3v2, "TALB", album, use_compression)).write();
    }


    /**
     * Read BPM. BPM is stored as text content.
     *
     * @return BPM
     * @exception FrameDamagedException If frame is damaged (e.g. too short)
     */
    public TagContent getBPM() throws FrameDamagedException
    {
	return TextFrameEncoding.read(id3v2, "TBPM");
    }


    /**
     * Set BPM. BPM is read from text content.
     *
     * @param bpm BPM to set
     * @exception TagFormatException If information to set is not correctly formatted
     */
    public void setBPM(TagContent bpm) throws TagFormatException
    {
	if (bpm.getTextContent()==null || checkNumeric(bpm.getTextContent()) == false) {
	    throw new TagFormatException();
	}

	(new TextFrameEncoding(id3v2, "TBPM", bpm, use_compression)).write();
    }


    /**
     * Read composer(s), stored as text content.
     *
     * @return composer(s)
     * @exception FrameDamagedException If frame is damaged (e.g. too short)
     */
    public TagContent getComposer() throws FrameDamagedException
    {
	return TextFrameEncoding.read(id3v2, "TCOM");
    }


    /**
     * Set composer(s), read from text content.
     *
     * @param composer Composer(s) to set
     * @exception TagFormatException If information to set is not correctly formatted
     */
    public void setComposer(TagContent composer) throws TagFormatException
    {
	(new TextFrameEncoding(id3v2, "TCOM", composer, use_compression)).write();
    }


    /**
     * Read genre (type of music like "Soul", "Rock", etc.) stored as text content.
     * ID3v1.1 content is denoted by putting round brackets around the number (like (4)),
     * round brackets in text are escaped by ((.
     *
     * @return Album
     * @exception FrameDamagedException If frame is damaged (e.g. too short)
     */
    public TagContent getGenre() throws FrameDamagedException
    {
	TagContent ret = new TagContent();

	byte []v2cont = null;

	try {
	    v2cont = ((ID3v2Frame)(id3v2.getFrame("TCON").elementAt(0))).getContent();
	} catch (ID3v2Exception e) {}

	if (v2cont == null) {
	    // try id3v1
	    int v1cont;
	    try {
		v1cont = id3.getGenre();

		// convert id3v1 info to new format
		ret.setContent("(" + v1cont + ")");
	    } catch (ID3Exception e) {
		// no info
	    }

	} else {
	    // use v2
	    Parser parse = new Parser(v2cont, true);
	    try {
		ret.setContent(parse.parseText());
	    } catch (ParseException e) {
		throw new FrameDamagedException();
	    }
	}

	return ret;
    }


    /**
     * Set genre, read from text content.
     * ID3v1.1 genre is denoted by putting round brackets around the number (like (4)),
     * round brackets in text are escaped by ((.
     *
     * @param genre Genre to set
     * @exception TagFormatException If information to set is not correctly formatted
     */
    public void setGenre(TagContent genre) throws TagFormatException
    {
	if (genre.getTextContent() == null) {
	    throw new TagFormatException();
	}

	// write v1

	// parse genre
	boolean found = false;
	int brackpos = 0;
	while ((brackpos != -1) && (found == false)) {
	    brackpos = genre.getTextContent().indexOf('(', brackpos);
	    if (brackpos != -1) {
		// check if next character is a number
		if (Character.isDigit(genre.getTextContent().charAt(brackpos+1)) == true) {
		    // found it
		    // search for )
		    int brackclose = genre.getTextContent().indexOf(')', brackpos);
		    if (brackclose == -1) {
			// something went wrong...
		    } else {
			// parse to int
			try {
			    Integer par =
				new Integer(genre.getTextContent().substring(brackpos + 1, brackclose));
			    // write genre
			    try {
				id3.setGenre(par.intValue());
			    } catch (ID3IllegalFormatException e2) {
			    }
			    found = true;
			} catch (NumberFormatException e) {}
		    }
		}
	    }
	}

	if (found == false) {
	    // could not parse a genre number
	    try {
		id3.setGenre(12); // Genre: OTHER
	    } catch (ID3IllegalFormatException e2) {
	    }
	}

	ByteBuilder build = new ByteBuilder(ByteBuilder.UNICODE);
	build.put(genre.getTextContent());
	byte []v2cont = build.getBytes();

	//// store
	// remove frame
	try {
	    id3v2.removeFrame("TCON");
	} catch (ID3v2Exception e) {}

	// store frame
	try {
	    ID3v2Frame add = new ID3v2Frame("TCON", v2cont, false, false, false,
					    (use_compression ? ID3v2Frame.DO_COMPRESS : ID3v2Frame.NO_COMPRESSION),
					    (byte)0, (byte)0);
	    id3v2.addFrame(add);
	} catch (ID3v2DecompressionException e) {}
    }


    /**
     * Read copyright, store as text content. According to the ID3v2 informal standard,
     * this has to be preceded when displayed with "Copyright (C)" where (C) is one
     * character showing a C in a circle...
     *
     * @return Copyright
     * @exception FrameDamagedException If frame is damaged (e.g. too short)
     */
    public TagContent getCopyrightText() throws FrameDamagedException
    {
	return TextFrameEncoding.read(id3v2, "TCOP");
    }


    /**
     * Set copyright, read from text content.
     *
     * @param copyright Copyright to set
     * @exception TagFormatException If information to set is not correctly formatted
     */
    public void setCopyrightText(TagContent copyright) throws TagFormatException
    {
	(new TextFrameEncoding(id3v2, "TCOP", copyright, use_compression)).write();
    }


    /**
     * Read date (format DDMM), store as text content.
     *
     * @return date
     * @exception FrameDamagedException If frame is damaged (e.g. too short)
     */
    public TagContent getDate() throws FrameDamagedException
    {
	return TextFrameEncoding.read(id3v2, "TDAT");
    }


    /**
     * Set date (format DDMM), read from text content.
     *
     * @param date Date to set
     * @exception TagFormatException If input does not adhere to the format given above.
     */
    public void setDate(TagContent date) throws TagFormatException
    {
	// check format
	if (date.getTextContent() == null || checkExactLength(date.getTextContent(), 4) == false
	    || checkNumeric(date.getTextContent()) == false) {
	    throw new TagFormatException();
	}

	(new TextFrameEncoding(id3v2, "TDAT", date, use_compression)).write();
    }


    /**
     * Read playlist delay, store as text content.
     *
     * @return Playlist delay
     * @exception FrameDamagedException If frame is damaged (e.g. too short)
     */
    public TagContent getPlaylistDelay() throws FrameDamagedException
    {
	return TextFrameEncoding.read(id3v2, "TDLY");
    }


    /**
     * Set playlist delay, read from text content.
     *
     * @param delay Playlist delay to set
     * @exception TagFormatException If input is not a numeric string
     */
    public void setPlaylistDelay(TagContent delay) throws TagFormatException
    {
	// check format
	if (delay.getTextContent() == null || !checkNumeric(delay.getTextContent())) {
	    throw new TagFormatException();
	}

	(new TextFrameEncoding(id3v2, "TDLY", delay, use_compression)).write();
    }


    /**
     * Read encoded by, store as text content.
     *
     * @return Encoded by
     * @exception FrameDamagedException If frame is damaged (e.g. too short)
     */
    public TagContent getEncodedBy() throws FrameDamagedException
    {
	return TextFrameEncoding.read(id3v2, "TENC");
    }


    /**
     * Set encoded by, read from text content.
     *
     * @param encoder Encoded by to set
     * @exception TagFormatException If information to set is not correctly formatted
     */
    public void setEncodedBy(TagContent encoder)  throws TagFormatException
    {
	(new TextFrameEncoding(id3v2, "TENC", encoder, use_compression)).write();
    }


    /**
     * Read lyricist, store as text content.
     *
     * @return Lyricist
     * @exception FrameDamagedException If frame is damaged (e.g. too short)
     */
    public TagContent getLyricist() throws FrameDamagedException
    {
	return TextFrameEncoding.read(id3v2, "TEXT");
    }


    /**
     * Set lyricist, read from text content.
     *
     * @param lyricist Lyricist to set
     * @exception TagFormatException If information to set is not correctly formatted
     */
    public void setLyricist(TagContent lyricist) throws TagFormatException
    {
	(new TextFrameEncoding(id3v2, "TEXT", lyricist, use_compression)).write();
    }


    /**
     * Read file type, store as text content.
     * <p>
     * The following types are defined (other types may be used)
     * <ul>
     * <li><code>MPG    </code>: MPEG Audio
     * <li><code>MPG/1  </code>: MPEG 1/2 layer I</li>
     * <li><code>MPG/2  </code>: MPEG 1/2 layer II</li>
     * <li><code>MPG/3  </code>: MPEG 1/2 layer III</li>
     * <li><code>MPG/2.5</code>: MPEG 2.5</li>
     * <li><code>MPG/AAC</code>: Advanced audio compression</li>
     * <li><code>VQF    </code>: Transform-domain weighted interleace vector quantization</li>
     * <li><code>PCM    </code>: Pulse code modulated audio</li>
     * </ul>
     * <p>
     *
     * @return File type
     * @exception FrameDamagedException If frame is damaged (e.g. too short)
     */
    public TagContent getFileType() throws FrameDamagedException
    {
	return TextFrameEncoding.read(id3v2, "TFLT");
    }


    /**
     * Set file type, read from text content.
     *
     * @param type File type to set
     * @exception TagFormatException If information to set is not correctly formatted
     */
    public void setFileType(TagContent type) throws TagFormatException
    {
	(new TextFrameEncoding(id3v2, "TFLT", type, use_compression)).write();
    }


    /**
     * Read time (format HHMM), store as text content.
     *
     * @return Time
     * @exception FrameDamagedException If frame is damaged (e.g. too short)
     */
    public TagContent getTime() throws FrameDamagedException
    {
	return TextFrameEncoding.read(id3v2, "TIME");
    }


    /**
     * Set time (format HHMM), read from text content.
     *
     * @param time Time to set
     * @exception TagFormatException If input does not adhere to the format given above.
     */
    public void setTime(TagContent time) throws TagFormatException
    {
	// check format
	if (time.getTextContent() == null || !checkExactLength(time.getTextContent(), 4)
	    || !checkNumeric(time.getTextContent())) {
	    throw new TagFormatException();
	}

	(new TextFrameEncoding(id3v2, "TIME", time, use_compression)).write();
    }


    /**
     * Read content group description, store as text content.
     * <p>
     * Content group description is used if sound belongs to a larger
     * category of sounds, e.g. "Piano Concerto", "Weather - Hurricane")
     *
     * @return Content group
     * @exception FrameDamagedException If frame is damaged (e.g. too short)
     */
    public TagContent getContentGroup() throws FrameDamagedException
    {
	return TextFrameEncoding.read(id3v2, "TIT1");
    }


    /**
     * Set content group description, read from text content.
     * <p>
     * Content group description is used if sound belongs to a larger
     * category of sounds, e.g. "Piano Concerto", "Weather - Hurricane")
     *
     * @param content Content group description to set
     * @exception TagFormatException If information to set is not correctly formatted
     */
    public void setContentGroup(TagContent content) throws TagFormatException
    {
	(new TextFrameEncoding(id3v2, "TIT1", content, use_compression)).write();
    }


    /**
     * Read song title, store as text content.
     *
     * @return Song title
     * @exception FrameDamagedException If frame is damaged (e.g. too short)
     */
    public TagContent getTitle() throws FrameDamagedException
    {
	TagContent ret = TextFrameEncoding.read(id3v2, "TIT2");
	if (ret.getTextContent() == null) {
	    // try id3v1
	    try {
		ret.setContent(id3.getTitle());
	    } catch (NoID3TagException e) {
	    }
	}

	return ret;
    }


    /**
     * Set title, read from text content.
     *
     * @param title Title to set
     * @exception TagFormatException If information to set is not correctly formatted
     */
    public void setTitle(TagContent title) throws TagFormatException
    {
	if (title.getTextContent() == null) {
	    throw new TagFormatException();
	}

	// write v1
	id3.setTitle(title.getTextContent());

	(new TextFrameEncoding(id3v2, "TIT2", title, use_compression)).write();
    }


    /**
     * Read subtitle, store as text content.
     * <p>
     * Subtitle is used for information directly related to the contents title
     * (e.g. "Op. 16" or "Performed live at Wembley")
     *
     * @return Subtitle
     * @exception FrameDamagedException If frame is damaged (e.g. too short)
     */
    public TagContent getSubtitle() throws FrameDamagedException
    {
	return TextFrameEncoding.read(id3v2, "TIT3");
    }


    /**
     * Set subtitle, read from text content.
     * <p>
     * Content group description is used if sound belongs to a larger
     * category of sounds, e.g. "Piano Concerto", "Weather - Hurricane")
     *
     * @param subtitle Subtitle to set
     * @exception TagFormatException If information to set is not correctly formatted
     */
    public void setSubtitle(TagContent subtitle) throws TagFormatException
    {
	(new TextFrameEncoding(id3v2, "TIT3", subtitle, use_compression)).write();
    }


    /**
     * Read initial key
     * <p>
     * Musical key in which sound starts. String with max 3 characters, ground keys:
     * A, B, C, D, E, F, G, halfkeys b and #. Minor: m, Off key: o
     *
     * @return Initial key
     * @exception FrameDamagedException If frame is damaged (e.g. too short)
     */
    public TagContent getInitialKey() throws FrameDamagedException
    {
	return TextFrameEncoding.read(id3v2, "TKEY");
    }


    /**
     * Set initial key, read from text content.
     * <p>
     * Musical key in which sound starts. String with max 3 characters, ground keys:
     * A, B, C, D, E, F, G, halfkeys b and #. Minor: m, Off key: o
     *
     * @param key Initial key to set
     * @exception TagFormatException If key is longer than three characters
     */
    public void setInitialKey(TagContent key) throws TagFormatException
    {
	if (key.getTextContent() == null || !checkMaxLength(key.getTextContent(), 3)) {
	    throw new TagFormatException();
	}

	(new TextFrameEncoding(id3v2, "TKEY", key, use_compression)).write();
    }


    /**
     * Read language of lyrics
     * <p>
     * Language is represented with three characters according to ISO-639-2.
     *
     * @return Language
     * @exception FrameDamagedException If frame is damaged (e.g. too short)
     */
    public TagContent getLanguage() throws FrameDamagedException
    {
	return TextFrameEncoding.read(id3v2, "TLAN");
    }


    /**
     * Set language of lyrics, read from text content.
     * <p>
     * Language is represented with three characters according to ISO-639-2.
     *
     * @param lang Language to set
     * @exception TagFormatException If information to set is not correctly formatted
     */
    public void setLanguage(TagContent lang) throws TagFormatException
    {
	if (lang.getTextContent() == null || !checkExactLength(lang.getTextContent(), 3)) {
	    throw new TagFormatException();
	}

	(new TextFrameEncoding(id3v2, "TLAN", lang, use_compression)).write();
    }


    /**
     * Read length of audiofile in milliseconds, store as text content.
     * <p>
     * This returns the length stored in the ID3v2 tag, not the length calculated from
     * file length.
     *
     * @return Length
     * @exception FrameDamagedException If frame is damaged (e.g. too short)
     */
    public TagContent getLengthInTag() throws FrameDamagedException
    {
	return TextFrameEncoding.read(id3v2, "TLEN");
    }


    /**
     * Set length of audiofile in milliseconds, read from text content.
     *
     * @param length Length to set
     * @exception TagFormatException If input is not a numeric string
     */
    public void setLengthInTag(TagContent length) throws TagFormatException
    {
	// check format
	if (length.getTextContent() == null || !checkNumeric(length.getTextContent())) {
	    throw new TagFormatException();
	}

	(new TextFrameEncoding(id3v2, "TLEN", length, use_compression)).write();
    }


    /**
     * Read media type, store as text content.
     * <p>
     * See the ID3v2 informal standard for more information.
     *
     * @return Media type
     * @exception FrameDamagedException If frame is damaged (e.g. too short)
     */
    public TagContent getMediaType() throws FrameDamagedException
    {
	return TextFrameEncoding.read(id3v2, "TMED");
    }


    /**
     * Set media type, read from text content.
     * <p>
     * See the ID3v2 informal standard for more information.
     *
     * @param type Media type to set
     * @exception TagFormatException If information to set is not correctly formatted
     */
    public void setMediaType(TagContent type) throws TagFormatException
    {
	(new TextFrameEncoding(id3v2, "TMED", type, use_compression)).write();
    }


    /**
     * Read original title (for cover songs), store as text content
     *
     * @return Original title
     * @exception FrameDamagedException If frame is damaged (e.g. too short)
     */
    public TagContent getOriginalTitle() throws FrameDamagedException
    {
	return TextFrameEncoding.read(id3v2, "TOAL");
    }


    /**
     * Set original title, read from text content.
     *
     * @param title Original title to set
     * @exception TagFormatException If information to set is not correctly formatted
     */
    public void setOriginalTitle(TagContent title) throws TagFormatException
    {
	(new TextFrameEncoding(id3v2, "TOAL", title, use_compression)).write();
    }


    /**
     * Read original filename, store as text content
     * <p>
     * Original filename is used to store prefered filename on media which does have limitations
     * to the filename. It is stored including suffix.
     *
     * @return Original filename
     * @exception FrameDamagedException If frame is damaged (e.g. too short)
     */
    public TagContent getOriginalFilename() throws FrameDamagedException
    {
	return TextFrameEncoding.read(id3v2, "TOFN");
    }


    /**
     * Set original filename, read from text content.
     * <p>
     * Original filename is used to store prefered filename on media which have limitations
     * to the filename. It is stored including suffix.
     *
     * @param filename Original filename to set
     * @exception TagFormatException If information to set is not correctly formatted
     */
    public void setOriginalFilename(TagContent filename) throws TagFormatException
    {
	(new TextFrameEncoding(id3v2, "TOFN", filename, use_compression)).write();
    }


    /**
     * Read original lyricist(s) (for cover songs), store as text content
     *
     * @return Original lyricist(s)
     * @exception FrameDamagedException If frame is damaged (e.g. too short)
     */
    public TagContent getOriginalLyricist() throws FrameDamagedException
    {
	return TextFrameEncoding.read(id3v2, "TOLY");
    }


    /**
     * Set original lyricist(s), read from text content.
     *
     * @param lyricist Original lyricist(s) to set
     * @exception TagFormatException If information to set is not correctly formatted
     */
    public void setOriginalLyricist(TagContent lyricist) throws TagFormatException
    {
	(new TextFrameEncoding(id3v2, "TOLY", lyricist, use_compression)).write();
    }


    /**
     * Read original artist(s) (for cover songs), store as text content
     *
     * @return Original artist(s)
     * @exception FrameDamagedException If frame is damaged (e.g. too short)
     */
    public TagContent getOriginalArtist() throws FrameDamagedException
    {
	return TextFrameEncoding.read(id3v2, "TOPE");
    }


    /**
     * Set original artist(s), read from text content.
     *
     * @param artist Original artist(s) to set
     * @exception TagFormatException If information to set is not correctly formatted
     */
    public void setOriginalArtist(TagContent artist) throws TagFormatException
    {
	(new TextFrameEncoding(id3v2, "TOPE", artist, use_compression)).write();
    }


    /**
     * Read original release year (format YYYY) (for cover songs), store as text content
     *
     * @return Original release year
     * @exception FrameDamagedException If frame is damaged (e.g. too short)
     */
    public TagContent getOriginalYear() throws FrameDamagedException
    {
	return TextFrameEncoding.read(id3v2, "TORY");
    }


    /**
     * Set original release year (format YYYY), read from text content.
     *
     * @param year Original year to set
     * @exception TagFormatException If input is not in the format listed above
     */
    public void setOriginalYear(TagContent year) throws TagFormatException
    {
	// check format
	if (year.getTextContent() == null ||
	    !checkExactLength(year.getTextContent(), 4) || !checkNumeric(year.getTextContent()))
	    {
		throw new TagFormatException();
	    }

	(new TextFrameEncoding(id3v2, "TORY", year, use_compression)).write();
    }


    /**
     * Read file owner, store as text content
     *
     * @return File owner
     * @exception FrameDamagedException If frame is damaged (e.g. too short)
     */
    public TagContent getFileOwner() throws FrameDamagedException
    {
	return TextFrameEncoding.read(id3v2, "TOWN");
    }


    /**
     * Set file owner, read from text content.
     *
     * @param owner File owner to set
     * @exception TagFormatException If information to set is not correctly formatted
     */
    public void setFileOwner(TagContent owner) throws TagFormatException
    {
	(new TextFrameEncoding(id3v2, "TOWN", owner, use_compression)).write();
    }


    /**
     * Read artist, store as text content.
     *
     * @return Artist
     * @exception FrameDamagedException If frame is damaged (e.g. too short)
     */
    public TagContent getArtist() throws FrameDamagedException
    {
	TagContent ret = TextFrameEncoding.read(id3v2, "TPE1");
	if (ret.getTextContent() == null) {
	    try {
		ret.setContent(id3.getArtist());
	    } catch (NoID3TagException e) {
	    }
	}

	return ret;
    }

    /**
     * Set artist, read from text content.
     *
     * @param artist Artist to set
     * @exception TagFormatException If information to set is not correctly formatted
     */
    public void setArtist(TagContent artist) throws TagFormatException
    {
	// write v1
	id3.setArtist(artist.getTextContent());

	(new TextFrameEncoding(id3v2, "TPE1", artist, use_compression)).write();
    }


    /**
     * Read band (orchestra, accompaniment), store as text content
     *
     * @return Band
     * @exception FrameDamagedException If frame is damaged (e.g. too short)
     */
    public TagContent getBand() throws FrameDamagedException
    {
	return TextFrameEncoding.read(id3v2, "TPE2");
    }


    /**
     * Set band, read from text content.
     *
     * @param band Band to set
     * @exception TagFormatException If information to set is not correctly formatted
     */
    public void setBand(TagContent band) throws TagFormatException
    {
      (new TextFrameEncoding(id3v2, "TPE2", band, use_compression)).write();
    }


    /**
     * Read conductor, store as text content
     *
     * @return Conductor
     * @exception FrameDamagedException If frame is damaged (e.g. too short)
     */
    public TagContent getConductor() throws FrameDamagedException
    {
	return TextFrameEncoding.read(id3v2, "TPE3");
    }


    /**
     * Set conductor, read from text content.
     *
     * @param conductor Conductor to set
     * @exception TagFormatException If information to set is not correctly formatted
     */
    public void setConductor(TagContent conductor) throws TagFormatException
    {
	(new TextFrameEncoding(id3v2, "TPE3", conductor, use_compression)).write();
    }


    /**
     * Read remixer, store as text content
     *
     * @return Remixer
     * @exception FrameDamagedException If frame is damaged (e.g. too short)
     */
    public TagContent getRemixer() throws FrameDamagedException
    {
	return TextFrameEncoding.read(id3v2, "TPE4");
    }


    /**
     * Set remixer, read from text content.
     *
     * @param conductor Remixer to set
     * @exception TagFormatException If information to set is not correctly formatted
     */
    public void setRemixer(TagContent remixer) throws TagFormatException
    {
	(new TextFrameEncoding(id3v2, "TPE4", remixer, use_compression)).write();
    }


    /**
     * Read part of a set (e.g. 1/2 for a double CD), store as text content
     *
     * @return Part of a set
     * @exception FrameDamagedException If frame is damaged (e.g. too short)
     */
    public TagContent getPartOfSet() throws FrameDamagedException
    {
	return TextFrameEncoding.read(id3v2, "TPOS");
    }


    /**
     * Set part of a set (e.g. 1/2 for a double CD), read from text content.
     *
     * @param part Part of a set to set
     * @exception TagFormatException If information to set is not correctly formatted
     */
    public void setPartOfSet(TagContent part) throws TagFormatException
    {
	(new TextFrameEncoding(id3v2, "TPOS", part, use_compression)).write();
    }


    /**
     * Read publisher, store as text content
     *
     * @return Publisher
     * @exception FrameDamagedException If frame is damaged (e.g. too short)
     */
    public TagContent getPublisher() throws FrameDamagedException
    {
	return TextFrameEncoding.read(id3v2, "TPUB");
    }


    /**
     * Set publisher, read from text content.
     *
     * @param publisher Publisher to set
     * @exception TagFormatException If information to set is not correctly formatted
     */
    public void setPublisher(TagContent publisher) throws TagFormatException
    {
	(new TextFrameEncoding(id3v2, "TPUB", publisher, use_compression)).write();
    }


    /**
     * Read track number, store in text content.
     *
     * @return Track number
     * @exception FrameDamagedException If frame is damaged (e.g. too short)
     */
    public TagContent getTrack() throws FrameDamagedException
    {
	TagContent ret = new TagContent();

	byte []v2cont = null;
	try {
	    v2cont = ((ID3v2Frame)(id3v2.getFrame("TRCK").elementAt(0))).getContent();
	} catch (ID3v2Exception e) {
	    // no info, wait for ID3
	}

	if (v2cont == null) {
	    // try id3v1
	    String v1cont;
	    try {
		v1cont = String.valueOf(id3.getTrack());
		ret.setContent(v1cont);
	    } catch (ID3Exception e) {
		// no info
	    }
	} else {
	    // use v2
	    Parser parse = new Parser(v2cont, true);
	    try {
		ret.setContent(parse.parseText());
	    } catch (ParseException e) {
		throw new FrameDamagedException();
	    }
	}

	return ret;
    }


    /**
     * Set track number, read from text content.
     *
     * @param track Track number to set
     * @exception TagFormatException If information to set is not correctly formatted
     */
    public void setTrack(TagContent track) throws TagFormatException
    {
	if (track.getTextContent() == null) {
	    throw new TagFormatException();
	}

	// write v1

	// parse track
	boolean found = false;
	int slashpos = track.getTextContent().indexOf('/');
	String trackstring;
	if (slashpos != -1) {
	    // using notation n/x
	    trackstring = track.getTextContent().substring(0, slashpos);
	} else {
	    trackstring = track.getTextContent();
	}

	try {
	    Integer test = new Integer(trackstring);
	    try {
		id3.setGenre(test.intValue());
	    } catch (Exception e) {}
	} catch (NumberFormatException e) {}

	ByteBuilder build = new ByteBuilder(ByteBuilder.UNICODE);
	build.put(track.getTextContent());
	byte []v2cont = build.getBytes();

	//// store
	// remove frame
	try
	    {
		id3v2.removeFrame("TRCK");
	    }
	catch (ID3v2Exception e)
	    {
	    }

	// store frame
	try {
	    ID3v2Frame add = new ID3v2Frame("TRCK", v2cont, false, false, false,
					    (use_compression ? ID3v2Frame.DO_COMPRESS : ID3v2Frame.NO_COMPRESSION),
					    (byte)0, (byte)0);
	    id3v2.addFrame(add);
	} catch (ID3v2DecompressionException e) {}
    }


    /**
     * Get recording dates, store as text content
     *
     * @return Recording dates
     * @exception FrameDamagedException If frame is damaged (e.g. too short)
     */
    public TagContent getRecordingDates() throws FrameDamagedException
    {
	return TextFrameEncoding.read(id3v2, "TRDA");
    }


    /**
     * Set recording date, read from text content.
     *
     * @param date Recording date
     * @exception TagFormatException If information to set is not correctly formatted
     */
    public void setRecordingDate(TagContent date) throws TagFormatException
    {
	(new TextFrameEncoding(id3v2, "TRDA", date, use_compression)).write();
    }


    /**
     * Get Internet radio station name, store as text content
     *
     * @return Internet radio station name
     * @exception FrameDamagedException If frame is damaged (e.g. too short)
     */
    public TagContent getInternetRadioStationName() throws FrameDamagedException
    {
	return TextFrameEncoding.read(id3v2, "TRSN");
    }


    /**
     * Set Internet radio station name, read from text content.
     *
     * @param name Internet radio station name
     * @exception TagFormatException If information to set is not correctly formatted
     */
    public void setInternetRadioStationName(TagContent name) throws TagFormatException
    {
      (new TextFrameEncoding(id3v2, "TRSO", name, use_compression)).write();
    }


    /**
     * Get Internet radio station owner, store as text content
     *
     * @return Internet radio station owner
     * @exception FrameDamagedException If frame is damaged (e.g. too short)
     */
    public TagContent getInternetRadioStationOwner() throws FrameDamagedException
    {
	return TextFrameEncoding.read(id3v2, "TRSO");
    }


    /**
     * Set Internet radio station owner, read from text content.
     *
     * @param owner Station owner
     * @exception TagFormatException If information to set is not correctly formatted
     */
    public void setInternetRadioStationOwner(TagContent owner) throws TagFormatException
    {
	(new TextFrameEncoding(id3v2, "TRSO", owner, use_compression)).write();
    }


    /**
     * Get size of file in bytes, excluding id3v2 tag, store as text content
     *
     * @return Size of File
     * @exception FrameDamagedException If frame is damaged (e.g. too short)
     */
    public TagContent getFilesize() throws FrameDamagedException
    {
      return TextFrameEncoding.read(id3v2, "TSIZ");
    }


    /**
     * Set size of files in bytes, excluding id3v2 tag, read from text content.
     *
     * @param size Size of file
     * @exception TagFormatException If input is not numeric
     */
    public void setFilesize(TagContent size) throws TagFormatException
    {
	if (size.getTextContent() == null || !checkNumeric(size.getTextContent())) {
	    throw new TagFormatException();
	}

	(new TextFrameEncoding(id3v2, "TSIZ", size, use_compression)).write();
    }


    /**
     * Get International Standard Recording Code, store as text content
     *
     * @return ISRC
     * @exception FrameDamagedException If frame is damaged (e.g. too short)
     */
    public TagContent getISRC() throws FrameDamagedException
    {
	return TextFrameEncoding.read(id3v2, "TSRC");
    }


    /**
     * Set International Standard Recording Code, read from text content.
     *
     * @param isrc ISRC
     * @exception TagFormatException If input is not of 12 character's length
     */
    public void setISRC(TagContent isrc) throws TagFormatException
    {
	(new TextFrameEncoding(id3v2, "TSRC", isrc, use_compression)).write();
    }


    /**
     * Get year of recording, store as text content
     *
     * @return Recording dates
     * @exception FrameDamagedException If frame is damaged (e.g. too short)
     */
    public TagContent getYear() throws FrameDamagedException
    {
	TagContent ret = TextFrameEncoding.read(id3v2, "TYER");
	if (ret.getTextContent() == null) {
	    try {
		ret.setContent(id3.getYear());
	    } catch (NoID3TagException e) {
	    }

	}

	return ret;
    }


    /**
     * Set year of recording, read from text content.
     *
     * @param year Year of recording
     * @exception TagFormatException If input is not numeric or not 4 or 5 characters
     */
    public void setYear(TagContent year) throws TagFormatException
    {
	if (year.getTextContent() == null || !checkNumeric(year.getTextContent()) ||
	    (!checkMaxLength(year.getTextContent(), 4) &&
	     !checkMaxLength(year.getTextContent(), 5))) {
	    throw new TagFormatException();
	}

	id3.setYear(year.getTextContent());

	(new TextFrameEncoding(id3v2, "TYER", year, use_compression)).write();
    }


    ////// URL link frames


    /**
     * Read Commercial information webpage, store as text content
     *
     * @return Commercial information
     * @exception FrameDamagedException If frame is damaged (e.g. too short)
     */
    public TagContent getCommercialInformation() throws FrameDamagedException
    {
	return TextFrameNoEncoding.read(id3v2, "WCOM");
    }


    /**
     * Set Commercial information webpage, read from text content.
     *
     * @param info Commercial information to set
     * @exception TagFormatException If information to set is not correctly formatted
     */
    public void setCommercialInformation(TagContent info) throws TagFormatException
    {
	(new TextFrameNoEncoding(id3v2, "WCOM", info, use_compression)).write();
    }


    /**
     * Read Coypright / legal information webpage, store as text content
     *
     * @return Copyright webpage
     * @exception FrameDamagedException If frame is damaged (e.g. too short)
     */
    public TagContent getCopyrightWebpage() throws FrameDamagedException
    {
	return TextFrameNoEncoding.read(id3v2, "WCOP");
    }


    /**
     * Set Copyright / legal information webpage, read from text content.
     *
     * @param copy Copyright webpage to set
     * @exception TagFormatException If information to set is not correctly formatted
     */
    public void setCopyrightWebpage(TagContent copy) throws TagFormatException
    {
	(new TextFrameNoEncoding(id3v2, "WCOP", copy, use_compression)).write();
    }


    /**
     * Read official audio file webpage, store as text content
     *
     * @return Audio file webpage
     * @exception FrameDamagedException If frame is damaged (e.g. too short)
     */
    public TagContent getAudioFileWebpage() throws FrameDamagedException
    {
	return TextFrameNoEncoding.read(id3v2, "WOAF");
    }


    /**
     * Set official audio file webpage, read from text content.
     *
     * @param page Official audio file webpage to set
     * @exception TagFormatException If information to set is not correctly formatted
     */
    public void setAudioFileWebpage(TagContent page) throws TagFormatException
    {
	(new TextFrameNoEncoding(id3v2, "WOAF", page, use_compression)).write();
    }


    /**
     * Read official artist / performer webpage, store as text content
     *
     * @return Artist webpage
     * @exception FrameDamagedException If frame is damaged (e.g. too short)
     */
    public TagContent getArtistWebpage() throws FrameDamagedException
    {
	return TextFrameNoEncoding.read(id3v2, "WOAR");
    }


    /**
     * Set official artist / performer webpage, read from text content.
     *
     * @param page Artist webpage webpage to set
     * @exception TagFormatException If information to set is not correctly formatted
     */
    public void setArtistWebpage(TagContent page) throws TagFormatException
    {
	(new TextFrameNoEncoding(id3v2, "WOAR", page, use_compression)).write();
    }


    /**
     * Read official audio source webpage, store as text content
     * Used e.g. for movie soundtracks, then points to the movie
     *
     * @return Audio source webpage
     * @exception FrameDamagedException If frame is damaged (e.g. too short)
     */
    public TagContent getAudioSourceWebpage() throws FrameDamagedException
    {
	return TextFrameNoEncoding.read(id3v2, "WOAS");
    }


    /**
     * Set official audio source webpage, read from text content.
     *
     * @param page Official audio source webpage to set
     * @exception TagFormatException If information to set is not correctly formatted
     */
    public void setAudioSourceWebpage(TagContent page) throws TagFormatException
    {
	(new TextFrameNoEncoding(id3v2, "WOAS", page, use_compression)).write();
    }


    /**
     * Read official internet radio station webpage, store as text content
     *
     * @return Internet radio station webpage
     * @exception FrameDamagedException If frame is damaged (e.g. too short)
     */
    public TagContent getInternetRadioStationWebpage() throws FrameDamagedException
    {
	return TextFrameNoEncoding.read(id3v2, "WORS");
    }


    /**
     * Set official internet radio station webpage, read from text content.
     *
     * @param page Official internet radio station webpage to set
     * @exception TagFormatException If information to set is not correctly formatted
     */
    public void setInternetRadioStationWebpage(TagContent page) throws TagFormatException
    {
	(new TextFrameNoEncoding(id3v2, "WORS", page, use_compression)).write();
    }


    /**
     * Read payment webpage, store as text content
     *
     * @return Payment webpage
     * @exception FrameDamagedException If frame is damaged (e.g. too short)
     */
    public TagContent getPaymentWebpage() throws FrameDamagedException
    {
	return TextFrameNoEncoding.read(id3v2, "WPAY");
    }


    /**
     * Set payment webpage, read from text content.
     *
     * @param page Payment webpage to set
     * @exception TagFormatException If information to set is not correctly formatted
     */
    public void setPaymentWebpage(TagContent page) throws TagFormatException
    {
	(new TextFrameNoEncoding(id3v2, "WPAY", page, use_compression)).write();
    }


    /**
     * Read official publishers webpage, store as text content
     *
     * @return Publishers webpage
     * @exception FrameDamagedException If frame is damaged (e.g. too short)
     */
    public TagContent getPublishersWebpage() throws FrameDamagedException
    {
	return TextFrameNoEncoding.read(id3v2, "WPUB");
    }


    /**
     * Set official publishers webpage, read from text content.
     *
     * @param page Official publishers webpage to set
     * @exception TagFormatException If information to set is not correctly formatted
     */
    public void setPublishersWebpage(TagContent page) throws TagFormatException
    {
	(new TextFrameNoEncoding(id3v2, "WPUB", page, use_compression)).write();
    }


    ////// Binary frames


    /**
     * Read event timing codes, store as binary content
     * See the ID3v2 informal standard for details on the format of this field.
     *
     * @return Event timing codes
     * @exception FrameDamagedException If frame is damaged (e.g. too short)
     */
    public TagContent getEventTimingCodes() throws FrameDamagedException
    {
	return BinaryFrame.read(id3v2, "ETCO");
    }


    /**
     * Set event timing codes, read from binary content.
     *
     * @param codes Timing codes to set
     * @exception TagFormatException If information to set is not correctly formatted
     */
    public void setEventTimingCodes(TagContent codes) throws TagFormatException
    {
	(new BinaryFrame(id3v2, "ETCO", codes, use_compression)).write();
    }


    /**
     * Read MPEG location lookup table, store as binary content
     * See the ID3v2 informal standard for details on the format of this field.
     *
     * @return Lookup table
     * @exception FrameDamagedException If frame is damaged (e.g. too short)
     */
    public TagContent getLookupTable() throws FrameDamagedException
    {
	return BinaryFrame.read(id3v2, "MLLT");
    }


    /**
     * Set MPEG location lookup table, read from binary content.
     * See the ID3v2 informal standard for details on the format of this field.
     *
     * @param table Location lookup table to set
     * @exception TagFormatException If information to set is not correctly formatted
     */
    public void setLookupTable(TagContent table) throws TagFormatException
    {
	(new BinaryFrame(id3v2, "MLLT", table, use_compression)).write();
    }


    /**
     * Read synchronized tempo codes, store as binary content
     * See the ID3v2 informal standard for details on the format of this field.
     *
     * @return Synchronized tempo codes
     * @exception FrameDamagedException If frame is damaged (e.g. too short)
     */
    public TagContent getSynchronizedTempoCodes() throws FrameDamagedException
    {
	return BinaryFrame.read(id3v2, "SYTC");
    }


    /**
     * Set synchronized tempo codes, read from binary content.
     * See the ID3v2 informal standard for details on the format of this field.
     *
     * @param codes Synchronized tempo codes to set
     * @exception TagFormatException If information to set is not correctly formatted
     */
    public void setSynchronizedTempoCodes(TagContent codes) throws TagFormatException
    {
	(new BinaryFrame(id3v2, "SYTC", codes, use_compression)).write();
    }


    /**
     * Read synchronized lyrics, store as binary content
     * See the ID3v2 informal standard for details on the format of this field.
     *
     * @return Synchronized lyrics
     * @exception FrameDamagedException If frame is damaged (e.g. too short)
     */
    public TagContent getSynchronizedLyrics() throws FrameDamagedException
    {
	return BinaryFrame.read(id3v2, "SYLT");
    }


    /**
     * Set synchronized lyrics, read from binary content.
     * See the ID3v2 informal standard for details on the format of this field.
     *
     * @param lyrics Synchronized lyrics
     * @exception TagFormatException If information to set is not correctly formatted
     */
    public void setSynchronizedLyrics(TagContent lyrics) throws TagFormatException
    {
	(new BinaryFrame(id3v2, "SYLT", lyrics, use_compression)).write();
    }


    /**
     * Read relative volume adjustment, store as binary content
     * See the ID3v2 informal standard for details on the format of this field.
     *
     * @return Relative volume adjustment
     * @exception FrameDamagedException If frame is damaged (e.g. too short)
     */
    public TagContent getRelativeVolumenAdjustment() throws FrameDamagedException
    {
	return BinaryFrame.read(id3v2, "RVAD");
    }


    /**
     * Set relative volume adjustment, read from binary content.
     * See the ID3v2 informal standard for details on the format of this field.
     *
     * @param adjust Relative volume adjustment to set
     * @exception TagFormatException If information to set is not correctly formatted
     */
    public void setRelativeVolumeAdjustment(TagContent adjust) throws TagFormatException
    {
	(new BinaryFrame(id3v2, "RVAD", adjust, use_compression)).write();
    }


    /**
     * Read equalisation, store as binary content
     * See the ID3v2 informal standard for details on the format of this field.
     *
     * @return Equalisation
     * @exception FrameDamagedException If frame is damaged (e.g. too short)
     */
    public TagContent getEqualisation() throws FrameDamagedException
    {
	return BinaryFrame.read(id3v2, "EQUA");
    }


    /**
     * Set equalisation, read from binary content.
     * See the ID3v2 informal standard for details on the format of this field.
     *
     * @param equal Equalisation
     * @exception TagFormatException If information to set is not correctly formatted
     */
    public void setEqualisation(TagContent equal) throws TagFormatException
    {
	(new BinaryFrame(id3v2, "EQUA", equal, use_compression)).write();
    }


    /**
     * Read reverb, store as binary content
     * See the ID3v2 informal standard for details on the format of this field.
     *
     * @return Reverb
     * @exception FrameDamagedException If frame is damaged (e.g. too short)
     */
    public TagContent getReverb() throws FrameDamagedException
    {
	return BinaryFrame.read(id3v2, "RVRB");
    }


    /**
     * Set reverb, read from binary content.
     * See the ID3v2 informal standard for details on the format of this field.
     *
     * @param reverb Reverb
     * @exception TagFormatException If information to set is not correctly formatted
     */
    public void setReverb(TagContent reverb) throws TagFormatException
    {
	(new BinaryFrame(id3v2, "RVRB", reverb, use_compression)).write();
    }


    /**
     * Read play counter, store as binary content
     * See the ID3v2 informal standard for details on the format of this field.
     *
     * @return Play counter
     * @exception FrameDamagedException If frame is damaged (e.g. too short)
     */
    public TagContent getPlayCounter() throws FrameDamagedException
    {
	return BinaryFrame.read(id3v2, "PCNT");
    }


    /**
     * Set play counter, read from binary content.
     * See the ID3v2 informal standard for details on the format of this field.
     *
     * @param count Play counter
     * @exception TagFormatException If information to set is not correctly formatted
     */
    public void setPlayCounter(TagContent count) throws TagFormatException
    {
	(new BinaryFrame(id3v2, "PCNT", count, use_compression)).write();
    }


    /**
     * Read popularimeter, store as binary content
     * See the ID3v2 informal standard for details on the format of this field.
     *
     * @return Popularimeter
     * @exception FrameDamagedException If frame is damaged (e.g. too short)
     */
    public TagContent getPopularimeter() throws FrameDamagedException
    {
	return BinaryFrame.read(id3v2, "POPM");
    }


    /**
     * Set popularimeter, read from binary content.
     * See the ID3v2 informal standard for details on the format of this field.
     *
     * @param pop Popularimeter
     * @exception TagFormatException If information to set is not correctly formatted
     */
    public void setPopularimeter(TagContent pop) throws TagFormatException
    {
	(new BinaryFrame(id3v2, "POPM", pop, use_compression)).write();
    }


    /**
     * Read recommended buffer size, store as binary content
     * See the ID3v2 informal standard for details on the format of this field.
     *
     * @return Recommended buffer size
     * @exception FrameDamagedException If frame is damaged (e.g. too short)
     */
    public TagContent getRecommendedBufferSize() throws FrameDamagedException
    {
	return BinaryFrame.read(id3v2, "RBUF");
    }


    /**
     * Set recommended buffer size, read from binary content.
     * See the ID3v2 informal standard for details on the format of this field.
     *
     * @param size Recommended buffer size
     * @exception TagFormatException If information to set is not correctly formatted
     */
    public void setRecommendedBufferSize(TagContent size) throws TagFormatException
    {
	(new BinaryFrame(id3v2, "RBUF", size, use_compression)).write();
    }


    /**
     * Read position synchronization, store as binary content
     * See the ID3v2 informal standard for details on the format of this field.
     *
     * @return Position synchronization
     * @exception FrameDamagedException If frame is damaged (e.g. too short)
     */
    public TagContent getPositionSynchronization() throws FrameDamagedException
    {
	return BinaryFrame.read(id3v2, "POSS");
    }


    /**
     * Set position synchronization, read from binary content.
     * See the ID3v2 informal standard for details on the format of this field.
     *
     * @param synch Position synchronization
     * @exception TagFormatException If information to set is not correctly formatted
     */
    public void setPositionSynchronization(TagContent synch) throws TagFormatException
    {
	(new BinaryFrame(id3v2, "POSS", synch, use_compression)).write();
    }


    /**
     * Read ownership, store as binary content
     * See the ID3v2 informal standard for details on the format of this field.
     *
     * @return Ownership
     * @exception FrameDamagedException If frame is damaged (e.g. too short)
     */
    public TagContent getOwnership() throws FrameDamagedException
    {
	return BinaryFrame.read(id3v2, "OWNE");
    }


    /**
     * Set ownership, read from binary content.
     * See the ID3v2 informal standard for details on the format of this field.
     *
     * @param owner Ownership
     * @exception TagFormatException If information to set is not correctly formatted
     */
    public void setOwnership(TagContent owner) throws TagFormatException
    {
	(new BinaryFrame(id3v2, "OWNE", owner, use_compression)).write();
    }


    /**
     * Read commercial frame, store as binary content
     * See the ID3v2 informal standard for details on the format of this field.
     *
     * @return Commercial frame
     * @exception FrameDamagedException If frame is damaged (e.g. too short)
     */
    public TagContent getCommercial() throws FrameDamagedException
    {
	return BinaryFrame.read(id3v2, "COMR");
    }


    /**
     * Set commercial frame, read from binary content.
     * See the ID3v2 informal standard for details on the format of this field.
     *
     * @param commercial Commercial frame
     * @exception TagFormatException If information to set is not correctly formatted
     */
    public void setCommercial(TagContent commercial) throws TagFormatException
    {
	(new BinaryFrame(id3v2, "COMR", commercial, use_compression)).write();
    }


    /**
     * Read Music CD identifier, store as binary content
     *
     * @return Music CD identifier
     * @exception FrameDamagedException If frame is damaged (e.g. too short)
     */
    public TagContent getCDIdentifier() throws FrameDamagedException
    {
	return BinaryFrame.read(id3v2, "MCDI");
    }


    /**
     * Set music CD identifier, read from binary content.
     *
     * @param ident CD identifier
     * @exception TagFormatException If information to set is not correctly formatted
     */
    public void setCDIdentifier(TagContent ident) throws TagFormatException
    {
	(new BinaryFrame(id3v2, "MCDI", ident, use_compression)).write();
    }


    //////// Other frames, each is different to parse


    /**
     * Read unique file identifier. Owner identifier is stored as description,
     * identifier as binary content.
     *
     * @return Unique file identifier
      * @exception FrameDamagedException If frame is damaged (e.g. too short)
    */
    public TagContent getUniqueFileIdentifier() throws FrameDamagedException
    {
	byte []v2cont = Frame.read(id3v2, "UFID");
	if (v2cont == null) {
	    return new TagContent();
	} else {
	    TagContent ret = new TagContent();

	    Parser parse = new Parser(v2cont, false);
	    try {
		ret.setDescription(parse.parseText());
		ret.setContent(parse.parseBinary());

		return ret;
	    } catch (ParseException e) {
		throw new FrameDamagedException();
	    }
	}
    }


    /**
     * Set unique file identifier. Owner identifier is read from description,
     * identifier from binary content.
     *
     * @param ufi Unique file identifier to set.
     * @exception TagFormatException If File identifier is longer than 64 characters
     */
    public void setUniqueFileIdentifier(TagContent ufi) throws TagFormatException
    {
	// check correct format
	if (ufi.getDescription() == null || ufi.getBinaryContent() == null ||
	    checkMaxLength(ufi.getBinaryContent(), 64) == false) {
	    throw new TagFormatException();
	}

	ByteBuilder build =
	    new ByteBuilder(ByteBuilder.NONE,
			    ufi.getDescription().length() + 2 + ufi.getBinaryContent().length);

	build.put(ufi.getDescription());
	build.put((byte)0);
	build.put(ufi.getBinaryContent());

	(new Frame(id3v2, "UFID", build.getBytes(), true, true, use_compression)).write();
    }


    /**
     * Read user defined text, store description as description and value as text content
     *
     * @return User defined text
     * @exception FrameDamagedException If frame is damaged (e.g. too short)
     */
    public TagContent getUserDefinedText() throws FrameDamagedException
    {
	byte []v2cont = Frame.read(id3v2, "TXXX");
	if (v2cont == null) {
	    return new TagContent();
	} else {
	    TagContent ret = new TagContent();

	    Parser parse = new Parser(v2cont, true);
	    try {
		ret.setDescription(parse.parseText());
		ret.setContent(parse.parseText());
		return ret;
	    } catch (ParseException e) {
		throw new FrameDamagedException();
	    }
	}
    }


    /**
     * Set user defined text information. Description is read from description,
     * value from text content.
     *
     * @param info User defined text information
     * @exception TagFormatException If information to set is not correctly formatted
     */
    public void setUserDefinedText(TagContent info) throws TagFormatException
    {
	if (info.getDescription() == null || info.getTextContent() == null) {
	    throw new TagFormatException();
	}

	ByteBuilder build =
	    new ByteBuilder(ByteBuilder.UNICODE,
			    info.getDescription().length() * 2 + 3 + info.getTextContent().length() * 2);

	build.put(info.getDescription());
	build.put((byte)0);
	build.put((byte)0);
	build.put(info.getTextContent());

	(new Frame(id3v2, "TXXX", build.getBytes(), true, true, use_compression)).write();
    }


    /**
     * Read user defined URL, store description as description and URL as text content
     *
     * @return User defined URL link
     * @exception FrameDamagedException If frame is damaged (e.g. too short)
     */
    public TagContent getUserDefinedURL() throws FrameDamagedException
    {
	byte []v2cont = Frame.read(id3v2, "WXXX");
	if (v2cont == null) {
	    return new TagContent();
	} else {
	    TagContent ret = new TagContent();

	    Parser parse = new Parser(v2cont, true);
	    try {
		ret.setDescription(parse.parseText());
		ret.setContent(parse.parseText(Parser.ISO));
		return ret;
	    } catch (ParseException e) {
		throw new FrameDamagedException();
	    }
	}
    }


    /**
     * Set user defined URL link. Description is read from description,
     * URL from text content.
     *
     * @param link User defined URL link
     * @exception TagFormatException If information to set is not correctly formatted
     */
    public void setUserDefinedURL(TagContent link) throws TagFormatException
    {
	if (link.getDescription() == null || link.getTextContent() == null) {
	    throw new TagFormatException();
	}

	ByteBuilder build =
	    new ByteBuilder(ByteBuilder.UNICODE,
			    link.getDescription().length() * 2 + 3 + link.getTextContent().length());

	build.put(link.getDescription());
	build.put((byte)0);
	build.put((byte)0);
	try {
	    build.put(link.getTextContent().getBytes("ISO8859_1"));
	} catch (java.io.UnsupportedEncodingException e) {}

	(new Frame(id3v2, "WXXX", build.getBytes(), true, true, use_compression)).write();
    }


    /**
     * Read unsynchronized lyrics, store language as type, description as description and
     * lyrics as text content
     *
     * @return Unsynchronized lyrics
     * @exception FrameDamagedException If frame is damaged (e.g. too short)
     */
    public TagContent getUnsynchronizedLyrics() throws FrameDamagedException
    {
	byte []v2cont = Frame.read(id3v2, "USLT");
	if (v2cont == null) {
	    return new TagContent();
	} else {
	    TagContent ret = new TagContent();

	    Parser parse = new Parser(v2cont, true);
	    try {
		byte []tmp = parse.parseBinary(3);
		try {
		    ret.setType(new String(tmp, "ISO8859_1"));
		} catch (java.io.UnsupportedEncodingException e) {}
		ret.setDescription(parse.parseText());
		ret.setContent(parse.parseText());
		return ret;
	    } catch (ParseException e) {
		throw new FrameDamagedException();
	    }
	}
    }


    /**
     * Set unsynchronized lyrics. Language is read from type, Description from description,
     * lyrics from text content.
     *
     * @param lyric Unsynchronized lyrics
     * @exception TagFormatException If information to set is not correctly formatted
     */
    public void setUnsynchronizedLyrics(TagContent lyric) throws TagFormatException
    {
	if (lyric.getType() == null || lyric.getDescription() == null ||
	    lyric.getTextContent() == null || !checkExactLength(lyric.getType(), 3)) {
	    throw new TagFormatException();
	}

	ByteBuilder build =
	    new ByteBuilder(ByteBuilder.UNICODE,
			    6 + lyric.getDescription().length() * 2 + lyric.getTextContent().length() * 2);

	try {
	    build.put(lyric.getType().getBytes("ISO8859_1"));
	} catch (java.io.UnsupportedEncodingException e) {}
	build.put(lyric.getDescription());
	build.put((byte)0);
	build.put((byte)0);
	build.put(lyric.getTextContent());

	(new Frame(id3v2, "USLT", build.getBytes(), true, true, use_compression)).write();
    }


    /**
     * Read comments, store language as type, description as description and
     * comments as text content
     *
     * @return Comments
     * @exception FrameDamagedException If frame is damaged (e.g. too short)
     */
    public TagContent getComments() throws FrameDamagedException
    {
	TagContent ret = new TagContent();

	byte []v2cont = Frame.read(id3v2, "COMM");
	if (v2cont == null) {
	    // use v1
	    try {
		ret.setContent(id3.getComment());
	    } catch (Exception e) {}
	} else {
	    Parser parse = new Parser(v2cont, true);
	    try {
		byte []tmp = parse.parseBinary(3);
		try {
		    ret.setType(new String(tmp, "ISO8859_1"));
		} catch (java.io.UnsupportedEncodingException e) {}
		ret.setDescription(parse.parseText());
		ret.setContent(parse.parseText());
		return ret;
	    } catch (ParseException e) {
		throw new FrameDamagedException();
	    }
	}

	return ret;
    }


    /**
     * Set comments. Language is read from type, Description from description,
     * comments from text content.
     *
     * @param comm Comments
     * @exception TagFormatException If information to set is not correctly formatted
     */
    public void setComments(TagContent comm) throws TagFormatException
    {
	if (comm.getType() == null || comm.getDescription() == null ||
	    comm.getTextContent() == null || !checkExactLength(comm.getType(), 3)) {
	    throw new TagFormatException();
	}

	ByteBuilder build =
	    new ByteBuilder(ByteBuilder.UNICODE,
			    4 + comm.getDescription().length() * 2 + comm.getTextContent().length() * 2);

	try {
	    build.put(comm.getType().getBytes("ISO8859_1"));
	} catch (java.io.UnsupportedEncodingException e) {}
	build.put(comm.getDescription());
	build.put((byte)0);
	build.put((byte)0);
	build.put(comm.getTextContent());

	(new Frame(id3v2, "COMM", build.getBytes(), true, true, use_compression)).write();

	// write id3v1
	id3.setComment(comm.getTextContent());
    }


    /**
     * Read attached picture, store MIME type as type, picture type as binary subtype,
     * description as description and picture data as binary content.
     *
     * @return Picture
     * @exception FrameDamagedException If frame is damaged (e.g. too short)
     */
    public TagContent getPicture() throws FrameDamagedException
    {
	byte []v2cont = Frame.read(id3v2, "APIC");
	if (v2cont == null) {
	    return new TagContent();
	} else {
	    TagContent ret = new TagContent();

	    Parser parse = new Parser(v2cont, true);
	    try {
		ret.setType(parse.parseText(Parser.ISO));
		ret.setSubtype(parse.parseBinary(1));
		ret.setDescription(parse.parseText());
		ret.setContent(parse.parseBinary());
		return ret;
	    } catch (ParseException e) {
		throw new FrameDamagedException();
	    }
	}
    }


    /**
     * Set attached picture. MIME type is read from type, picture type from binary subtype,
     * description from description, picture data from binary content.
     *
     * @param pic Picture
     * @exception TagFormatException If information to set is not correctly formatted
     */
    public void setPicture(TagContent pic) throws TagFormatException
    {
	if (pic.getType() == null || pic.getBinarySubtype() == null ||
	    pic.getDescription() == null || pic.getBinaryContent() == null) {
	    throw new TagFormatException();
	}

	ByteBuilder build =
	    new ByteBuilder(ByteBuilder.UNICODE,
			    6 + pic.getType().length() + 1 +
			    pic.getDescription().length() * 2 + pic.getBinaryContent().length);

	try {
	    build.put(pic.getType().getBytes("ISO8859_1"));
	} catch (java.io.UnsupportedEncodingException e) {}
	build.put((byte)0);
	build.put(pic.getBinarySubtype()[0]);
	build.put(pic.getDescription());
	build.put((byte)0);
	build.put((byte)0);
	build.put(pic.getBinaryContent());

	(new Frame(id3v2, "APIC", build.getBytes(), true, true, use_compression)).write();
    }


    /**
     * Read general encapsulated object, store MIME type as type, filename as text subtype,
     * description as description and object as binary content.
     *
     * @return Object
     * @exception FrameDamagedException If frame is damaged (e.g. too short)
     */
    public TagContent getEncapsulatedObject() throws FrameDamagedException
    {
	byte []v2cont = Frame.read(id3v2, "GEOB");
	if (v2cont == null) {
	    return new TagContent();
	} else {
	    TagContent ret = new TagContent();

	    Parser parse = new Parser(v2cont, true);
	    try {
		ret.setType(parse.parseText(Parser.ISO));
		ret.setSubtype(parse.parseText());
		ret.setDescription(parse.parseText());
		ret.setContent(parse.parseBinary());
		return ret;
	    } catch (ParseException e) {
		throw new FrameDamagedException();
	    }
	}
    }


    /**
     * Set general encapsulated object. MIME type is read from type, filename from subtype,
     * description from description, object from binary content.
     *
     * @param obj Object
     * @exception TagFormatException If information to set is not correctly formatted
     */
    public void setEncapsulatedObject(TagContent obj) throws TagFormatException
    {
	if (obj.getType() == null || obj.getTextSubtype() == null ||
	    obj.getDescription() == null || obj.getBinaryContent() == null) {
	    throw new TagFormatException();
	}

	ByteBuilder build =
	    new ByteBuilder(ByteBuilder.UNICODE,
			    6 + obj.getType().length() + obj.getTextSubtype().length() * 2 +
			    obj.getDescription().length() * 2 + obj.getBinaryContent().length);

	try {
	    build.put(obj.getType().getBytes("ISO8859_1"));
	} catch (java.io.UnsupportedEncodingException e) {}
	build.put((byte)0);
	build.put(obj.getTextSubtype());
	build.put((byte)0);
	build.put((byte)0);
	build.put(obj.getDescription());
	build.put((byte)0);
	build.put((byte)0);
	build.put(obj.getBinaryContent());

	(new Frame(id3v2, "GEOB", build.getBytes(), true, true, use_compression)).write();
    }


    /**
     * Read terms of use, store language as type and
     * terms of use as text content
     *
     * @return Terms of use
     * @exception FrameDamagedException If frame is damaged (e.g. too short)
     */
    public TagContent getTermsOfUse() throws FrameDamagedException
    {
	byte []v2cont = Frame.read(id3v2, "USER");
	if (v2cont == null) {
	    return new TagContent();
	} else {
	    TagContent ret = new TagContent();

	    Parser parse = new Parser(v2cont, true);
	    try {
		byte []tmp = parse.parseBinary(3);
		try {
		    ret.setType(new String(tmp, "ISO8859_1"));
		} catch (java.io.UnsupportedEncodingException e) {}
		ret.setContent(parse.parseText());
		return ret;
	    } catch (ParseException e) {
		throw new FrameDamagedException();
	    }
	}
    }


    /**
     * Set terms of use. Language is read from type,
     * terms of use from text content.
     *
     * @param use Terms of use
     * @exception TagFormatException If information to set is not correctly formatted
     */
    public void setTermsOfUse(TagContent use) throws TagFormatException
    {
	if (use.getType() == null || use.getTextContent() == null ||
	    !checkExactLength(use.getType(), 3)) {
	    throw new TagFormatException();
	}

	ByteBuilder build =
	    new ByteBuilder(ByteBuilder.UNICODE,
			    3 + use.getTextContent().length() * 2);

	try {
	    build.put(use.getType().getBytes("ISO8859_1"));
	} catch (java.io.UnsupportedEncodingException e) {}
	build.put(use.getTextContent());

	(new Frame(id3v2, "USER", build.getBytes(), true, true, use_compression)).write();
    }


    /**
     * Read encryption method registration, store owner identifier as type,
     *  method symbol as binary subtype and encryption data as binary content.
     *
     * @return Encryption method registration
     * @exception FrameDamagedException If frame is damaged (e.g. too short)
     */
    public TagContent getEncryptionMethodRegistration() throws FrameDamagedException
    {
	byte []v2cont = Frame.read(id3v2, "ENCR");
	if (v2cont == null) {
	    return new TagContent();
	} else {
	    TagContent ret = new TagContent();

	    Parser parse = new Parser(v2cont, false);
	    try {
		ret.setType(parse.parseText());
		ret.setSubtype(parse.parseBinary(1));
		ret.setContent(parse.parseBinary());
		return ret;
	    } catch (ParseException e) {
		throw new FrameDamagedException();
	    }
	}
    }


    /**
     * Set encryption method registration. Owner identifier is read from type,
     * method symbol from binary subtype and encryption data from binary content.
     *
     * @param encr Encryption method
     * @exception TagFormatException If information to set is not correctly formatted
     */
    public void setEncryptionMethodRegistration(TagContent encr) throws TagFormatException
    {
	if (encr.getType() == null || encr.getBinarySubtype() == null ||
	    encr.getBinaryContent() == null || !checkExactLength(encr.getBinarySubtype(), 1)) {
	    throw new TagFormatException();
	}

	ByteBuilder build =
	    new ByteBuilder(ByteBuilder.NONE,
			    2 + encr.getType().length() + encr.getBinaryContent().length);

	build.put(encr.getType());
	build.put((byte)0);
	build.put(encr.getBinarySubtype()[0]);
	build.put(encr.getBinaryContent());

	(new Frame(id3v2, "ENCR", build.getBytes(), true, true, use_compression)).write();
    }


    /**
     * Read group identification registration, store owner identifier as type,
     * group symbol as binary subtype and group dependent data as binary content.
     *
     * @return Group identification registration
     * @exception FrameDamagedException If frame is damaged (e.g. too short)
     */
    public TagContent getGroupIdentificationRegistration() throws FrameDamagedException
    {
	byte []v2cont = Frame.read(id3v2, "GRID");
	if (v2cont == null) {
	    return new TagContent();
	} else {
	    TagContent ret = new TagContent();

	    Parser parse = new Parser(v2cont, false);
	    try {
		ret.setType(parse.parseText());
		ret.setSubtype(parse.parseBinary(1));
		ret.setContent(parse.parseBinary());
		return ret;
	    } catch (ParseException e) {
		throw new FrameDamagedException();
	    }
	}
    }


    /**
     * Set group identification registration. Owner identifier is read from type,
     * group symbol from binary subtype and group dependent data from binary content.
     *
     * @param grp Group identification
     * @exception TagFormatException If information to set is not correctly formatted
     */
    public void setGroupIdentificationRegistration(TagContent grp) throws TagFormatException
    {
	if (grp.getType() == null || grp.getBinarySubtype() == null ||
	    grp.getBinaryContent() == null || !checkExactLength(grp.getBinarySubtype(), 1)) {
	    throw new TagFormatException();
	}

	ByteBuilder build =
	    new ByteBuilder(ByteBuilder.NONE,
			    2 + grp.getType().length() + grp.getBinaryContent().length);

	build.put(grp.getType());
	build.put((byte)0);
	build.put(grp.getBinarySubtype()[0]);
	build.put(grp.getBinaryContent());

	(new Frame(id3v2, "GRID", build.getBytes(), true, true, use_compression)).write();
    }


    /**
     * Read private data, store owner identifier as type,
     * private data as binary content.
     *
     * @return Private data
     * @exception FrameDamagedException If frame is damaged (e.g. too short)
     */
    public TagContent getPrivateData() throws FrameDamagedException
    {
	byte []v2cont = Frame.read(id3v2, "PRIV");
	if (v2cont == null) {
	    return new TagContent();
	} else {
	    TagContent ret = new TagContent();

	    Parser parse = new Parser(v2cont, false);
	    try {
		ret.setType(parse.parseText());
		ret.setContent(parse.parseBinary());
		return ret;
	    } catch (ParseException e) {
		throw new FrameDamagedException();
	    }
	}
    }


    /**
     * Set private data. Owner identifier is read from type,
     * private data from binary content.
     *
     * @param data Private data
     * @exception TagFormatException If information to set is not correctly formatted
     */
    public void setPrivateData(TagContent data) throws TagFormatException
    {
	if (data.getType() == null || data.getBinaryContent() == null) {
	    throw new TagFormatException();
	}

	ByteBuilder build =
	    new ByteBuilder(ByteBuilder.NONE,
			    1 + data.getType().length() + data.getBinaryContent().length);

	build.put(data.getType());
	build.put((byte)0);
	build.put(data.getBinaryContent());

	(new Frame(id3v2, "PRIV", build.getBytes(), true, true, use_compression)).write();
    }


  /********** Private fields **********/

  /**
   * Write ID3 tag?
   */
  protected boolean write_id3 = true;

  /**
   * Write ID3v2 tag?
   */
  protected boolean write_id3v2 = true;

  /**
   * Use compression in ID3v2 tag?
   */
  protected boolean use_compression = true;

  /**
   * Use CRC in ID3v2 tag?
   */
  protected boolean use_crc = true;

  /**
   * Use padding in ID3v2 tag?
   */
  protected boolean use_padding = true;

  /**
   * Use unsynchronization in ID3v2 tag?
   */
  protected boolean use_unsynchronization = true;

  /**
   * ID3 tag
   */
  protected ID3 id3 = null;

  /**
   * ID3v3 tag
   */
  protected ID3v2 id3v2 = null;

  /**
   * MP3 properties
   */
  protected MP3Properties prop = null;


  /********** Private methods **********/


  /**
   * Checks if input string is of a given length
   */
  protected boolean checkExactLength(String in, int length)
    {
      return (in.length() == length);
    }


  /**
   * Checks if input string has a maximum given length
   */
  protected boolean checkMaxLength(String in, int length)
    {
      return (in.length() <= length);
    }


  /**
   * Checks if input byte array is of a given length
   */
  protected boolean checkExactLength(byte []in, int length)
    {
      return (in.length == length);
    }


  /**
   * Checks if input byte array has a maximum given length
   */
  protected boolean checkMaxLength(byte []in, int length)
    {
      return (in.length <= length);
    }


  /**
   * Checks if input string is numeric
   */
  protected boolean checkNumeric(String in)
    {
      try
	{
	  Integer test = new Integer(in);
	  return true;
	}
      catch (NumberFormatException e)
	{
	  return false;
	}
    }

} // public class MP3File
