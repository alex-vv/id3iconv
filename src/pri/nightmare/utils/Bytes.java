// Bytes.java
//
// Takes an array of bytes (e.g. read from an InputStream) and 
// converts it into a number, assuming MSB order.

package pri.nightmare.utils;

public class Bytes
{
  /**
   * Construct a new class from array of bytes and convert to number.
   * Result can be read by <code>getValue()</code>.
   *
   * @param in Array to convert.
   */
  public Bytes(byte []in)
    {
      this(in, 0, in.length);
    }


  /**
   * Construct a new class from array of bytes and convert to number.
   * Result can be read by <code>getValue()</code>.
   *
   * @param in Array to convert.
   * @param start First index in <code>in</code> to read
   * @param length Number of bytes to read
   */
  public Bytes(byte []in, int start, int length)
    {
      value = byteArrayToLong(in, start, length);
    }

  
  /**
   * Construct a new class from long value und convert to array of bytes.
   * Result can be read with <code>getBytes()</code>.
   *
   * @param in Value to convert
   */
  public Bytes(long in)
    {
      bytes = longToByteArray(in, -1);
    }


  /**
   * Construct a new class from long value und convert to array of bytes.
   * Result can be read with <code>getBytes()</code>.
   *
   * @param in Value to convert
   * @param len Length of resulting byte array
   */
  public Bytes(long in, int len)
    {
      bytes = longToByteArray(in, len);
    }


  /**
   * @returns long representation of input
   */
  public long getValue()
    {
      return value;
    }


  /**
   * @returns bytes representation of input
   */
  public byte []getBytes()
    {
      return bytes;
    }


  /**
   * Convert <code>length</code> bytes from an array of bytes starting 
   * at position <code>start</code> to long
   *
   * @param in Array of bytes to convert
   * @param start First index in <code>in</code> to read
   * @param length Number of bytes to read
   */
  public static long byteArrayToLong(byte []in, int start, int length)
    {
      long value = 0;

      for (int i=start; i < (start + length); i++)
	{
	  // move each byte (length-pos-1)*8 bits to the left and add them
	  value += (long)((in[i] & 0xff) << ((length - i + start - 1) * 8));
	}

      return value;
    }


  /**
   * Convert long value to array of bytes. 
   *
   * @param in Long value to convert
   * @param len Length of resulting byte array. <code>-1</code> for minimum
   *        length needed.
   * @returns Newly created array of bytes with enough fields to hold input
   *          First entry contains the MSB.
   */
  public static byte[] longToByteArray(long in, int len)
    {
      if (len == -1)
	{
	  // get length of result array (log2 n bits => log2 n / 8 Bytes)
	  len = (int)(Math.ceil(Math.ceil(Math.log(in) / Math.log(2)) / 8));
	}

      byte[] res = new byte[len];

      long act = in;
      for (int i=0; i<len; i++)
	{
	  // move now handled byte to the right
	  res[i] = (byte)(act >> ((len - i - 1) * 8)) ;

	  // and remove all bytes to the left
	  res[i] = (byte)(res[i] & 0xff);
	}

      return res;
    }


  private long value;
  private byte []bytes;

}
