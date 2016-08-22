package ui.widget;

import com.app.R;

import base.app.BaseApp;

import android.app.Activity;
import android.content.Context;
import android.os.Looper;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 对app的所有toast进行管理
 * 
 * @author andjdk
 */
public class CustomToastView
{

	public static void showShortToast(String msg)
	{
		showCustomTranslucentToast(BaseApp.instance, msg, Toast.LENGTH_SHORT);
	}

	public static void showShortToast(int msgId)
	{
		showCustomTranslucentToast(BaseApp.instance, msgId, Toast.LENGTH_SHORT);
	}

	public static void showLongToast(String msg)
	{
		showCustomTranslucentToast(BaseApp.instance, msg, Toast.LENGTH_LONG);
	}

	public static void showLongToast(int msgId)
	{
		showCustomTranslucentToast(BaseApp.instance, msgId, Toast.LENGTH_LONG);
	}

	/**
	 * 创建运行在UI线程中的Toast
	 * 
	 * @param activity
	 * @param msg
	 */
	public static void showToastInUiThread(final Activity activity, final String msg)
	{
		if (activity != null)
		{
			activity.runOnUiThread(new Runnable()
			{
				public void run()
				{
					showCustomTranslucentToast(activity, msg);
				}
			});
		}
	}

	public static void showToastInUiThread(final Activity activity, final int stringId)
	{
		if (activity != null)
		{
			activity.runOnUiThread(new Runnable()
			{
				public void run()
				{
					showCustomTranslucentToast(activity, stringId);
				}
			});
		}
	}

	private static void showCustomTranslucentToast(Context context, int msgId)
	{
		final String msg = context.getResources().getString(msgId);
		showCustomTranslucentToast(context, msg);
	}

	private static void showCustomTranslucentToast(Context context, String msg)
	{
		showCustomTranslucentToast(context, msg, Toast.LENGTH_SHORT);
	}

	private static void showCustomTranslucentToast(Context context, int msgId, int duration)
	{
		final String msg = context.getResources().getString(msgId);
		showCustomTranslucentToast(context, msg, duration);
	}

	private static void showCustomTranslucentToast(final Context context, final String msg, final int duration)
	{
		if (context == null)
			return;
		if (Looper.myLooper() == Looper.getMainLooper())
		{
			showToast(context, msg, duration);
		} else
		{
			new Handler(Looper.getMainLooper()).post(new Runnable()
			{

				@Override
				public void run()
				{
					showToast(context, msg, duration);
				}
			});
		}

	}

	private static void showToast(Context context, String msg, int duration)
	{
		if (null != context)
		{
			LayoutInflater inflater = LayoutInflater.from(context);
			View layout = inflater.inflate(R.layout.pkg_app_layout_toast_common, null);
			TextView content = (TextView) layout.findViewById(R.id.toast_content);
			content.setText(msg);

			Toast toast = new Toast(context);
			// 得到屏幕的宽度和高度
			DisplayMetrics dm =BaseApp.instance.getResources().getDisplayMetrics();
			toast.setGravity(Gravity.CENTER, 0, dm.heightPixels / 4);
			toast.setDuration(duration);
			toast.setView(layout);
			toast.show();
		}
		
	}
		
	
}
