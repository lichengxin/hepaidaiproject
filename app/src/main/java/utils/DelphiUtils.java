package utils;

import base.app.BaseApp;

public class DelphiUtils {

	private static java.util.Calendar calendar = java.util.Calendar.getInstance();
	private static int gmtOffset = calendar.get(java.util.Calendar.ZONE_OFFSET) + calendar.get(java.util.Calendar.DST_OFFSET);
	public static double JavaTime2DelphiTime(java.util.Date javaTime){
	    long sysMillis = javaTime.getTime() + gmtOffset;
	    return sysMillis / 86400000 + 25569 + ((double) (sysMillis % 86400000) / 86400000);
	}
	

	public static java.util.Date DelphiTime2JavaTime(double delphiTime){
	    long timeLong = (long)(delphiTime * 86400000L) - (25569 * 86400000L);
	    return new java.util.Date(timeLong - gmtOffset);
	}
    /*//
	byte[] bytes = DelphiUtils.JavaInt2JavaDelphiBytes(65000);
	int intbytes = DelphiUtils.JavaDelphiBytes2JavaInt(bytes);
	BaseApp.showToast("test convert:" + intbytes + " byte0:" + bytes[0] + " byte1:" + bytes[1]);
	if (true)
		return;
    //*/
	public static byte[] JavaInt2JavaDelphiBytes2(int delphiWordData){
		byte[] bytes = new byte[2];
		// 这个高 字节 低字节 只有 实际测试 才知道
        //bytes[0] = (byte) (delphiWordData & 0xff);
        //bytes[1] = (byte) ((delphiWordData & 0xff00) >> 8);
        bytes[0] = (byte) (delphiWordData & 0xff);
        bytes[1] = (byte) ((delphiWordData >> 8) & 0xff);
	    return bytes;
	}

	public static int JavaDelphiBytes2_JavaInt(byte[] bytes){
		int result = ((bytes[1] << 8) & 0xff00) | (bytes[0] & 0xff); 
	    return result;
	}

	public static byte[] JavaInt2JavaDelphiBytes4(int delphiWordData){
		byte[] bytes = new byte[4];
		// 这个高 字节 低字节 只有 实际测试 才知道
		//bytes[0] = (byte) (delphiWordData & 0xff);
        //bytes[1] = (byte) ((delphiWordData & 0xff00) >> 8);
        //bytes[2] = (byte) ((delphiWordData & 0xff0000) >> 16);
        //bytes[3] = (byte) ((delphiWordData & 0xff000000) >> 24);

        bytes[0] = (byte) (delphiWordData & 0xff);
        bytes[1] = (byte) ((delphiWordData >> 8) & 0xff);
        bytes[2] = (byte) ((delphiWordData >> 16) & 0xff);
        bytes[3] = (byte) ((delphiWordData >> 24) & 0xff);
	    return bytes;
	}

	public static int JavaDelphiBytes4_JavaInt(byte[] bytes){
		int result = ((bytes[3] << 24) & 0xff000000) | 
				     ((bytes[2] << 16) & 0xff0000) | 
				     ((bytes[1] << 8) & 0xff00) | 
				     (bytes[0] & 0xff); 
	    return result;
	}

	public static byte[] JavaLong2JavaDelphiBytes8(long delphiWordData){
		byte[] bytes = new byte[8];
		// 这个高 字节 低字节 只有 实际测试 才知道
        bytes[0] = (byte) (delphiWordData & 0xff);
        bytes[1] = (byte) ((delphiWordData >> 8) & 0xff);
        bytes[2] = (byte) ((delphiWordData >> 16) & 0xff);
        bytes[3] = (byte) ((delphiWordData >> 24) & 0xff);
        bytes[4] = (byte) ((delphiWordData >> 32) & 0xff);
        bytes[5] = (byte) ((delphiWordData >> 40) & 0xff);
        bytes[6] = (byte) ((delphiWordData >> 48) & 0xff);
        bytes[7] = (byte) ((delphiWordData >> 56) & 0xff);
	    return bytes;
	}

	public static long JavaDelphiBytes8_JavaLong(byte[] bytes){
		int result = ((bytes[3] << 24) & 0xff000000) | 
				     ((bytes[2] << 16) & 0xff0000) | 
				     ((bytes[1] << 8) & 0xff00) | 
				     (bytes[0] & 0xff); 
	    return result;
	}
}
