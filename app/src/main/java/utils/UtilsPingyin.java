package utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;



public class UtilsPingyin {

    private	UtilsPingyin(){ 	
    	initialize();    	
	}

	private final static String pingyinResName = "/assets/pinyin/unicode2pinyin.txt";
	private static void initialize() {
		try {
			InputStream in = UtilsPingyin.class.getResourceAsStream(pingyinResName);
			try {
				byte[] list = new byte[in.available()];
				in.read(list);
				mPingyinTable = (new String(list, "US-ASCII")).split("\n");
			} finally {
				in.close();
			}
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}	
	
	private static UtilsPingyin mInstance;
    public static UtilsPingyin getInstance(){
    	if (mInstance == null)
    		mInstance = new UtilsPingyin();
     	return mInstance;
    }
    
	private static String [] mPingyinTable;
    public String pingyin(char c){
    	if(c>=0x4E00 && c<=0x9FA5){
    		return mPingyinTable[c-0x4E00];
    	}
    	else
    		return null;
    }
    
	/*//
	public String pingyin(String str){
		StringBuilder strbd = new StringBuilder();
		for (int i = 0; i < str.length(); i++) {
			strbd.append(pingyin(str.charAt(i)));
		}
		return strbd.toString();
	}
	//*/
}
