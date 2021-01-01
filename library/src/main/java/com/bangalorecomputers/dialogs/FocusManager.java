package com.bangalorecomputers.dialogs;

import android.app.Activity;
import android.content.Context;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class FocusManager {
    public static void showFocus(Context context, View view) {
        try {
            view.requestFocus();
            InputMethodManager ipmm= (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            ipmm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
        } catch(Exception e) {
            Log.e("showFocus", e.toString());
        }
    }

    public static void hideFocus(Context context, View view) {
        try {
            IBinder token= ((Activity) context).getCurrentFocus()==null?null:((Activity) context).getCurrentFocus().getWindowToken();
            hideFocus(context, token);
        } catch(Exception e) {
            Log.e("hideFocus", e.toString());
        }
        try {
            hideFocus(context, view==null?null:view.getWindowToken());
        } catch(Exception e) {
            Log.e("hideFocus", e.toString());
        }
    }

    public static void hideFocus(Context context, IBinder token) {
        try {
            if(token==null) {
                return;
            }
            InputMethodManager ipmm= (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            ipmm.hideSoftInputFromWindow(token, 0);
        } catch(Exception e) {
            Log.e("hideFocus", e.toString());
        }
    }
}
