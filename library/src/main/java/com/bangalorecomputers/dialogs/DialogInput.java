package com.bangalorecomputers.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class DialogInput {
    public static final int TYPE_NUMERIC= 1;
    public static final int TYPE_STRING= 2;
    public static final int TYPE_MULTI_LINE= 3;

    private final Context mContext;
    private AlertDialog.Builder mBuilder;
    private View mRootView;
    private LinearLayout inputContainer;

    private ArrayList<InputItems> mInputItems;

    private class InputItems {
        int inputType;
        View subView= null;
        TextInputLayout inputLayout;
        EditText input;
        InputItems(int inputType) {
            this.inputType= inputType;
            switch (inputType) {
                case TYPE_NUMERIC:
                    subView= LayoutInflater.from(mContext).inflate(R.layout.dialog_input_int, null);
                break;
                case TYPE_STRING:
                    subView= LayoutInflater.from(mContext).inflate(R.layout.dialog_input_text, null);
                break;
                case TYPE_MULTI_LINE:
                    subView= LayoutInflater.from(mContext).inflate(R.layout.dialog_input_text_multiline, null);
                break;
            }
            if(subView!=null) {
                input= subView.findViewById(R.id.input);
                inputLayout= subView.findViewById(R.id.inputLayout);
            }
        }
    }

    public interface AfterDialog {
        void result(String result);
        void result(String[] results);
    }

    public static DialogInput get(final Context context) {
        return new DialogInput(context);
    }

    private DialogInput(final Context context) {
        mContext= context;
        mBuilder= new AlertDialog.Builder(context);
        mInputItems= new ArrayList<>();
    }

    public void show(final String title, final int[] inputTypes, final String[] inputTitles,
                     final String[] inputValues, final AfterDialog mAfterDialog) {
        mBuilder.setTitle(Html.fromHtml(title));

        mRootView= LayoutInflater.from(mContext).inflate(R.layout.dialog_layout, null);
        inputContainer= mRootView.findViewById(R.id.inputContainer);
        inputContainer.removeAllViews();
        mInputItems.clear();
        for(int i=0; i<inputTypes.length; i++) {
            InputItems item= new InputItems(inputTypes[i]);
            if(item.subView!=null) {
                /* if(adapter!=null) { ((AutoCompleteTextView) item.input).setAdapter(adapter); } */
                //item.input.setHint(inputTitles[i]);
                item.inputLayout.setHint(inputTitles[i]);
                if(inputValues!=null) {
                    item.input.setText(inputValues[i]);
                }
                inputContainer.addView(item.subView);
                mInputItems.add(item);
            }
        }

        final EditText input= mInputItems.get(0).input;
        mBuilder.setView(mRootView);
        mBuilder.setCancelable(false);
        mBuilder.setOnDismissListener((dialog)->{
            FocusManager.hideFocus(mContext, input);
        }).setOnCancelListener((dialog)->{
            FocusManager.hideFocus(mContext, input);
        });
        mBuilder.setPositiveButton(android.R.string.ok, (dialog,which)->{
            FocusManager.hideFocus(mContext, input);
            dialog.dismiss();
            if(mAfterDialog!=null) {
                String[] results= new String[mInputItems.size()];
                for(int i=0; i<mInputItems.size(); i++) {
                    results[i]= mInputItems.get(i).input.getText().toString();
                }
                mAfterDialog.result(input.getText().toString());
                mAfterDialog.result(results);
            }
        });
        mBuilder.setNegativeButton(android.R.string.cancel, (dialog,which)->{
            FocusManager.hideFocus(mContext, input);
            dialog.cancel();
        });
        mBuilder.show();

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                ((Activity)mContext).runOnUiThread(()->{
                    input.requestFocus();
                    FocusManager.showFocus(mContext, input);
                });
            }
        }, 100);
        input.requestFocus();
        FocusManager.showFocus(mContext, input);
    }

    public static void showNumeric(final Context context, final String title, final String oldInput, final AfterDialog mAfterDialog) {
        get(context).show(title, new int[] { TYPE_NUMERIC }, new String[] { title },  new String[] { oldInput }, mAfterDialog);
    }
    public static void showString(final Context context, final String title, final String oldInput, final AfterDialog mAfterDialog) {
        get(context).show(title, new int[] { TYPE_STRING }, new String[] { title },  new String[] { oldInput }, mAfterDialog);
    }
    public static void showAutoString(final Context context, final String title,final String oldInput,
                                      final ArrayAdapter<String> adapter, final AfterDialog mAfterDialog) {
        get(context).show(title, new int[] { TYPE_STRING }, new String[] { title },  new String[] { oldInput }, mAfterDialog);
    }
    public static void showStringMulti(final Context context, final String title, final String oldInput, final AfterDialog mAfterDialog) {
        get(context).show(title, new int[] { TYPE_MULTI_LINE }, new String[] { title },  new String[] { oldInput }, mAfterDialog);
    }
}
