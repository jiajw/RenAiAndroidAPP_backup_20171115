package com.yousails.chrenai.publish.adapter;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.yousails.chrenai.R;
import com.yousails.chrenai.common.CommonHolder;
import com.yousails.chrenai.common.CommonRecyclerAdapter;

import java.util.List;

/**
 * Author:WangKunHui
 * Date: 2017/7/16 16:56
 * Desc:
 * E-mail:life_artist@163.com
 */
public class OptionsAdapter extends CommonRecyclerAdapter<String> {

    private boolean isSingle;

    private int maxOption = 10;

    private int defaultOption = 3;

    private boolean isShowAddView = true;

    private List<String> options;

    public OptionsAdapter(List<String> options) {

        this.options = options;

        if (options == null || options.size() == 0) {
            for (int i = 0; i <= defaultOption; i++) {
                dataList.add("");
            }
        } else {
            dataList.addAll(options);
            dataList.add("");
        }
    }

    @Override
    public OptionsHolder setViewHolder(ViewGroup parent, int type) {
        return new OptionsHolder(parent);
    }

    private class OptionsHolder extends CommonHolder<String> {

        private EditText inputView;

        private LinearLayout addView;

        private LinearLayout inputContainer;

        private ImageView clearButton;

        public OptionsHolder(ViewGroup root) {
            super(root.getContext(), root, R.layout.adapter_item_option);
        }

        @Override
        public void bindView() {
            addView = (LinearLayout) itemView.findViewById(R.id.ll_add_view);
            inputView = (EditText) itemView.findViewById(R.id.et_input_view);

            inputContainer = (LinearLayout) itemView.findViewById(R.id.ll_input_container);
            clearButton = (ImageView) itemView.findViewById(R.id.iv_input_clear);
        }

        @Override
        public void bindData(String string) {

            final int layoutPosition = getLayoutPosition();

            if (isShowAddView) {

                if (layoutPosition == dataList.size() - 1) {

                    addView.setVisibility(View.VISIBLE);
                    inputContainer.setVisibility(View.GONE);

                    addView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            addMoreOption();
                        }
                    });
                } else {
                    addView.setVisibility(View.GONE);
                    inputView.setVisibility(View.VISIBLE);
                    inputView.setHint("选项" + (layoutPosition + 1));

                    if (!TextUtils.isEmpty(string)) {
                        inputView.setText(string);
                    }
                }
            } else {
                addView.setVisibility(View.GONE);
                inputContainer.setVisibility(View.VISIBLE);
                inputView.setHint("选项" + (layoutPosition + 1));

                if (!TextUtils.isEmpty(string)) {
                    inputView.setText(string);
                }
            }

            if (inputContainer.getVisibility() == View.VISIBLE) {
                inputView.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        String temp = s.toString();
                        if (TextUtils.isEmpty(temp)) {
                            clearButton.setVisibility(View.INVISIBLE);
                        } else {
                            clearButton.setVisibility(View.VISIBLE);
                        }
                    }
                });

                clearButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        inputView.setText("");
                    }
                });
            }
        }
    }

    private void addMoreOption() {
        int size = dataList.size();
        if (size == 10) {
            isShowAddView = false;
            notifyDataSetChanged();
        } else {
            dataList.add(size - 1, "");
            notifyItemInserted(size - 1);
        }
    }
}
