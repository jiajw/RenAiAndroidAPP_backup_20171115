package com.yousails.chrenai.person.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yousails.chrenai.R;
import com.yousails.chrenai.person.bean.RegisterInforBean;
import com.yousails.chrenai.person.listener.OnSignInListener;
import com.yousails.chrenai.person.ui.PersonActivity;
import com.yousails.chrenai.person.ui.RegistDetailActivity;
import com.yousails.chrenai.utils.StringUtil;
import com.yousails.chrenai.view.CircleImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/8/23.
 */

public class SignInAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    //加载更多item 状态切换
    private int mSwitch = 0;
    public static final int TYPE_HEADER = 0;
    public static final int TYPE_NORMAL = 1;
    public static final int TYPE_FOOTER = 2;
    private Context mContext;
    private List<RegisterInforBean> registerInforList = new ArrayList<RegisterInforBean>();
    private String type;
    private String reachedCount;
    private String leftCount;

    public SignInAdapter(Context context, List<RegisterInforBean> registerInforList){
        this.mContext=context;
        this.registerInforList=registerInforList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mHeaderView == null) {
            if (viewType == TYPE_NORMAL) {
                View view = LayoutInflater.from(mContext).inflate(R.layout.sign_in_item, parent, false);
                return new CustomHolder(view);
            } else {
                View footerView = LayoutInflater.from(mContext).inflate(R.layout.column_item_footer, parent, false);
                return new FootViewHolder(footerView);
            }
        } else {
            if (viewType == TYPE_HEADER) {
                return new HeadViewHolder(mHeaderView);

            } else if (viewType == TYPE_NORMAL) {
                View view = LayoutInflater.from(mContext).inflate(R.layout.sign_in_item, parent, false);
                return new CustomHolder(view);
            } else {
                View footerView = LayoutInflater.from(mContext).inflate(R.layout.column_item_footer, parent, false);
                return new FootViewHolder(footerView);
            }

        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (getItemViewType(position) == TYPE_HEADER) {

            if("reached".equals(type)){
                ((HeadViewHolder) viewHolder).reachedView.setBackgroundResource(R.drawable.sign_in_btn);
                ((HeadViewHolder) viewHolder).unreachedView.setBackgroundResource(R.color.main_blue_color);
            }else if("left".equals(type)){
                ((HeadViewHolder) viewHolder).reachedView.setBackgroundResource(R.color.main_blue_color);
                ((HeadViewHolder) viewHolder).unreachedView.setBackgroundResource(R.drawable.sign_in_btn);
            }

            if("0".equals(reachedCount)){
                ((HeadViewHolder) viewHolder).reachedView.setText("已到场  ");
            }else{
                ((HeadViewHolder) viewHolder).reachedView.setText("已到场  "+reachedCount);
            }


            if("0".equals(leftCount)){
                ((HeadViewHolder) viewHolder).unreachedView.setText("已离开  ");
            }else{
                ((HeadViewHolder) viewHolder).unreachedView.setText("已离开  "+leftCount);
            }


            ((HeadViewHolder) viewHolder).reachedView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(onSignInListener!=null){
                        onSignInListener.onReached();
                    }
                }
            });

            ((HeadViewHolder) viewHolder).unreachedView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(onSignInListener!=null){
                        onSignInListener.onLeft();
                    }
                }
            });


        } else if (getItemViewType(position) == TYPE_FOOTER) {
            ((FootViewHolder) viewHolder).footView.setText(R.string.loading);
        } else {
            final int pos = getRealPosition(viewHolder);
            final RegisterInforBean registerInforBean = registerInforList.get(pos);
            final String data = registerInforBean.getUser().getName();
            String headUrl = registerInforBean.getUser().getAvatar();
            boolean isCertificated = registerInforBean.getUser().is_certificated();

            if(0==position){
                ((CustomHolder) viewHolder).dividerLine.setVisibility(View.VISIBLE);
            }else{
                ((CustomHolder) viewHolder).dividerLine.setVisibility(View.GONE);
            }

            ((CustomHolder) viewHolder).nameView.setText(data);
            if (isCertificated) {
                ((CustomHolder) viewHolder).identiView.setVisibility(View.VISIBLE);
            }else{
                ((CustomHolder) viewHolder).identiView.setVisibility(View.GONE);
            }


            if (StringUtil.isNotNull(headUrl)) {
                Glide.with(mContext).load(headUrl).into(((CustomHolder) viewHolder).headView);
            } else {
                String sex = registerInforBean.getUser().getSex();
                if (StringUtil.isNotNull(sex)) {

                    if ("female".equals(sex)) {
                        ((CustomHolder) viewHolder).headView.setImageResource(R.drawable.ic_avatar_woman);
                    } else {
                        ((CustomHolder) viewHolder).headView.setImageResource(R.drawable.ic_avatar);
                    }
                }
            }


            ((CustomHolder) viewHolder).headView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, PersonActivity.class);
                    intent.putExtra("user",registerInforBean.getUser());
                    mContext.startActivity(intent);
                }
            });


            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, RegistDetailActivity.class);
                    intent.putExtra("cid",registerInforBean.getId());
                    mContext.startActivity(intent);
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return mHeaderView == null ? registerInforList.size() + mSwitch  : registerInforList.size() + 1 + mSwitch;
//        return  registerInforList.size() + mSwitch ;
    }

    public int getRealPosition(RecyclerView.ViewHolder holder) {
        int position = holder.getLayoutPosition();
//        return position;
        return mHeaderView == null ? position : position - 1;
    }

    private View mHeaderView;

    public void setHeaderView(View headerView,String type,String reachedCount,String leftCount) {
        mHeaderView = headerView;
        this.type=type;
        this.reachedCount=reachedCount;
        this.leftCount=leftCount;
        this.notifyDataSetChanged();
//        notifyItemInserted(0);
    }

    public void  setFooterSwitch(int mSwitch) {
        this.mSwitch = mSwitch;
        this.notifyDataSetChanged();
    }


    public void notifyDataChanged(List<RegisterInforBean> registerInforList) {
      this.registerInforList = registerInforList;
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (mHeaderView == null) {
            if (position + 1 == getItemCount() && mSwitch == 1) {
                return TYPE_FOOTER;
            } else {
                return TYPE_NORMAL;
            }
        } else {
            if (position == 0) {
                return TYPE_HEADER;
            } else {
                if (position + 1 == getItemCount() && mSwitch == 1) {
                    return TYPE_FOOTER;
                } else {
                    return TYPE_NORMAL;
                }
            }
        }

    }


    class CustomHolder extends RecyclerView.ViewHolder {
        private CircleImageView headView;
        private TextView nameView;
        private TextView identiView;
        private RelativeLayout deletedLayout;
        private TextView allowTitle;
        private LinearLayout dividerLine;


        public CustomHolder(View view) {
            super(view);
            dividerLine=(LinearLayout) view.findViewById(R.id.divider_line);

            headView = (CircleImageView) view.findViewById(R.id.iv_head);
            nameView = (TextView) view.findViewById(R.id.name_tview);
            identiView = (TextView) view.findViewById(R.id.iv_state);

            deletedLayout = (RelativeLayout)view.findViewById(R.id.deleted_layout);
            allowTitle = (TextView) view.findViewById(R.id.allow_tview);

        }
    }


    class HeadViewHolder extends RecyclerView.ViewHolder {
        private TextView reachedView;
        private TextView unreachedView;
        public HeadViewHolder(View itemView){
            super(itemView);
           reachedView=(TextView)itemView.findViewById(R.id.reached_tview);
           unreachedView=(TextView)itemView.findViewById(R.id.unreached_tview);
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


    private OnSignInListener onSignInListener;

    public void setOnItemClickListener(OnSignInListener onSignInListener) {
        this.onSignInListener = onSignInListener;
    }

}
