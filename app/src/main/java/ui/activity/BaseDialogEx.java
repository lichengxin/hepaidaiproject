package ui.activity;

import com.app.R;

import android.content.*;
import android.view.*;
import android.widget.*;

public class BaseDialogEx extends android.app.Dialog {  
  
    public BaseDialogEx(Context context) {  
        super(context);  
    }  
  
    public BaseDialogEx(Context context, int theme) {  
        super(context, theme);  
    }  

    
    public static class Builder {  
        private Context context;  
        private String title;  
        private String message;  
        private String positiveButtonText;  
        private String negativeButtonText;  
        private View contentView;  
        private DialogInterface.OnClickListener positiveButtonClickListener;  
        private DialogInterface.OnClickListener negativeButtonClickListener;  
  
        public Builder(Context context) {  
            this.context = context;  
        }  
  
        public Builder setMessage(String message) {  
            this.message = message;  
            return this;  
        }  
  
        /** 
         * Set the Dialog message from resource 
         *  
         * @param title 
         * @return 
         */  
        public Builder setMessage(int message) {  
            this.message = (String) context.getText(message);  
            return this;  
        }  
  
        /** 
         * Set the Dialog title from resource 
         *  
         * @param title 
         * @return 
         */  
        public Builder setTitle(int title) {  
            this.title = (String) context.getText(title);  
            return this;  
        }  
  
        /** 
         * Set the Dialog title from String 
         *  
         * @param title 
         * @return 
         */  
  
        public Builder setTitle(String title) {  
            this.title = title;  
            return this;  
        }  
  
        public Builder setContentView(View v) {  
            this.contentView = v;  
            return this;  
        }  
  
        /** 
         * Set the positive button resource and it's listener 
         *  
         * @param positiveButtonText 
         * @return 
         */  
        public Builder setPositiveButton(int positiveButtonText,  
                DialogInterface.OnClickListener listener) {  
            this.positiveButtonText = (String) context  
                    .getText(positiveButtonText);  
            this.positiveButtonClickListener = listener;  
            return this;  
        }  
  
        public Builder setPositiveButton(String positiveButtonText,  
                DialogInterface.OnClickListener listener) {  
            this.positiveButtonText = positiveButtonText;  
            this.positiveButtonClickListener = listener;  
            return this;  
        }  
  
        public Builder setNegativeButton(int negativeButtonText,  
                DialogInterface.OnClickListener listener) {  
            this.negativeButtonText = (String) context  
                    .getText(negativeButtonText);  
            this.negativeButtonClickListener = listener;  
            return this;  
        }  
  
        public Builder setNegativeButton(String negativeButtonText,  
                DialogInterface.OnClickListener listener) {  
            this.negativeButtonText = negativeButtonText;  
            this.negativeButtonClickListener = listener;  
            return this;  
        }  
  
        public BaseDialogEx create() {  
            LayoutInflater inflater = (LayoutInflater) context  
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
            // instantiate the dialog with the custom Theme  
            final BaseDialogEx dialog = new BaseDialogEx(context, R.style.Dialog);  
            View layout = inflater.inflate(R.layout.pkg_app_dialog, null);  
            dialog.addContentView(layout, new 
            		android.view.ViewGroup.LayoutParams(  
            				android.view.ViewGroup.LayoutParams.MATCH_PARENT, 
            				android.view.ViewGroup.LayoutParams.WRAP_CONTENT));  
            // set the dialog title  
            ((TextView) layout.findViewById(R.id.title)).setText(title);  
            // set the confirm button  
            if (positiveButtonText != null) {  
                ((Button) layout.findViewById(R.id.buttonPositive))  
                        .setText(positiveButtonText);  
                if (positiveButtonClickListener != null) {  
                    ((Button) layout.findViewById(R.id.buttonPositive))  
                            .setOnClickListener(new View.OnClickListener() {  
                                public void onClick(View v) {  
                                    positiveButtonClickListener.onClick(dialog,  
                                            DialogInterface.BUTTON_POSITIVE);  
                                }  
                            });  
                }  
            } else {  
                // if no confirm button just set the visibility to GONE  
                layout.findViewById(R.id.buttonPositive).setVisibility(  
                        View.GONE);  
            }  
            // set the cancel button  
            if (negativeButtonText != null) {  
                ((Button) layout.findViewById(R.id.buttonNegative))  
                        .setText(negativeButtonText);  
                if (negativeButtonClickListener != null) {  
                    ((Button) layout.findViewById(R.id.buttonNegative))  
                            .setOnClickListener(new View.OnClickListener() {  
                                public void onClick(View v) {  
                                    negativeButtonClickListener.onClick(dialog,  
                                            DialogInterface.BUTTON_NEGATIVE);  
                                }  
                            });  
                }  
            } else {  
                // if no confirm button just set the visibility to GONE  
                layout.findViewById(R.id.buttonNegative).setVisibility(  
                        View.GONE);  
            }  
            // set the content message  
            if (message != null) {  
                ((TextView) layout.findViewById(R.id.textview)).setText(message);  
            } else if (contentView != null) {  
                // if no message set  
                // add the contentView to the dialog body  
                ((LinearLayout) layout.findViewById(R.id.content)).removeAllViews();  
                ((LinearLayout) layout.findViewById(R.id.content)).addView(contentView, new 
                        		android.view.ViewGroup.LayoutParams(
                        				android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                        				android.view.ViewGroup.LayoutParams.MATCH_PARENT));  
            }  
            dialog.setContentView(layout);  
            return dialog;  
        }  
    }  
}
