package com.yousails.chrenai.home.adapter;

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
import com.yousails.chrenai.home.bean.CommentBean;
import com.yousails.chrenai.home.listener.OnComItemClickListener;
import com.yousails.chrenai.utils.StringUtil;
import com.yousails.chrenai.utils.TimeUtil;
import com.yousails.chrenai.view.CircleImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/7/18.
 */

public class CommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    //加载更多item 状态切换
    private int mSwitch = 0;
    public static final int TYPE_NORMAL = 1;
    public static final int TYPE_FOOTER = 2;
    private Context mContext;
    private String userId;
    private String publisherId;
    private List<CommentBean> commentBeanList = new ArrayList<CommentBean>();
    public CommentAdapter(Context context,String publisherId,List<CommentBean> commentBeanList){
        this.mContext=context;
        this.publisherId=publisherId;
        this.commentBeanList=commentBeanList;
        userId= AppPreference.getInstance(mContext).readUerId();
    }

    private OnComItemClickListener mListener;

    public void setOnItemClickListener(OnComItemClickListener li) {
        mListener = li;
    }



    public void  setFooterSwitch(int mSwitch) {
        this.mSwitch = mSwitch;
        notifyDataSetChanged();
    }

    public void notifyDataChanged(List<CommentBean> commentBeanList) {
        this.commentBeanList = commentBeanList;
        this.notifyDataSetChanged();
    }

    /**
     * 更新点赞数目
     */
    public void notifyVoteChanged(CommentBean commentBean) {
        if(commentBean!=null){
            for(CommentBean commBean:commentBeanList){
                if(commentBean.getId().equals(commBean.getId())){
                    commBean.setVote_count(commentBean.getVote_count());
                    break;
                }
            }
            this.notifyDataSetChanged();
        }

    }


    @Override
    public int getItemCount() {
        return commentBeanList==null?0+mSwitch:commentBeanList.size()+mSwitch;
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
            View view = LayoutInflater.from(mContext).inflate(R.layout.comment_item, parent, false);
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
          final CommentBean commentBean = commentBeanList.get(position);
            String headUrl = commentBean.getUser().getAvatar();
            String sex = commentBean.getUser().getSex();
            String voteCount=commentBean.getVote_count();
            String replyCount=commentBean.getReply_count();
            ((CommentHolder) holder).nameView.setText(commentBean.getUser().getName());
            if(StringUtil.isNotNull(voteCount)&&!"0".equals(voteCount)){
                ((CommentHolder) holder).voteCountView.setText(commentBean.getVote_count());
            }else{
                ((CommentHolder) holder).voteCountView.setText("赞");
            }

            ((CommentHolder) holder).contentView.setText(commentBean.getContent());

              ((CommentHolder) holder).createView.setText(commentBean.getCreated_at_diff());
//            ((CommentHolder) holder).createView.setText(TimeUtil.getDistanceTime(commentBean.getCreated_at()));

            if(StringUtil.isNotNull(replyCount)){
                if("0".equals(replyCount)){
                    ((CommentHolder) holder).replyView.setText("回复");
                }else{
                    ((CommentHolder) holder).replyView.setText(replyCount+"回复");
                }
            }else{
                ((CommentHolder) holder).replyView.setText("回复");
            }


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


                if(StringUtil.isNotNull(userId)){
                    if(StringUtil.isNotNull(publisherId)){
                        if(userId.equals(publisherId)){
                            ((CommentHolder) holder).deleteLayout.setVisibility(View.VISIBLE);
                        }else{
                            if(userId.equals(commentBean.getUser_id())){
                                ((CommentHolder) holder).deleteLayout.setVisibility(View.VISIBLE);
                            }else{
                                ((CommentHolder) holder).deleteLayout.setVisibility(View.GONE);
                            }
                        }
                    }else{
                        if(userId.equals(commentBean.getUser_id())){
                            ((CommentHolder) holder).deleteLayout.setVisibility(View.VISIBLE);
                        }else{
                            ((CommentHolder) holder).deleteLayout.setVisibility(View.GONE);
                        }
                    }

                }else{
                    ((CommentHolder) holder).deleteLayout.setVisibility(View.GONE);
                }




            //默认在布局中隐藏
//            if(StringUtil.isNotNull(userId)&&userId.equals(commentBean.getUser_id())){
//                ((CommentHolder) holder).deleteLayout.setVisibility(View.VISIBLE);
//            }else{
//                ((CommentHolder) holder).deleteLayout.setVisibility(View.GONE);
//            }

            if (mListener == null) return;
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onItemClick(commentBean);
                }
            });

            ((CommentHolder) holder).voteCountView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.doVote(commentBean);
                }
            });

            ((CommentHolder) holder).deleteLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.delComment(commentBean);
                }
            });
        }
    }



    class CommentHolder extends RecyclerView.ViewHolder {
        private CircleImageView headView;
        private TextView nameView;
        private TextView voteCountView;
        private TextView contentView;
        private TextView createView;
        private TextView replyView;
        private RelativeLayout deleteLayout;

        public CommentHolder(View itemView) {
            super(itemView);
            headView = (CircleImageView) itemView.findViewById(R.id.iv_head);
            nameView = (TextView) itemView.findViewById(R.id.tv_name);
            voteCountView= (TextView) itemView.findViewById(R.id.iv_vote_count);
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
