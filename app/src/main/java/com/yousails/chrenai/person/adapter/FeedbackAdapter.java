package com.yousails.chrenai.person.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yousails.chrenai.R;
import com.yousails.chrenai.config.AppPreference;
import com.yousails.chrenai.person.bean.FeedbackBean;
import com.yousails.chrenai.person.listener.OnFeedbackClickListener;
import com.yousails.chrenai.utils.StringUtil;
import com.yousails.chrenai.view.CircleImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/7/18.
 */

public class FeedbackAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    //加载更多item 状态切换
    private int mSwitch = 0;
    public static final int TYPE_NORMAL = 1;
    public static final int TYPE_FOOTER = 2;
    private Context mContext;
    private List<FeedbackBean> feedbackBeanList = new ArrayList<FeedbackBean>();
    private String userId;

    public FeedbackAdapter(Context context, List<FeedbackBean> feedbackBeanList){
        this.mContext=context;
        this.feedbackBeanList=feedbackBeanList;
        userId= AppPreference.getInstance(mContext).readUerId();

    }

    private OnFeedbackClickListener mListener;

    public void setOnItemClickListener(OnFeedbackClickListener li) {
        mListener = li;
    }



    public void  setFooterSwitch(int mSwitch) {
        this.mSwitch = mSwitch;
        notifyDataSetChanged();
    }

    public void notifyDataChanged(List<FeedbackBean> feedbackBeanList) {
        this.feedbackBeanList = feedbackBeanList;
        this.notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return feedbackBeanList==null?0+mSwitch:feedbackBeanList.size()+mSwitch;
    }


    @Override
    public int getItemViewType(int position) {

        if (position + 1 == getItemCount() && mSwitch == 1) {
            return TYPE_FOOTER;
        } else {
            return TYPE_NORMAL;
        }

    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_NORMAL) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.feedback_item, parent, false);
            return new CommentHolder(view);
        } else {
            View footerView = LayoutInflater.from(mContext).inflate(R.layout.column_item_footer, parent, false);
            return new FootViewHolder(footerView);
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_FOOTER) {
            ((FootViewHolder) holder).footView.setText(R.string.loading);
        } else {
          final FeedbackBean feedbackBean = feedbackBeanList.get(position);
            String headUrl = feedbackBean.getUser().getAvatar();
            String sex = feedbackBean.getUser().getSex();
            ((CommentHolder) holder).nameView.setText(feedbackBean.getUser().getName());

            ((CommentHolder) holder).contentView.setText(feedbackBean.getContent());

              ((CommentHolder) holder).createView.setText(feedbackBean.getCreated_at());
//            ((CommentHolder) holder).createView.setText(TimeUtil.getDistanceTime(feedbackBean.getCreated_at()));

            if (StringUtil.isNotNull(headUrl)) {
                Glide.with(mContext).load(headUrl).into(((CommentHolder) holder).headView);
            } else {
                if (StringUtil.isNotNull(sex)) {
                    if ("female".equals(sex)) {
                        ((CommentHolder) holder).headView.setImageResource(R.drawable.ic_avatar_woman);
                    } else {
                        ((CommentHolder) holder).headView.setImageResource(R.drawable.ic_avatar);
                    }
                }
            }

            //默认在布局中隐藏
            if(StringUtil.isNotNull(userId)&&userId.equals(""+feedbackBean.getUser_id())){
                ((CommentHolder) holder).deleteLayout.setVisibility(View.VISIBLE);
            }else{
                ((CommentHolder) holder).deleteLayout.setVisibility(View.GONE);
            }

            if (mListener == null) return;
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onItemClick(feedbackBean);
                }
            });


            ((CommentHolder) holder).deleteLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.delComment(feedbackBean);
                }
            });
        }
    }



    class CommentHolder extends RecyclerView.ViewHolder {
        private CircleImageView headView;
        private TextView nameView;
        private TextView contentView;
        private TextView createView;
        private TextView replyView;
        private RelativeLayout deleteLayout;

        public CommentHolder(View itemView) {
            super(itemView);
            headView = (CircleImageView) itemView.findViewById(R.id.iv_head);
            nameView = (TextView) itemView.findViewById(R.id.tv_name);
            contentView=(TextView) itemView.findViewById(R.id.tv_content);
            createView=(TextView) itemView.findViewById(R.id.tv_create_time);
            replyView=(TextView) itemView.findViewById(R.id.tv_reply_count);
            deleteLayout=(RelativeLayout)itemView.findViewById(R.id.del_layout);

        }
    }

    class FootViewHolder extends RecyclerView.ViewHolder {

        private ProgressBar progressBar;
        private TextView footView;

        public FootViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
            footView = (TextView) itemView.findViewById(R.id.foot_view_item_tv);
        }
    }
}
