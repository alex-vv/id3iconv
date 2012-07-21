// File.java
//
// Methods for file access

package pri.nightmare.utils;

public class File
{
  private File()
    {
    }


  /**
   * Copy from <tt>source</tt> to <tt>destination</tt>
   *
   * @param source Source file
   * @param dest Destination file (no directory!)
   */
  public static void copy(String source, String dest) throws java.io.IOException
    {
      java.io.InputStream in=null;
      java.io.OutputStream out=null;
      byte[] buf = new byte[8192];
      int len;

      try
	{
	  // Create streams
	  in = new java.io.FileInputStream(source);
	  out = new java.io.FileOutputStream(dest);
	  
	  // copy bytes
	  while ((len = in.read(buf)) > 0)
	    {
	      out.write(buf, 0, len);
	    }
	}
      catch (java.io.IOException e)
	{
	  throw e;
	}
      finally
	{
	  try 
	    {
	      if (in!=null)
		{
		  in.close();
		}
	      if (out!=null)
		{
	      out.close();
		}
	    }
	  catch (java.io.IOException E)
	    {
	    }
	}
    }

  /**
   * Create a uniquely named temporary file of the form XXXnnnnn.tmp.
   *
   * @param id a string prepended on the file generated.  Should you fail
   * to delete it later, the id will help identify where it came from. 
   * null and "" also allowed.
   *
   * @param near Directory to create file in. Can also be a file, then
   * temporary file is created in same directory. If null, one of these
   * locations is used (sorted by preference; : is used as path.separator, 
   * / as file.separator ):
   * 1) /tmp
   * 2) /var/tmp
   * 3) c:/temp
   * 4) c:/windows/temp
   * 5) /
   * 6) current directory
   *
   * @return a temporary File with a unique name of the form XXXnnnnn.tmp.
   */
  public static java.io.File getTempFile (String id, java.io.File near) throws java.io.IOException
    {
      String prepend = (id != null) ? id : "";
      near = near.getAbsoluteFile();

      // Find location for temp file
      String temp_loc = null;

      if (near != null)
	{
	  if (near.isDirectory())
	    {
	      temp_loc = near.getAbsolutePath();
	    }
	  else
	    {
	      java.io.File tmp = new java.io.File(near.getAbsolutePath());
	      temp_loc = tmp.getParent();
	    }
	}

      if (near == null || (near != null && checkTempLocation(temp_loc)==false))
	{
	  String pathSep = System.getProperty("path.separator");
	  String fileSep = System.getProperty("file.separator");
	  if (checkTempLocation(fileSep + "tmp") == true)
	    {
	      temp_loc = fileSep + "tmp";
	    }
	  else if (checkTempLocation(fileSep + "var" + fileSep + "tmp") == true)
	    {
	      temp_loc = fileSep + "var" + fileSep + "tmp";
	    }
	  else if (checkTempLocation("c" + pathSep + fileSep + "temp") == true)
	    {
	      temp_loc = "c" + pathSep + fileSep + "temp";
	    }
	  else if (checkTempLocation("c" + pathSep + fileSep + "windows" + fileSep + "temp") == true)
	    {
	      temp_loc = "c" + pathSep + fileSep + "windows" + fileSep + "temp";
	    }
	  else if (checkTempLocation(fileSep) == true)
	    {
	      temp_loc = fileSep;
	    }
	  else if (checkTempLocation(".") == true)
	    {
	      temp_loc = ".";
	    }
	  else
	    {
	      // give up
	      throw new java.io.IOException("Could not find directory for temporary file");
	    }
	}

      java.util.Random wheel = new java.util.Random(); // seeded from the clock
      java.io.File tempFile = null;
      do 
	{
	  // generate random a number 10,000 .. 99,999
	  int unique = (wheel.nextInt() & Integer.MAX_VALUE) % 90000 + 10000;
	  tempFile = new java.io.File(temp_loc, prepend + Integer.toString(unique) + ".tmp");
	} 
      while ( tempFile.exists() );

      // We "finally" found a name not already used.  Nearly always the first
      // time.
      // Quickly stake our claim to it by opening/closing it to create it.
      // In theory somebody could have grabbed it in that tiny window since
      // we checked if it exists, but that is highly unlikely.
      new java.io.FileOutputStream(tempFile).close();

      // debugging peek at the name generated.
      if ( false ) {
	System.out.println(tempFile.getCanonicalPath());
      }

      return tempFile;
    }


  /**
   * Checks if directory chosen exists and is writable
   */
  private static boolean checkTempLocation(String dir)
    {
      java.io.File test = new java.io.File(dir);
      
      if (test.isDirectory()) //&& test.canWrite())
	{
	  return true;
	}
      else
	{
	  return false;
	}
    }

}
