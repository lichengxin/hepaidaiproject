package utils;

public class BytesUtils {

	  public static short reverseBytesShort(short s) {
	    int i = s & 0xffff;
	    int reversed = (i & 0xff00) >>> 8
	        |          (i & 0x00ff)  << 8;
	    return (short) reversed;
	  }

	  public static int reverseBytesInt(int i) {
	    return (i & 0xff000000) >>> 24
	        |  (i & 0x00ff0000) >>>  8
	        |  (i & 0x0000ff00)  <<  8
	        |  (i & 0x000000ff)  << 24;
	  }

	  public static long reverseBytesLong(long v) {
	    return (v & 0xff00000000000000L) >>> 56
	        |  (v & 0x00ff000000000000L) >>> 40
	        |  (v & 0x0000ff0000000000L) >>> 24
	        |  (v & 0x000000ff00000000L) >>>  8
	        |  (v & 0x00000000ff000000L)  <<  8
	        |  (v & 0x0000000000ff0000L)  << 24
	        |  (v & 0x000000000000ff00L)  << 40
	        |  (v & 0x00000000000000ffL)  << 56;
	  }

}
