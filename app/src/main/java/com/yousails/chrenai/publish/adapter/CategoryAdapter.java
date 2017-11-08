package com.yousails.chrenai.publish.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yousails.chrenai.R;
import com.yousails.chrenai.common.CommonHolder;
import com.yousails.chrenai.common.CommonRecyclerAdapter;
import com.yousails.chrenai.publish.bean.Category;
import com.yousails.chrenai.publish.event.CategoryClickEvent;
import com.yousails.common.event.EventBusManager;

/**
 * Author:WangKunHui
 * Date: 2017/7/13 13:56
 * Desc:
 * E-mail:life_artist@163.com
 */
public class CategoryAdapter extends CommonRecyclerAdapter<Category> {

    private Category selectedIdCategory;

    private int selectedId = -1;

    public CategoryAdapter(Category category) {
        if (category != null) {
            this.selectedIdCategory = category;
            selectedId = category.getId();
        }
    }

    @Override
    public CommonHolder<Category> setViewHolder(ViewGroup parent, int type) {
        return new CategoryHolder(parent);
    }

    public Category getSelectedIdCategory() {
        return selectedIdCategory;
    }

    private class CategoryHolder extends CommonHolder<Category> {

        private TextView content;

        private ImageView flag;

        public CategoryHolder(ViewGroup root) {
            super(root.getContext(), root, R.layout.adapter_item_category);
        }

        @Override
        public void bindView() {
            content = (TextView) itemView.findViewById(R.id.tv_content);
            flag = (ImageView) itemView.findViewById(R.id.iv_is_selected);
        }

        @Override
        public void bindData(final Category category) {
            if (category == null) {
                return;
            }
            content.setText(category.getName());

            if (category.getId() == selectedId) {
                flag.setVisibility(View.VISIBLE);
            } else {
                flag.setVisibility(View.GONE);
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (category.getId() == selectedId) {
                        return;
                    }

                    notifyDataSetChanged();
                    selectedId = category.getId();
                    EventBusManager.getInstance().post(new CategoryClickEvent(category));
                }
            });
        }
    }
}
