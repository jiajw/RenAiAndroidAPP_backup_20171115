package com.yousails.chrenai.view;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.yousails.chrenai.R;
import com.yousails.chrenai.home.bean.ActivitiesBean;
import com.yousails.chrenai.person.listener.OnEnrollListener;

/**
 * Created by Administrator on 2017/8/1.
 */

public class ActivityEditDialog extends BaseDialog  implements View.OnClickListener{
    private Context mContext;
    private String uri;

    public ActivityEditDialog(Context context) {
        super(context);
        this.mContext = context;
    }

    public ActivityEditDialog(Context context, String uri) {
        super(context);
        this.mContext = context;
        this.uri = uri;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initContent();
        setAttributes(getWindow().getWindowManager().getDefaultDisplay()
                .getWidth(), Gravity.BOTTOM);
    }

    private void initContent() {
        setContentView(R.layout.dialog_activity_edit);

        TextView tvEdit = (TextView) findViewById(R.id.tv_edit);
        TextView tvDelete = (TextView) findViewById(R.id.tv_delete);
        TextView tvFinish = (TextView) findViewById(R.id.tv_finish);
        TextView tvCancel = (TextView) findViewById(R.id.tv_cancle);


        tvEdit.setOnClickListener(this);
        tvDelete.setOnClickListener(this);
        tvFinish.setOnClickListener(this);
        tvCancel.setOnClickListener(this);

    }

    private OnEditDialogListener listener;

    public void setOnEditDialogListener(OnEditDialogListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_edit:
                listener.onEdit();
                break;
            case R.id.tv_delete:
                listener.onDelete();
                break;
            case R.id.tv_finish:
                listener.onFinish();
                break;
            case R.id.tv_cancle:
                listener.onCancel();
                break;
        }
    }

    public interface OnEditDialogListener {
//        void onEdit( ActivitiesBean bean );
        void onEdit( );
        void onDelete( );
        void onFinish( );
        void onCancel( );
    }


}
