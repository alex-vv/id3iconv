// ID3.java
//
// $Id: ID3.java,v 1.3 2004/02/15 02:46:34 zf Exp $
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
 * Class to read and modify ID3 tags on MP3 files.
 *
 *    ID3 information are loaded
 *    - the first time any of these is requested
 *    - after doing a readTag()
 *    - after changing any of these (no real reload, it is just changed)
 *    ID3 information are written
 *    - after doing a writeTag()
 *
 *
 * If a file does not contain an ID3 tag, each read access will throw
 * a NoID3TagException. A write access will create an ID3 tag if none
 * is present.
 */

package de.vdheide.mp3;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.IOException;

public class ID3 {

	// encoding to use when converting from Unicode (String) to bytes
	public String encoding = "Cp437";

	/**
	 * Create a new ID3 tag which is based on mp3_file
	 *
	 * @param mp3_file MP3 file to read ID3 tag to / write ID3 tag to
	 */
	public ID3(File mp3_file) {
		this.mp3_file = mp3_file;
	}

	/**
	 * Read title from ID3 tag
	 *
	 * @returns Title
	 * @exception NoID3TagException If file does not contain an ID3 tag
	 */
	public String getTitle() throws NoID3TagException {
		try {
			checkIfRead(title);
		} catch (IOException e) {
			throw new NoID3TagException();
		}
		return title;
	}

	/**
	 * Read artist from ID3 tag
	 *
	 * @returns Artist
	 * @exception NoID3TagException If file does not contain an ID3 tag
	 */
	public String getArtist() throws NoID3TagException {
		try {
			checkIfRead(artist);
		} catch (IOException e) {
			throw new NoID3TagException();
		}
		return artist;
	}

	/**
	 * Read album from ID3 tag
	 *
	 * @returns album
	 * @exception NoID3TagException If file does not contain an ID3 tag
	 */
	public String getAlbum() throws NoID3TagException {
		try {
			checkIfRead(album);
		} catch (IOException e) {
			throw new NoID3TagException();
		}
		return album;
	}

	/**
	 * Read year from ID3 tag
	 *
	 * @returns Year
	 * @exception NoID3TagException If file does not contain an ID3 tag
	 */
	public String getYear() throws NoID3TagException {
		try {
			checkIfRead(year);
		} catch (IOException e) {
			throw new NoID3TagException();
		}
		return year;
	}

	/**
	 * Read genre from ID3 tag
	 *
	 * @returns Genre
	 * @exception NoID3TagException If file does not contain an ID3 tag
	 */
	public int getGenre() throws NoID3TagException {
		if (genre == null) {
			// ID3 tag not already read
			// read tag
			try {
				readTag();
			} catch (IOException e) {
				throw new NoID3TagException();
			}
		}
		return genre.byteValue();
	}

	/**
	 * Read comment from ID3 tag
	 *
	 * @returns comment
	 * @exception NoID3TagException If file does not contain an ID3 tag
	 */
	public String getComment() throws NoID3TagException {
		try {
			checkIfRead(comment);
			return comment;
		} catch (IOException e) {
			throw new NoID3TagException();
		}
	}

	/**
	 * Read track number from ID3 tag
	 *
	 * @returns Track number
	 * @exception NoID3TagException If file does not contain an ID3 tag
	 */
	public int getTrack() throws NoID3TagException {
		if (track == null) {
			try {
				readTag();
			} catch (IOException e) {
				throw new NoID3TagException();
			}
		}
		return track.byteValue();
	}

	/**
	 * Read ID3 tag and prepare for retrieval with getXXX
	 * Use this method to reread tag if changed externally
	 *
	 * @exception NoID3TagException If file does not contain an ID3 tag
	 * @exception IOException If I/O error occurs
	 */
	public void readTag() throws NoID3TagException, IOException {
		// get access to file
		RandomAccessFile in = new RandomAccessFile(mp3_file, "r");

		try {
			// file is now prepared
			// check for ID3 tag
			if (checkForTag() == false) {
				// No ID3 tag found
				throw new NoID3TagException();
			} else {
				// ID3 tag found, read it
				in.seek(in.length() - 125);
				byte[] buffer = new byte[125];
				if (in.read(buffer, 0, 125) != 125) {
					// tag too short
					// this cannot happen cause we found "TAG" at correct position
					throw new RuntimeException("ERROR: Tag too short!");
				}
//				String tag = new String(buffer, 0, 125, encoding);

				// cut tag;
				title = new String (buffer, 0, 30, encoding).trim();
				artist = new String (buffer, 30, 30, encoding).trim();
				album = new String (buffer, 60, 30, encoding).trim();
				year = new String (buffer, 90, 4, encoding).trim();
				comment = new String (buffer, 94, 29, encoding).trim();
				track = new Byte(buffer[123]);
				// ouch, what a dirty cast...
				genre = new Byte(buffer[124]);
			}

			//	        in.close();
		} finally {
			in.close();
		}
	}

	/**
	 * Set title
	 *
	 * @param title Title
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Set artist
	 *
	 * @param artist Artist
	 */
	public void setArtist(String artist) {
		this.artist = artist;
	}

	/**
	 * Set album
	 *
	 * @param album Album
	 */
	public void setAlbum(String album) {
		this.album = album;
	}

	/**
	 * Set year
	 *
	 * @param year Year
	 */
	public void setYear(String year) {
		this.year = year;
	}

	/**
	 * Set comment
	 *
	 * @param comment Comment
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}

	/**
	 * Set track number
	 *
	 * @param track Track number
	 * @exception ID3IllegalFormatException if track is negative or
	 *         larger than 255
	 */
	public void setTrack(int track) throws ID3IllegalFormatException {
		if (track < 0 || track > 255) {
			throw new ID3IllegalFormatException();
		} else {
			this.track = new Byte((byte) track);
		}
	}

	/**
	 * Set genre
	 *
	 * @param genre Genre
	 * @exception ID3IllegalFormatException if genre is negative or
	 *         larger than 255
	 */
	public void setGenre(int genre) throws ID3IllegalFormatException {
		this.genre = new Byte((byte) genre);
	}

	/**
	 * Write information provided with setXXX to ID3 tag
	 */
	public void writeTag() throws IOException {
		// get access to file
		RandomAccessFile in = new RandomAccessFile(mp3_file, "rw");

		try {
			// file is now prepared
			// check for ID3 tag
			if (checkForTag() == false) {
				// No ID3 tag found, create new
				// seek to end of file
				in.seek(in.length());
			} else {
				// jump to "TAG"
				in.seek(in.length() - 128);
			}

			// write new tag

			in.write(new String("TAG").getBytes(encoding));
			in.write(fillWithNills(title, 30).getBytes(encoding));
			in.write(fillWithNills(artist, 30).getBytes(encoding));
			in.write(fillWithNills(album, 30).getBytes(encoding));
			in.write(fillWithNills(year, 4).getBytes(encoding));
			in.write(fillWithNills(comment, 29).getBytes(encoding));
			if (track == null) {
				in.writeByte(0);
			} else {
				in.writeByte(track.byteValue());
			}
			if (genre == null) {
				in.writeByte(0);
			} else {
				in.writeByte(genre.byteValue());
			}
		} finally {
			in.close();
		}
	}

	public void removeTag() throws IOException {
		// get access to file
		if (checkForTag()) {
			// tag exists, we need to truncate the file
			File temp = new File(mp3_file.getParentFile(), ".id3.tmp");
			OutputStream out = null;
			InputStream in = null;
			long size = mp3_file.length();
			try {
				in = new FileInputStream(mp3_file);
				out = new FileOutputStream(temp);
				byte buf[] = new byte[8192];
				int c;
				// size is the bytes remained to be read
				while ((c = in.read(buf)) > 0 && size > 128) {	// truncate the last 128 bytes 
					if (c+128 > size) {
						c = (int)size - 128;
					}
					out.write(buf, 0, c);
					size -= c;
				}
				in.close();
				if (!mp3_file.delete()) {
					System.err.println("Cannot delete mp3 file: "+mp3_file);
				}
				out.close();
				if (!temp.renameTo(mp3_file)) {
					System.err.println("Cannot rename "+temp+" to "+mp3_file);
					temp = null;		// prevent it from being deleted
				}
			} finally {
				try {
					if (in != null) in.close();
				} catch (Exception e) {
				}
				try {
					if (out != null) out.close();
				} catch (Exception e) {
				}
				if (temp != null)
					temp.delete();
			}
		}
	}

	private File mp3_file = null; // file to access
	private String title = null; // id3 title
	private String artist = null; // id3 artist
	private String album = null; // id3 album
	private String year = null; // id3 year
	private Byte genre = null; // id3 genre, -1==not set
	private String comment = null; // id3 comment
	private Byte track = null; // id3 track number

	/**
	 * Check if reading of ID3 tag if necessary. If so, reads tag.
	 *
	 * @param what Which information is requested?
	 * @exception NoID3TagException If file does not contain an ID3 tag
	 * @exception IOException If an I/O errors occurs
	 */
	private void checkIfRead(String what)
		throws NoID3TagException, IOException {
		if (what == null) {
			readTag();
		}
	}

	/**
	 * Check if ID3 tag is present
	 *
	 * @returns true if tag present
	 */
	public boolean checkForTag() throws IOException {
		// Create random access file
		RandomAccessFile raf = new RandomAccessFile(mp3_file, "r");

		try {
			if (raf.length() < 129) {
				// file to short for an ID3 tag
				return false;
			} else {
				// go to position where "TAG" must be
				long seekPos = raf.length() - 128;
				raf.seek(seekPos);

				byte buffer[] = new byte[3];

				if (raf.read(buffer, 0, 3) != 3) {
					// something terrible happened
					throw new IOException("Read beyond end of file");
				}

				String testTag = new String(buffer, 0, 3, encoding);
				if (!testTag.equals("TAG")) {
					return false;
				} else {
					return true;
				}
			}
		} finally {
			raf.close();
		}
	}

	/**
	 * Fill <tt>str</tt> with \0 until <tt>str</tt> has length <tt>len</tt>
	 *
	 * @param str String to work with
	 * @param len Length of <tt>str</tt> after filling
	 * @returns Filled string
	 */
	private String fillWithNills(String str, int len) {
		if (str == null) {
			// tag info not set!
			str = new String("");
		}
		StringBuffer tmp = new StringBuffer(str);
		for (int i = str.length() + 1; i <= len; i++) {
			tmp.append('\0');
		}
		return tmp.toString();
	}

}
