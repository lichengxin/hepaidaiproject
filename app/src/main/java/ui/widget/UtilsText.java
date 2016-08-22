package ui.widget;

import android.view.View;

public class UtilsText {

	public static String getViewText(View v) {
		if (null == v)
			return null;
		if (android.widget.Button.class.getSimpleName().equals(v.getClass().getSimpleName())) {
			return ((android.widget.Button) v).getText().toString();					
		}
		if (android.widget.TextView.class.getSimpleName().equals(v.getClass().getSimpleName())) {
			return ((android.widget.TextView) v).getText().toString();					
		}
		if (android.widget.EditText.class.getSimpleName().equals(v.getClass().getSimpleName())) {
			return ((android.widget.EditText) v).getEditableText().toString();					
		}
		if (android.widget.RadioButton.class.getSimpleName().equals(v.getClass().getSimpleName())) {
			return ((android.widget.RadioButton) v).getEditableText().toString();					
		}
		return null;
	}
	
	public static void updateViewText(View view, String text) {
		if (null == view)
			return;
		if (android.widget.TextView.class.getSimpleName().equals(view.getClass().getSimpleName())) {
			((android.widget.TextView) view).setText(text);
			return;
		}
		if (android.widget.Button.class.getSimpleName().equals(view.getClass().getSimpleName())) {
			((android.widget.Button) view).setText(text);
			return;
		}
		if (android.widget.EditText.class.getSimpleName().equals(view.getClass().getSimpleName())) {
			((android.widget.EditText) view).setText(text);
			return;
		}
		if (android.widget.RadioButton.class.getSimpleName().equals(view.getClass().getSimpleName())) {
			((android.widget.RadioButton) view).setText(text);
			return;
		}
	}
	
}
