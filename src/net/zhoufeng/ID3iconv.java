/*
 * Created on 2003-8-23
 */
package net.zhoufeng;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import de.vdheide.mp3.ID3;
import de.vdheide.mp3.ID3v2;
import de.vdheide.mp3.ID3v2Frame;

/**
 * @author zf
 *
 */
public class ID3iconv {

	private static boolean isDebug;
	private static boolean removev1;
	private static boolean forcev1;
	private static boolean quiet;
	private static boolean dry;

	private static HashSet NON_UNICODE_FIELDS = new HashSet();
	private static String[] _NON_UNICODE_FIELDS = {
		"TDAT", "TIME", "TPOS", "TRCK", "TYER"
	};
	
	static {
		for (int i = 0; i < _NON_UNICODE_FIELDS.length; i++)
			NON_UNICODE_FIELDS.add(_NON_UNICODE_FIELDS[i]);
	}

	public static void main(String[] args) {
		int opt = 0;
		if (args.length == 0) {
			usage();
			return;			
		}

		String encoding = System.getProperty("file.encoding");
		for (; opt < args.length; opt++) {
			String s = args[opt];
			if (s.equals("-e")) {
				encoding = args[++opt];
			} else if (s.equals("-p")) {
				dry = true;
			} else if (s.equals("-q")) {
				quiet = true;
			} else if (s.equals("-v1")) {
				forcev1 = true;
			} else if (s.equals("-removev1")) {
				removev1 = true;
			} else if (s.equals("-d")) {
				isDebug = true;
			} else if (s.startsWith("-")) {
				error("Unknown option: "+s);
				System.exit(-1);
			} else {
				break;
			}
		}

		ID3iconv encoder = new ID3iconv();
		info("Using source encoding: "+encoding);
		for (int i = opt; i < args.length; i++) try {
			info ("Converting "+args[i]);
			encoder.convert (new File(args[i]), encoding);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param string
	 */
	private static void info(String string) {
		if (!quiet) {
			System.out.println(string);
		}
	}

	ID3 id3;
	ID3v2 id3v2;

	
	private void addFrame(String id, String content) throws Exception {
		ID3v2Frame frame;
		debug (id+": "+content);
		byte[] newbuf = content.getBytes("UnicodeLittle");
		if (newbuf.length == 0)
			return;
		byte[] newbuf2 = new byte[newbuf.length+3];
		System.arraycopy(newbuf, 0, newbuf2, 1, newbuf.length);
		newbuf2[newbuf2.length-2]=newbuf2[newbuf2.length-1]=0;
		newbuf2[0] = 1;		// encoding: utf-16

		frame = new ID3v2Frame (id, newbuf2,
			false, false, false, ID3v2Frame.NO_COMPRESSION, (byte)0, (byte)0);
		id3v2.addFrame(frame);
	}

	/**
	 * Convert the ID3 tag from any encoding to Unicode.  If the original file
	 * contains ID3 v1 tags, it is converted to ID3v2 in order to support Unicode.
	 * @param file
	 */
	private void convert(File file, String encoding) throws Exception{
		id3 = new ID3(file);		// V1 tag
		id3.encoding = encoding;
		id3v2 = new ID3v2(file);	// V2 tag

		boolean hasv1 = id3.checkForTag();
		boolean hasv2 = id3v2.hasTag();
		
		if (hasv2 && !forcev1) try {
			id3v2.getFrames();
		} catch (Exception e) {
			debug ("Cannot get v2 frames, assuming no v2 tag.");
			hasv2 = false;
		}
		
		if ((hasv1 && !hasv2) || (hasv1 && forcev1)) {
			// convert ID3v1 to ID3v2
			info ("Converting id3v1 tag to id3v2 Unicode format.");
			if (hasv2 && forcev1) {
				info("Warning: v1 tag use forced, original v2 tag overwritten.");
			}
			id3v2.clear();		// clear current v2 content, if it exists
			addFrame("TALB", id3.getAlbum());
			addFrame("TOPE", id3.getArtist());
			addFrame("TPE1", id3.getArtist());
			addFrame("COMM", id3.getComment());
			addFrame("TIT2", id3.getTitle());
			addFrame("TORY", id3.getYear());
			int i = id3.getGenre();
			if (i >= 0 && i < genreString.length)
				addFrame("TCON", genreString[i]);
			else
				addFrame("TCON", "unknown");
			addFrame("TRCK", id3.getTrack()+"");
			if (!dry && removev1) 
				id3.removeTag();
			if (!dry)
				id3v2.update();
		} else if (hasv2) {
			// convert all text frames
			info ("Reencoding id3v2 tag into Unicode");
			
			boolean updated = false;
			Vector frames = id3v2.getFrames();
			if (frames != null && frames.size() > 0) {
				for (Iterator iter = frames.iterator(); iter.hasNext();) {
					ID3v2Frame frame = (ID3v2Frame) iter.next();
					if (frame.getID().startsWith("T")) {
						// check whether the frame is "numerical string" or URL as defined
						// by IDv2.3.  They should be encoded in ISO8859-1, not Unicode
						
						if (id3v2.getVersion() == 3 && NON_UNICODE_FIELDS.contains(frame.getID())) {
							debug("No action for frame: "+frame.getID()+" because it's a v2.3 non-unicode field");
						} else {
							byte[] buf = frame.getContent();
							if (buf.length > 1 && buf[0] == 0) {
								String s = new String(buf, 1, buf.length-1, encoding);
								debug(frame.getID() + ": "+s);
								byte[] newbuf = s.getBytes("UnicodeLittle");	// utf-16LE with leading BOM character
																				// This seems to be the most compatible one
								byte[] newbuf2 = new byte[newbuf.length+5];
								System.arraycopy(newbuf, 0, newbuf2, 1, newbuf.length);
								newbuf2[newbuf2.length-2]=newbuf2[newbuf2.length-1]=0;
								newbuf2[0] = 1;		// UNICODE encoding
								frame.setContent(newbuf2);
								updated = true;
							}
						}
					} else {
						debug ("No action for frame: "+frame.getID());
					}
				}
				if (!dry && updated) {
					id3v2.touch();
					id3v2.update();
				}
				if (!dry && removev1) 
					id3.removeTag();
			}
		} else {
			error ("File "+file.getAbsolutePath()+" has no id3 tag, skipping!");
		}
	}

	/**
	 * @param string
	 */
	private static void error(String string) {
		System.out.println(string);		
	}

	/**
	 * @param string
	 */
	private void debug(String string) {
		if (isDebug) {
			System.err.println(string);
		}
	}

	/**
	 * 
	 */
	private static void usage() {
		System.out.println("ID3iconv - convert ID3 (ID3v1 or v2) tags from native encoding "
					+ "to unicode and store them using ID3v2 format.\n"
					+ "\n\tid3iconv [options] [mp3 files]\n\n" +						"Supported options:\n" +
						"-e <encoding>   Specify original tag encoding.  If not specified, system default encoding will be used.\n" +
						"-p              Dry-run. Do not actually modify files\n" +
						"-v1             Force using v1 tag as source, even if v2 tag exists.  Default is using v2 tag.\n" +
						"-removev1       Remove v1 tag after processing the file\n" +
						"-q              Quiet mode\n" +
						"-d              Output debug info to stderr\n"+
						"\nCAUTION: Files are update in-place.  So backup if you're unsure of what you are doing."
						);		
	}
	
	String genreString[] = {"Blues"
		,  "Classic Rock"
		,  "Country"
		,  "Dance"
		,  "Disco"
		,  "Funk"
		,  "Grunge"
		,  "Hip-Hop"
		,  "Jazz"
		,  "Metal"
		,  "New Age"
		,  "Oldies"
		,  "Other"
		,  "Pop"
		,  "R&B"
		,  "Rap"
		,  "Reggae"
		,  "Rock"
		,  "Techno"
		,  "Industrial"
		,  "Alternative"
		,  "Ska"
		,  "Death Metal"
		,  "Pranks"
		,  "Soundtrack"
		,  "Euro-Techno"
		,  "Ambient"
		,  "Trip-Hop"
		,  "Vocal"
		,  "Jazz Funk"
		,  "Fusion"
		,  "Trance"
		,  "Classical"
		,  "Instrumental"
		,  "Acid"
		,  "House"
		,  "Game"
		,  "Sound Clip"
		,  "Gospel"
		,  "Noise"
		,  "Alternative Rock"
		,  "Bass"
		,  "Soul"
		,  "Punk"
		,  "Space"
		,  "Meditative"
		,  "Instrumental Pop"
		,  "Instrumental Rock"
		,  "Ethnic"
		,  "Gothic"
		,  "Darkwave"
		,  "Techno-Industrial"
		,  "Electronic"
		,  "Pop-Folk"
		,  "Eurodance"
		,  "Dream"
		,  "Southern Rock"
		,  "Comedy"
		,  "Cult"
		,  "Gangsta"
		,  "Top 40"
		,  "Christian Rap"
		,  "Pop/Funk"
		,  "Jungle"
		,  "Native American"
		,  "Cabaret"
		,  "New Wave"
		,  "Psychadelic"
		,  "Rave"
		,  "Showtunes"
		,  "Trailer"
		,  "Lo-Fi"
		,  "Tribal"
		,  "Acid Punk"
		,  "Acid Jazz"
		,  "Polka"
		,  "Retro"
		,  "Musical"
		,  "Rock & Roll"
		,  "Hard Rock"
		,  "Folk"
		,  "Folk/Rock"
		,  "National Folk"
		,  "Swing"
		,  "Fast Fusion"
		,  "Bebob"
		,  "Latin"
		,  "Revival"
		,  "Celtic"
		,  "Bluegrass"
		,  "Avantgarde"
		,  "Gothic Rock"
		,  "Progressive Rock"
		,  "Psychedelic Rock"
		,  "Symphonic Rock"
		,  "Slow Rock"
		,  "Big Band"
		,  "Chorus"
		,  "Easy Listening"
		,  "Acoustic"
		,  "Humour"
		,  "Speech"
		,  "Chanson"
		,  "Opera"
		,  "Chamber Music"
		,  "Sonata"
		,  "Symphony"
		,  "Booty Bass"
		,  "Primus"
		,  "Porn Groove"
		,  "Satire"
		,  "Slow Jam"
		,  "Club"
		,  "Tango"
		,  "Samba"
		,  "Folklore"
		,  "Ballad"
		,  "Power Ballad"
		,  "Rhythmic Soul"
		,  "Freestyle"
		,  "Duet"
		,  "Punk Rock"
		,  "Drum Solo"
		,  "A Capella"
		,  "Euro-House"
		,  "Dance Hall"
		,  "Goa"
		,  "Drum & Bass"
		,  "Club-House"
		,  "Hardcore"
		,  "Terror"
		,  "Indie"
		,  "BritPop"
		,  "Negerpunk"
		,  "Polsk Punk"
		,  "Beat"
		,  "Christian Gangsta Rap"
		,  "Heavy Metal"
		,  "Black Metal"
		,  "Crossover"
		,  "Contemporary Christian"
		,  "Christian Rock"
		,  "Merengue"
		,  "Salsa"
		,  "Thrash Metal"
		,  "Anime"
		,  "JPop"
		,  "Synthpop"
	};
	
}
