package base.app;

import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import utils.Log;

import android.content.Context;
import android.content.pm.*;
import android.text.TextUtils;

public class AppChannel {

	private static final String LOGTAG = AppChannel.class.getSimpleName();
	
	public static String mChannel = null;
	
	public static String getChannel(Context context) {
        if (!TextUtils.isEmpty(mChannel)) {
            return mChannel;
        }
        ApplicationInfo appinfo = context.getApplicationInfo();
        String sourceDir = appinfo.sourceDir;
        ZipFile zipfile = null;
        final String start_flag1 = "META-INF/channel_";
        final String start_flag2 = "assets/channel_";
        try {
            zipfile = new ZipFile(sourceDir);
            Enumeration<?> entries = zipfile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = ((ZipEntry) entries.nextElement());
                String entryName = entry.getName();
                //result = result + "/" + entryName;
                Log.d(LOGTAG, "getChannel:" + entryName);
                
                if (entryName.contains(start_flag1)) {
                	int p = entryName.indexOf(start_flag1) + start_flag1.length();
                	mChannel = entryName.substring(p);
                	//mChannel = entryName.replaceAll(start_flag, "");
                    Log.d(LOGTAG, "channel1:" + mChannel);
                    return mChannel;
                }
                if (entryName.contains(start_flag2)) {
                	int p = entryName.indexOf(start_flag2) + start_flag2.length();
                	mChannel = entryName.substring(p);
                	//mChannel = entryName.replaceAll(start_flag, "");
                    Log.d(LOGTAG, "channel2:" + mChannel);
                    return mChannel;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (zipfile != null) {
                try {
                    zipfile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
		if (null != appinfo.metaData) {
			mChannel  = appinfo.metaData.getString("UMENG_CHANNEL");
			if (TextUtils.isEmpty(mChannel)) {
				mChannel = appinfo.metaData.getString("CHANNEL");						
		    }
		}
        return mChannel;
    }
}
