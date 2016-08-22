package ui.widget;

import utils.StrUtils;

import com.app.R;


import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class UtilsListItem {

	public static LinearLayout initListItemText(View rootView, int layoutId, String txtItemTitle, String txtItemText, int icoForward){
		if (0 == layoutId)
			return null;
		LinearLayout view = (LinearLayout) rootView.findViewById(layoutId);
		if (null == view) 
			return null;
		initItemTitle(view, txtItemTitle);
		return view;
	}

	public static LinearLayout initListItemText(Activity activity, int layoutId, String txtItemTitle, String txtItemText, int icoForward){
		if (0 == layoutId)
			return null;
		LinearLayout view = (LinearLayout) activity.findViewById(layoutId);
		if (null == view) 
			return null;
		initItemTitle(view, txtItemTitle);
		return view;
	}

	public static void initItemTitle(View view, String txtItemTitle){
		TextView textTitle = (TextView) view.findViewById(R.id.item_title);
		if (null != textTitle) {
			if (TextUtils.isEmpty(txtItemTitle)) {
				textTitle.setVisibility(View.GONE);					
			} else {
				textTitle.setVisibility(View.VISIBLE);
				textTitle.setText(StrUtils.strNoNull(txtItemTitle));					
			}
		}			
	}

}
