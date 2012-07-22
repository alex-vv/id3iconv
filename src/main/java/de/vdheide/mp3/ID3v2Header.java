// ID3v2Header.java
//
// $Id: ID3v2Header.java,v 1.3 2004/02/15 02:46:34 zf Exp $
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
 * This class contains an ID3v2 header
 */

package de.vdheide.mp3;

import java.io.InputStream;
import java.io.IOException;

public class ID3v2Header {

	/********** Constructors **********/

	/**
	 * Create a new (empty) header
	 */
	public ID3v2Header() {
		this(ID3v2.VERSION, ID3v2.REVISION, false, false, false, 0);
	}

	/**
	 * Build a ID3v2 header
	 *
	 * @param version ID3v2 version
	 * @param revision ID3v2 revision
	 * @param unsynch Use unsynchronization scheme?
	 * @param extended_header Use extended header?
	 * @param experimental Is experimental?
	 * @param length ID3v2 tag length
	 */
	public ID3v2Header(
		byte version,
		byte revision,
		boolean unsynch,
		boolean extended_header,
		boolean experimental,
		int length) {
		this.version = version;
		this.revision = revision;
		this.unsynch = unsynch;
		this.extended_header = extended_header;
		this.experimental = experimental;
		this.size = length;
	}

	/** 
	 * Creates an ID3v2 header from an input stream.
	 *
	 * @param in Stream to read from
	 * @exception ID3v2IllegalVersionException If tag has a revision higher than
	 *            <code>ID3v2.VERSION</code>.<code>ID3v2.REVISION</code>
	 * @exception NoID3v2HeaderException If file does not contain an ID3v2 header
	 * @exception IOException If an I/O error occurs
	 */
	public ID3v2Header(InputStream in)
		throws ID3v2IllegalVersionException, NoID3v2HeaderException, IOException {
		readFromFile(in);
	}

	/********** Public methods **********/

	/**
	 * Reads header from stream <code>in</code>
	 * Header must start at file position.
	 *
	 * @param in Stream to read from
	 * @exception ID3v2IllegalVersionException If tag has a revision higher than
	 *            <code>ID3v2.VERSION</code>.<code>ID3v2.REVISION</code>
	 * @exception NoID3v2HeaderException If file does not contain an ID3v2 header
	 * @exception IOException If an I/O error occurs
	 */
	public void readFromFile(InputStream in)
		throws ID3v2IllegalVersionException, NoID3v2HeaderException, IOException {
		byte[] head = new byte[10];
		in.read(head);

		// check if header
		if (!isHeader(head)) {
			throw new NoID3v2HeaderException();
		}

		// so we have a valid header
		// check version
		version = head[3];
		revision = head[4];
		if ((version & 0xff) > (ID3v2.VERSION & 0xff)
			|| (version == ID3v2.VERSION
				&& (revision & 0xff) > (ID3v2.REVISION & 0xff))) {
			throw new ID3v2IllegalVersionException();
		}

		// read & parse flags
		if (((head[5] & 0xff) & FLAG_UNSYNCHRONIZATION) > 0) {
			unsynch = true;
		} else {
			unsynch = false;
		}
		if (((head[5] & 0xff) & FLAG_EXTENDED_HEADER) > 0) {
			extended_header = true;
		} else {
			extended_header = false;
		}

		if (((head[5] & 0xff) & FLAG_EXPERIMENTAL) > 0) {
			experimental = true;
		} else {
			experimental = false;
		}

		// Last, read size. Size is stored in 4 bits, which all have their highest
		// bit set to 0 (unsynchronization)
		size =
			(head[9] & 0xff)
				+ ((head[8] & 0xff) << 7)
				+ ((head[7] & 0xff) << 14)
				+ ((head[6] & 0xff) << 21);

	}

	/**
	 * Checks if bytes contain a correct header
	 *
	 * @param head Array of bytes to be checked
	 * @return true if header is correct
	 */
	public static boolean isHeader(byte[] head) {
		// head must be 10 bytes long
		if (head.length != 10)
			return false;

		// must start with ID3
		if (head[0] != 'I' || head[1] != 'D' || head[2] != '3')
			return false;

		// next two bytes must be smaller than 255
		if (head[3] == (byte) 255 || head[4] == (byte) 255)
			return false;

		// for safety's sake (who knows what future versions will bring),
		// the flags are not checked

		// last 4 bytes must be smaller than 128 (first bit set to 0)
		if ((head[6] & 0xff) >= 128
			|| (head[7] & 0xff) >= 128
			|| (head[8] & 0xff) >= 128
			|| (head[9] & 0xff) >= 128)
			return false;

		return true;
	}

	/**
	 * Is unsynchronization bit set?
	 */
	public boolean getUnsynchronization() {
		return unsynch;
	}

	/** 
	 * Set / unset unsynchronization bit
	 *
	 * @param act True: Set unsynchronization bit
	 */
	public void setUnsynchronization(boolean act) {
		unsynch = act;
	}

	/**
	 * Is extended header present?
	 */
	public boolean hasExtendedHeader() {
		return extended_header;
	}

	/**
	 * Set / unset extended header present
	 *
	 * @param act True: Set extended header present bit
	 */
	public void setExtendedHeader(boolean act) {
		extended_header = act;
	}

	/**
	 * Is experimental?
	 */
	public boolean getExperimental() {
		return experimental;
	}

	/**
	 * Set / unset experimental
	 *
	e   * @param act True: Set experimental bit
	 */
	public void setExperimental(boolean act) {
		experimental = act;
	}

	/**
	 * Get length of tag
	 *
	 * @return Length of tag without header (complete length - 10)
	 */
	public int getTagSize() {
		return size;
	}

	/**
	 * Set length if tag
	 */
	public void setTagSize(int size) {
		this.size = size;
	}

	/**
	 * Convert header to array of bytes
	 *
	 * @return Header as bytes, ready to write
	 */
	public byte[] getBytes() {
		byte[] work = new byte[10];

		work[0] = 'I';
		work[1] = 'D';
		work[2] = '3';

		work[3] = version;
		work[4] = revision;

		byte flag = 0;
		if (unsynch == true) {
			flag += FLAG_UNSYNCHRONIZATION;
		}
		if (extended_header == true) {
			flag += FLAG_EXTENDED_HEADER;
		}
		if (experimental == true) {
			flag += FLAG_EXPERIMENTAL;
		}
		work[5] = (byte) flag;

		/*      byte []size_byte = (new pri.nightmare.utils.Bytes(size, 4)).getBytes();
		    System.arraycopy(size_byte, 0, work, 6, 4);*/
		// create length bytes manually ("unsynchronized")

		for (int i = 0; i < 4; i++) {
			work[i + 6] = (byte) ((size >> ((3 - i) * 7)) & 127);
		}

		return work;
	}

	public int getVersion() {
		return version;
	}

	public int getRevision() {
		return revision;
	}

	/********** Private variables **********/

	byte version = 0;
	byte revision = 0;

	boolean unsynch = false;
	boolean extended_header = false;
	boolean experimental = false;

	int size = 0;

	private final static byte FLAG_UNSYNCHRONIZATION = (byte) (1 << 7);
	private final static byte FLAG_EXTENDED_HEADER = (byte) (1 << 6);
	private final static byte FLAG_EXPERIMENTAL = (byte) (1 << 5);

}
