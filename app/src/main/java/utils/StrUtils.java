package utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class StrUtils {

	private static final String LOGTAG = StrUtils.class.getSimpleName();
	
	public final static String strNoNull(String string) {
		if (null == string)
			return "";
		if ("null".equals(string))
			return "";
		return string;
	}
	
	public final static String stringToMD5(String string) {
		byte[] hash;
		try {
			hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
		StringBuilder hex = new StringBuilder(hash.length * 2);
		for (byte b : hash) {
			if ((b & 0xFF) < 0x10)
				hex.append("0");
			hex.append(Integer.toHexString(b & 0xFF));
		}
		return hex.toString();
	}

	public final static String MD5(String s) {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
		try {
			byte[] btInput = s.getBytes();
			// 获得MD5摘要算法�? MessageDigest 对象
			MessageDigest mdInst = MessageDigest.getInstance("MD5");
			// 使用指定的字节更新摘�?
			mdInst.update(btInput);
			// 获得密文
			byte[] md = mdInst.digest();
			// 把密文转换成十六进制的字符串形式
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			return (new String(str));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String sqlStr(String value) {
		if (null == value) {
			return "";
		} else {
			return value;
		}
	}

	public final static byte[] serialize(Object obj) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(obj);
		} catch (Exception e) {
			Log.e(LOGTAG, "serialize", e);
		}
		return bos.toByteArray();
	}

	public final static Object unserialize(byte[] obj) {
		ByteArrayInputStream bos = new ByteArrayInputStream(obj);
		try {
			ObjectInputStream ois = new ObjectInputStream(bos);
			// ois.reset();
			return ois.readObject();

		} catch (Exception e) {
			Log.e(LOGTAG, "unserialize", e);
			return null;
		}
	}

	public final static int saveObjectToFile(Object obj, String path) {
		try {
			byte[] buff = serialize(obj);
			File savefile = new File(path);
			if (savefile.exists()) {
				
			} else {
				
			}
			OutputStream out = new FileOutputStream(path);
			out.write(buff);
			out.close();
			out = null;
		} catch (Exception e) {
			Log.e(LOGTAG, "saveObjectToFile", e);
			return -1;
		}
		return 0;
	}

	public final static Object loadObjectFromFile(String path) {
		try {
			if (!(new File(path)).exists()) 
				return null;
			InputStream in = new FileInputStream(path);
			Object ret = loadObjectFromStream(in);
			in.close();
			in = null;
			return ret;
		} catch (Exception e) {
			Log.e(LOGTAG, "loadObjectToFile", e);
		}
		return null;
	}

	public final static Object loadObjectFromStream(InputStream in) {
		try {
			if (null == in)
				return null;
			if (in.available() < 1)
				return null;
			byte[] buff = new byte[in.available()];
			in.read(buff);
			return unserialize(buff);
		} catch (Exception e) {
			Log.e(LOGTAG, "loadObjectFromStream", e);
		}
		return null;
	}

	public final static Object loadObjectFromResource(String resName) {
		try {
			InputStream in = StrUtils.class.getResourceAsStream(resName);
			if (null != in) {
				Object ret = loadObjectFromStream(in);
				in.close();
				in = null;
				return ret;
			}
		} catch (Exception e) {
			Log.e(LOGTAG, "loadObjectToFile", e);
		}
		return null;
	}

}
