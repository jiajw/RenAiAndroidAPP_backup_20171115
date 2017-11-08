package com.yousails.chrenai.framework.util;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yousails.chrenai.R;

/**
 * User: jiajinwu
 * Date: 2017-11-03
 * Time: 10:49
 * 修改备注：
 * version:
 */


public class CustomDialog extends Dialog {
    public CustomDialog(@NonNull Context context) {
        super(context);
    }

    public CustomDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

    public static class Builder {

        private Context context;
        private String title;
        private String message;
        private String leftText;
        private String rightText;

        /**
         * Sets whether this dialog is canceled when touched outside the window's
         * bounds. If setting to true, the dialog is set to be cancelable if not already set.
         */
        private boolean canceledOnTouchOutside;
        /**
         * Sets whether this dialog is cancelable with the
         * {@link KeyEvent#KEYCODE_BACK BACK} key.
         */
        private boolean cancelable;

        private View contentView;
        private View horizontal_divide_view;
        private DialogInterface.OnClickListener leftButtonClickListener;
        private DialogInterface.OnClickListener rightButtonClickListener;

        private TextView tvDialogTitle;
        private TextView tvDialogContent;
        private TextView tvLeftButton, tvRightButton;
        private RelativeLayout rlCustomUIContent;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setTitle(int resId) {
            this.title = context.getString(resId);
            return this;
        }

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        public Builder setMessage(int resId) {
            this.message = context.getString(resId);
            return this;
        }

        public Builder setContentView(View view) {
            this.contentView = view;
            return this;
        }

        public Builder setLeftButton(int leftResId, DialogInterface.OnClickListener leftButtonClickListener) {
            this.leftText = context.getString(leftResId);
            this.leftButtonClickListener = leftButtonClickListener;
            return this;
        }

        public Builder setLeftButton(String leftText, DialogInterface.OnClickListener leftButtonClickListener) {
            this.leftText = leftText;
            this.leftButtonClickListener = leftButtonClickListener;
            return this;
        }

        public Builder setRightButton(int rightResId, DialogInterface.OnClickListener rightButtonClickListener) {
            this.rightText = context.getString(rightResId);
            this.rightButtonClickListener = rightButtonClickListener;
            return this;
        }

        public Builder setRightButton(String rightText, DialogInterface.OnClickListener rightButtonClickListener) {
            this.rightText = rightText;
            this.rightButtonClickListener = rightButtonClickListener;
            return this;
        }

        public Builder setCanceledOnTouchOutside(Boolean canceledOnTouchOutside) {
            this.canceledOnTouchOutside = canceledOnTouchOutside;
            return this;
        }

        public Builder setCancelable(Boolean cancelable) {
            this.cancelable = cancelable;
            return this;
        }


        public CustomDialog create() {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // instantiate the dialog with the custom Theme
            final CustomDialog customDialog = new CustomDialog(context, R.style.dialog);
            View layout = inflater.inflate(R.layout.dialog_normal_layout, null);
            customDialog.addContentView(layout, new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

            tvDialogTitle = (TextView) layout.findViewById(R.id.tv_dialog_title);
            tvDialogContent = (TextView) layout.findViewById(R.id.tv_dialog_message);
            tvLeftButton = (TextView) layout.findViewById(R.id.tv_btn_left);
            tvRightButton = (TextView) layout.findViewById(R.id.tv_btn_right);
            horizontal_divide_view = layout.findViewById(R.id.horizontal_divide_view);

            rlCustomUIContent = (RelativeLayout) layout.findViewById(R.id.rl_customer_ui_content);

            //title
            if (!TextUtils.isEmpty(title)) {
                tvDialogTitle.setText(title);
            } else {
                tvDialogTitle.setVisibility(View.GONE);
            }


            //cancel button
            if (!TextUtils.isEmpty(leftText)) {
                tvLeftButton.setText(leftText);
                if (leftButtonClickListener != null) {
                    tvLeftButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            leftButtonClickListener.onClick(customDialog, DialogInterface.BUTTON_NEGATIVE);
                        }
                    });
                }
            } else {
                horizontal_divide_view.setVisibility(View.GONE);
                tvLeftButton.setVisibility(View.GONE);
            }

            //confirm button

            if (!TextUtils.isEmpty(rightText)) {
                horizontal_divide_view.setVisibility(View.VISIBLE);
                tvRightButton.setText(rightText);
                if (rightButtonClickListener != null) {
                    tvRightButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            rightButtonClickListener.onClick(customDialog, DialogInterface.BUTTON_POSITIVE);
                        }
                    });
                }
            } else {
                tvRightButton.setVisibility(View.GONE);
                horizontal_divide_view.setVisibility(View.GONE);
            }

            //message
            if (!TextUtils.isEmpty(message)) {
                tvDialogContent.setText(message);
            } else if (contentView != null) {

                rlCustomUIContent.removeAllViews();
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                rlCustomUIContent.addView(contentView, params);
            }

            customDialog.setContentView(layout);
            customDialog.setCanceledOnTouchOutside(canceledOnTouchOutside);
            customDialog.setCancelable(cancelable);
            return customDialog;
        }

    }
}
