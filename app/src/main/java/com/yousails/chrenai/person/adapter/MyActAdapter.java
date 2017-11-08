package com.yousails.chrenai.person.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yousails.chrenai.R;
import com.yousails.chrenai.home.adapter.BannderHolder;
import com.yousails.chrenai.home.adapter.ColumnAdapter;
import com.yousails.chrenai.home.bean.ActivitiesBean;
import com.yousails.chrenai.home.bean.EnrollBean;
import com.yousails.chrenai.home.listener.OnItemClickListener;
import com.yousails.chrenai.home.listener.VotedEvent;
import com.yousails.chrenai.person.bean.AuthorityBean;
import com.yousails.chrenai.person.bean.FeedbackBean;
import com.yousails.chrenai.person.bean.UserPermsBean;
import com.yousails.chrenai.person.ui.ActivityFeedbackActivity;
import com.yousails.chrenai.person.ui.AttenStatisActivity;
import com.yousails.chrenai.person.ui.ReplyActivity;
import com.yousails.chrenai.publish.listener.OnPublishClickListener;
import com.yousails.chrenai.utils.StringUtil;
import com.yousails.chrenai.utils.TimeUtil;
import com.yousails.chrenai.view.CircleImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/7/3.
 */

public class MyActAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    //加载更多item 状态切换
    private int mSwitch = 0;

    public static final int TYPE_HEADER = 0;
    public static final int TYPE_NORMAL = 1;
    public static final int TYPE_FOOTER = 2;

    public static final int TYPE_RELEASE_NEW = 3;
    public static final int TYPE_RELEASE_END = 4;
    public static final int TYPE_ENJOY_NEW = 5;
    public static final int TYPE_ENJOY_END = 6;

    private Context mContext;
    private List<ActivitiesBean> activitiesBeanList = new ArrayList<ActivitiesBean>();
    private List<String> mDatas;

    private int type = 0;
    private String from = "";

    //适配器初始化
    public MyActAdapter(Context context, List<ActivitiesBean> activitiesBeanList, int type, String from) {
        mContext = context;
        this.type = type;
        this.from = from;
        this.activitiesBeanList = activitiesBeanList;
    }

    private View mHeaderView;

    private OnPublishClickListener mListener;

    public void setOnItemClickListener(OnPublishClickListener li) {
        mListener = li;
    }

    public void setHeaderView(View headerView) {
        mHeaderView = headerView;
        notifyItemInserted(0);
    }

    public View getHeaderView() {
        return mHeaderView;
    }


    public void  setFooterSwitch(int mSwitch) {
        this.mSwitch = mSwitch;
        this.notifyDataSetChanged();
    }


    public void notifyDataChanged(List<ActivitiesBean> activitiesList) {
        this.activitiesBeanList = activitiesList;
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
       /* if (mHeaderView == null) return TYPE_NORMAL;
        if (position == 0) return TYPE_HEADER;
        return TYPE_NORMAL;*/


        /*if (mHeaderView == null) {
            if (position + 1 == getItemCount()) {
                return TYPE_FOOTER;
            } else {
                return TYPE_NORMAL;
            }
        } else {
            if (position == 0) {
                return TYPE_HEADER;
            } else {
                if (position + 1 == getItemCount()) {
                    return TYPE_FOOTER;
                } else {
                    return TYPE_NORMAL;
                }
            }
        }*/


        if (mHeaderView == null) {
            if (position + 1 == getItemCount() && mSwitch == 1) {
                return TYPE_FOOTER;
            } else {
                if(type==1){
                    return TYPE_RELEASE_NEW;
                }else if(type==2){
                    return TYPE_RELEASE_END;
                }else if(type==3){
                    return TYPE_ENJOY_NEW;
                }else if(type==4){
                    return TYPE_ENJOY_END;
                }
                return TYPE_NORMAL;
            }
        } else {
            if (position == 0) {
                return TYPE_HEADER;
            } else {
                if (position + 1 == getItemCount() && mSwitch == 1) {
                    return TYPE_FOOTER;
                } else {
                    if(type==1){
                        return TYPE_RELEASE_NEW;
                    }else if(type==2){
                        return TYPE_RELEASE_END;
                    }else if(type==3){
                        return TYPE_ENJOY_NEW;
                    }else if(type==4){
                        return TYPE_ENJOY_END;
                    }
                    return TYPE_NORMAL;
                }
            }
        }


    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        /*if (mHeaderView != null && viewType == TYPE_HEADER) {
            return new BannderHolder(mHeaderView);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.column_item, parent, false);

            return new CustomHolder(view);
        }*/

        if (mHeaderView == null) {
            if (viewType == TYPE_NORMAL) {
                View view = LayoutInflater.from(mContext).inflate(R.layout.myact_item, parent, false);
                return new CustomHolder(view);
            } else if (viewType == TYPE_RELEASE_NEW) {
                View view = LayoutInflater.from(mContext).inflate(R.layout.item_release_new, parent, false);
                return new ReleaseNewHolder(view);
            }else if (viewType == TYPE_RELEASE_END) {
                View view = LayoutInflater.from(mContext).inflate(R.layout.item_release_end, parent, false);
                return new ReleaseEndHolder(view);
            }else if (viewType == TYPE_ENJOY_NEW) {
                View view = LayoutInflater.from(mContext).inflate(R.layout.item_enjoy_new, parent, false);
                return new EnjoyNewHolder(view);
            }else if (viewType == TYPE_ENJOY_END) {
                View view = LayoutInflater.from(mContext).inflate(R.layout.item_enjoy_end, parent, false);
                return new EnjoyEndHolder(view);
            } else {
                View footerView = LayoutInflater.from(mContext).inflate(R.layout.column_item_footer, parent, false);
                return new FootViewHolder(footerView);
            }

        } else {
            if (viewType == TYPE_HEADER) {
                return new BannderHolder(mHeaderView);
            } else if (viewType == TYPE_NORMAL) {
                View view = LayoutInflater.from(mContext).inflate(R.layout.myact_item, parent, false);
                return new CustomHolder(view);
            } else if (viewType == TYPE_RELEASE_NEW) {
                View view = LayoutInflater.from(mContext).inflate(R.layout.item_release_new, parent, false);
                return new ReleaseNewHolder(view);
            }else if (viewType == TYPE_RELEASE_END) {
                View view = LayoutInflater.from(mContext).inflate(R.layout.item_release_end, parent, false);
                return new ReleaseEndHolder(view);
            }else if (viewType == TYPE_ENJOY_NEW) {
                View view = LayoutInflater.from(mContext).inflate(R.layout.item_enjoy_new, parent, false);
                return new EnjoyNewHolder(view);
            }else if (viewType == TYPE_ENJOY_END) {
                View view = LayoutInflater.from(mContext).inflate(R.layout.item_enjoy_end, parent, false);
                return new EnjoyEndHolder(view);
            }else {
                View footerView = LayoutInflater.from(mContext).inflate(R.layout.column_item_footer, parent, false);
                return new FootViewHolder(footerView);
            }

        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (getItemViewType(position) == TYPE_HEADER) {
            ((BannderHolder) viewHolder).bindItem();
        } else if (getItemViewType(position) == TYPE_FOOTER) {
            ((FootViewHolder) viewHolder).footView.setText(R.string.loading);
        }else if (getItemViewType(position) == TYPE_RELEASE_NEW) {
            final int pos = getRealPosition(viewHolder);
            final ActivitiesBean activitiesBean = activitiesBeanList.get(pos);
            final String name = activitiesBean.getUser().getName();
            String headUrl = activitiesBean.getUser().getAvatar();
            boolean isCertificated = activitiesBean.getUser().is_certificated();
            String coverimage = activitiesBean.getCover_image();
            String starTime = activitiesBean.getStarted_at();
            if (viewHolder instanceof ReleaseNewHolder) {
                ((ReleaseNewHolder) viewHolder).nameTitle.setText(activitiesBean.getTitle());
                ((ReleaseNewHolder) viewHolder).nameView.setText(name);
                if (isCertificated) {
                    ((ReleaseNewHolder) viewHolder).identiView.setVisibility(View.VISIBLE);
                }else{
                    ((ReleaseNewHolder) viewHolder).identiView.setVisibility(View.GONE);
                }

                ((ReleaseNewHolder) viewHolder).nameLocation.setText(activitiesBean.getDistrict() + "  " + StringUtil.formatDistance(activitiesBean.getDistance()));
                if (StringUtil.isNotNull(starTime)) {
                    ((ReleaseNewHolder) viewHolder).nameDate.setText(TimeUtil.parseDate(starTime) + "  (" + TimeUtil.getWeek(starTime) + ")");
                    ((ReleaseNewHolder) viewHolder).startimeView.setText(TimeUtil.parseTime(starTime));
                }

                if (StringUtil.isNotNull(headUrl)) {
                    Glide.with(mContext).load(headUrl).into(((ReleaseNewHolder) viewHolder).headView);
                } else {
                    String sex = activitiesBean.getUser().getSex();
                    if (StringUtil.isNotNull(sex)) {

                        if ("female".equals(sex)) {
                            ((ReleaseNewHolder) viewHolder).headView.setImageResource(R.drawable.ic_avatar_woman);
                        } else {
                            ((ReleaseNewHolder) viewHolder).headView.setImageResource(R.drawable.ic_avatar);
                        }
                    }
                }

                if (StringUtil.isNotNull(coverimage)) {
                    Glide.with(mContext).load(coverimage).into(((ReleaseNewHolder) viewHolder).picView);
                }
                if (mListener == null) return;
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.onItemClick(activitiesBean);
                    }
                });

                ((ReleaseNewHolder) viewHolder).btnEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.editor(activitiesBean);
                    }
                });

                ((ReleaseNewHolder) viewHolder).btnSign.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.doMore(activitiesBean);
                    }
                });

                ((ReleaseNewHolder) viewHolder).btnWeituo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.entrustManage(activitiesBean);
                    }
                });


                if(from.equals("mine")) {
                    /*((ReleaseNewHolder) viewHolder).btnEdit.setVisibility(View.VISIBLE);
                    ((ReleaseNewHolder) viewHolder).btnSign.setVisibility(View.VISIBLE);
                    ((ReleaseNewHolder) viewHolder).btnWeituo.setVisibility(View.VISIBLE);*/
                    ((ReleaseNewHolder) viewHolder).rlAction.setVisibility(View.VISIBLE);

                }else{
                    /*((ReleaseNewHolder) viewHolder).btnEdit.setVisibility(View.GONE);
                    ((ReleaseNewHolder) viewHolder).btnEdit.setVisibility(View.GONE);
                    ((ReleaseNewHolder) viewHolder).btnEdit.setVisibility(View.GONE);*/
                    ((ReleaseNewHolder) viewHolder).rlAction.setVisibility(View.GONE);

                }
            }


        }
        else if (getItemViewType(position) == TYPE_RELEASE_END) {
            final int pos = getRealPosition(viewHolder);
            final ActivitiesBean activitiesBean = activitiesBeanList.get(pos);
            final String name = activitiesBean.getUser().getName();
            String headUrl = activitiesBean.getUser().getAvatar();
            boolean isCertificated = activitiesBean.getUser().is_certificated();
            String coverimage = activitiesBean.getCover_image();
            String starTime = activitiesBean.getStarted_at();
            if (viewHolder instanceof ReleaseEndHolder) {
                if(activitiesBean.getUnread_feedback_count()>0){
                    ((ReleaseEndHolder) viewHolder).ivRed.setVisibility(View.VISIBLE);
                }else{
                    ((ReleaseEndHolder) viewHolder).ivRed.setVisibility(View.GONE);
                }
                ((ReleaseEndHolder) viewHolder).nameTitle.setText(activitiesBean.getTitle());
                ((ReleaseEndHolder) viewHolder).nameView.setText(name);
                if (isCertificated) {
                    ((ReleaseEndHolder) viewHolder).identiView.setVisibility(View.VISIBLE);
                }else{
                    ((ReleaseEndHolder) viewHolder).identiView.setVisibility(View.GONE);
                }

                ((ReleaseEndHolder) viewHolder).nameLocation.setText(activitiesBean.getDistrict() + "  " + StringUtil.formatDistance(activitiesBean.getDistance()));

                if (StringUtil.isNotNull(starTime)) {
                    ((ReleaseEndHolder) viewHolder).nameDate.setText(TimeUtil.parseDate(starTime) + "  (" + TimeUtil.getWeek(starTime) + ")");
                    ((ReleaseEndHolder) viewHolder).startimeView.setText(TimeUtil.parseTime(starTime));
                }

                if (StringUtil.isNotNull(headUrl)) {
                    Glide.with(mContext).load(headUrl).into(((ReleaseEndHolder) viewHolder).headView);
                } else {
                    String sex = activitiesBean.getUser().getSex();
                    if (StringUtil.isNotNull(sex)) {

                        if ("female".equals(sex)) {
                            ((ReleaseEndHolder) viewHolder).headView.setImageResource(R.drawable.ic_avatar_woman);
                        } else {
                            ((ReleaseEndHolder) viewHolder).headView.setImageResource(R.drawable.ic_avatar);
                        }
                    }
                }


                if (StringUtil.isNotNull(coverimage)) {
                    Glide.with(mContext).load(coverimage).into(((ReleaseEndHolder) viewHolder).picView);
                }


                if (mListener == null) return;
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.onItemClick(activitiesBean);
                    }
                });

                ((ReleaseEndHolder) viewHolder).btnSign.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(mContext,AttenStatisActivity.class);
                        intent.putExtra("id",activitiesBean.getId());
                        intent.putExtra("sign_type",activitiesBean.getSign_type());
                        mContext.startActivity(intent);
                    }
                });


                ((ReleaseEndHolder) viewHolder).btnWeituo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(mContext,ActivityFeedbackActivity.class);
                        intent.putExtra("id",activitiesBean.getId());
                        mContext.startActivity(intent);
                    }
                });

                if(from.equals("mine")) {
                    /*((ReleaseEndHolder) viewHolder).btnEdit.setVisibility(View.GONE);
                    ((ReleaseEndHolder) viewHolder).btnSign.setVisibility(View.VISIBLE);
                    ((ReleaseEndHolder) viewHolder).btnWeituo.setVisibility(View.VISIBLE);*/

                    ((ReleaseEndHolder) viewHolder).rlAction.setVisibility(View.VISIBLE);

                }else{
                    /*((ReleaseEndHolder) viewHolder).btnEdit.setVisibility(View.GONE);
                    ((ReleaseEndHolder) viewHolder).btnEdit.setVisibility(View.GONE);
                    ((ReleaseEndHolder) viewHolder).btnEdit.setVisibility(View.GONE);*/
                    ((ReleaseEndHolder) viewHolder).rlAction.setVisibility(View.GONE);

                }
            }
        }
        else if (getItemViewType(position) == TYPE_ENJOY_NEW) {
            final int pos = getRealPosition(viewHolder);
            final ActivitiesBean activitiesBean = activitiesBeanList.get(pos);
            final String name = activitiesBean.getUser().getName();
            String headUrl = activitiesBean.getUser().getAvatar();
            boolean isCertificated = activitiesBean.getUser().is_certificated();
            String coverimage = activitiesBean.getCover_image();
            String starTime = activitiesBean.getStarted_at();

            if (viewHolder instanceof EnjoyNewHolder) {
                ((EnjoyNewHolder) viewHolder).nameTitle.setText(activitiesBean.getTitle());
                ((EnjoyNewHolder) viewHolder).nameView.setText(name);
                if (isCertificated) {
                    ((EnjoyNewHolder) viewHolder).identiView.setVisibility(View.VISIBLE);
                }else{
                    ((EnjoyNewHolder) viewHolder).identiView.setVisibility(View.GONE);
                }

                ((EnjoyNewHolder) viewHolder).nameLocation.setText(activitiesBean.getDistrict() + "  " + StringUtil.formatDistance(activitiesBean.getDistance()));

                if (StringUtil.isNotNull(starTime)) {
                    ((EnjoyNewHolder) viewHolder).nameDate.setText(TimeUtil.parseDate(starTime) + "  (" + TimeUtil.getWeek(starTime) + ")");
                    ((EnjoyNewHolder) viewHolder).startimeView.setText(TimeUtil.parseTime(starTime));
                }


                if (StringUtil.isNotNull(headUrl)) {
                    Glide.with(mContext).load(headUrl).into(((EnjoyNewHolder) viewHolder).headView);
                } else {
                    String sex = activitiesBean.getUser().getSex();
                    if (StringUtil.isNotNull(sex)) {

                        if ("female".equals(sex)) {
                            ((EnjoyNewHolder) viewHolder).headView.setImageResource(R.drawable.ic_avatar_woman);
                        } else {
                            ((EnjoyNewHolder) viewHolder).headView.setImageResource(R.drawable.ic_avatar);
                        }
                    }
                }

                if (StringUtil.isNotNull(coverimage)) {
                    Glide.with(mContext).load(coverimage).into(((EnjoyNewHolder) viewHolder).picView);
                }
                UserPermsBean userPermsBean=activitiesBean.getUserPerms();
                if(userPermsBean!=null){
                    List<AuthorityBean> authorityBeanList= userPermsBean.getData();
                    if(authorityBeanList!=null&&authorityBeanList.size()>0){
                        ((EnjoyNewHolder) viewHolder).weituoLayout.setVisibility(View.VISIBLE);
                    }else{
                        ((EnjoyNewHolder) viewHolder).weituoLayout.setVisibility(View.GONE);
                    }

                }else{
                    ((EnjoyNewHolder) viewHolder).weituoLayout.setVisibility(View.GONE);
                }

                ((EnjoyNewHolder) viewHolder).btn_weituo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mListener!=null){
                            mListener.doMore(activitiesBean);
                        }
                    }
                });

                if (mListener == null) return;
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.onItemClick(activitiesBean);
                    }
                });
            }
        }
        else if (getItemViewType(position) == TYPE_ENJOY_END) {
            final int pos = getRealPosition(viewHolder);
            final ActivitiesBean activitiesBean = activitiesBeanList.get(pos);
            final String name = activitiesBean.getUser().getName();
            String headUrl = activitiesBean.getUser().getAvatar();
            boolean isCertificated = activitiesBean.getUser().is_certificated();
            String coverimage = activitiesBean.getCover_image();
            String starTime = activitiesBean.getStarted_at();
            if (viewHolder instanceof EnjoyEndHolder) {
                if(activitiesBean.getUnread_feedback_count()>0){
                    ((EnjoyEndHolder) viewHolder).ivRed.setVisibility(View.VISIBLE);
                }else{
                    ((EnjoyEndHolder) viewHolder).ivRed.setVisibility(View.GONE);
                }
                ((EnjoyEndHolder) viewHolder).nameTitle.setText(activitiesBean.getTitle());
                ((EnjoyEndHolder) viewHolder).nameView.setText(name);
                if (isCertificated) {
                    ((EnjoyEndHolder) viewHolder).identiView.setVisibility(View.VISIBLE);
                }else{
                    ((EnjoyEndHolder) viewHolder).identiView.setVisibility(View.GONE);
                }

                ((EnjoyEndHolder) viewHolder).nameLocation.setText(activitiesBean.getDistrict() + "  " + StringUtil.formatDistance(activitiesBean.getDistance()));

                if (StringUtil.isNotNull(starTime)) {
                    ((EnjoyEndHolder) viewHolder).nameDate.setText(TimeUtil.parseDate(starTime) + "  (" + TimeUtil.getWeek(starTime) + ")");
                    ((EnjoyEndHolder) viewHolder).startimeView.setText(TimeUtil.parseTime(starTime));
                }

                if (StringUtil.isNotNull(headUrl)) {
                    Glide.with(mContext).load(headUrl).into(((EnjoyEndHolder) viewHolder).headView);
                } else {
                    String sex = activitiesBean.getUser().getSex();
                    if (StringUtil.isNotNull(sex)) {

                        if ("female".equals(sex)) {
                            ((EnjoyEndHolder) viewHolder).headView.setImageResource(R.drawable.ic_avatar_woman);
                        } else {
                            ((EnjoyEndHolder) viewHolder).headView.setImageResource(R.drawable.ic_avatar);
                        }
                    }
                }


                if (StringUtil.isNotNull(coverimage)) {
                    Glide.with(mContext).load(coverimage).into(((EnjoyEndHolder) viewHolder).picView);
                }

                UserPermsBean userPermsBean=activitiesBean.getUserPerms();
                if(userPermsBean!=null){
                    List<AuthorityBean> authorityBeanList= userPermsBean.getData();
                    if(authorityBeanList!=null&&authorityBeanList.size()>0){
                        EnrollBean userApplication=activitiesBean.getUserApplication();
                        if(userApplication!=null){
                            ((EnjoyEndHolder) viewHolder).titleView.setText(userApplication.getEntrust_title());
                            ((EnjoyEndHolder) viewHolder).titleView.setVisibility(View.VISIBLE);
                        }

                    }else{
                        ((EnjoyEndHolder) viewHolder).titleView.setVisibility(View.GONE);
                    }

                }else{
                    ((EnjoyEndHolder) viewHolder).titleView.setVisibility(View.GONE);
                }


                ((EnjoyEndHolder) viewHolder).btnWeituo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(mContext,ReplyActivity.class);
                        intent.putExtra("id",activitiesBean.getId());
                        mContext.startActivity(intent);
                    }
                });

                if (mListener == null) return;
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.onItemClick(activitiesBean);
                    }
                });
            }
        }else {
            final int pos = getRealPosition(viewHolder);
            final ActivitiesBean activitiesBean = activitiesBeanList.get(pos);
            final String name = activitiesBean.getUser().getName();
            String headUrl = activitiesBean.getUser().getAvatar();
            boolean isCertificated = activitiesBean.getUser().is_certificated();
            String coverimage = activitiesBean.getCover_image();
            String starTime = activitiesBean.getStarted_at();
            if (viewHolder instanceof CustomHolder) {
                ((CustomHolder) viewHolder).nameView.setText(name);
                ((CustomHolder) viewHolder).nameTitle.setText(activitiesBean.getTitle());
                if (isCertificated) {
                    ((CustomHolder) viewHolder).identiView.setVisibility(View.VISIBLE);
                }else{
                    ((CustomHolder) viewHolder).identiView.setVisibility(View.GONE);
                }

                ((CustomHolder) viewHolder).nameLocation.setText(activitiesBean.getDistrict() + "  " + StringUtil.formatDistance(activitiesBean.getDistance()));

                if (StringUtil.isNotNull(starTime)) {
                    ((CustomHolder) viewHolder).nameDate.setText(TimeUtil.parseDate(starTime) + "  (" + TimeUtil.getWeek(starTime) + ")");
                    ((CustomHolder) viewHolder).startimeView.setText(TimeUtil.parseTime(starTime));
                }

                if (StringUtil.isNotNull(headUrl)) {
                    Glide.with(mContext).load(headUrl).into(((CustomHolder) viewHolder).headView);
                } else {
                    String sex = activitiesBean.getUser().getSex();
                    if (StringUtil.isNotNull(sex)) {

                        if ("female".equals(sex)) {
                            ((CustomHolder) viewHolder).headView.setImageResource(R.drawable.ic_avatar_woman);
                        } else {
                            ((CustomHolder) viewHolder).headView.setImageResource(R.drawable.ic_avatar);
                        }
                    }
                }


                if (StringUtil.isNotNull(coverimage)) {
                    Glide.with(mContext).load(coverimage).into(((CustomHolder) viewHolder).picView);
                }


                if (mListener == null) return;
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.onItemClick(activitiesBean);
                    }
                });
            }
        }


    }

    @Override
    public int getItemCount() {
//        return mHeaderView == null ? activitiesBeanList.size() : activitiesBeanList.size() + 1;
//        return mHeaderView == null ? activitiesBeanList.size() + 1 : activitiesBeanList.size() + 2;
        return mHeaderView == null ? activitiesBeanList.size() + mSwitch : activitiesBeanList.size() + 1 + mSwitch;
    }

    public int getRealPosition(RecyclerView.ViewHolder holder) {
        int position = holder.getLayoutPosition();
        return mHeaderView == null ? position : position - 1;
    }


    class CustomHolder extends RecyclerView.ViewHolder {
        private CircleImageView headView;
        private TextView nameView;
        private TextView identiView;
        private ImageView picView;

        private TextView nameTitle;
        private TextView nameDate;
        private TextView nameLocation;
        private TextView startimeView;

        public CustomHolder(View view) {
            super(view);
            headView = (CircleImageView) view.findViewById(R.id.iv_head);
            nameView = (TextView) view.findViewById(R.id.tv_name);
            identiView = (TextView) view.findViewById(R.id.iv_state);
            picView = (ImageView) view.findViewById(R.id.iv_pic);

            nameTitle = (TextView) view.findViewById(R.id.tv_title);
            nameDate = (TextView) view.findViewById(R.id.tv_date);
            nameLocation = (TextView) view.findViewById(R.id.tv_location);
            startimeView = (TextView) view.findViewById(R.id.tv_start_time);

        }
    }


    class ReleaseNewHolder extends RecyclerView.ViewHolder {
        private CircleImageView headView;
        private TextView nameView;
        private TextView identiView;
        private ImageView picView;

        private TextView nameTitle;
        private TextView nameDate;
        private TextView nameLocation;
        private TextView startimeView;
        private TextView btnEdit;
        private TextView btnSign;
        private TextView btnWeituo;
        private RelativeLayout rlAction;

        public ReleaseNewHolder(View view) {
            super(view);
            headView = (CircleImageView) view.findViewById(R.id.iv_head);
            nameView = (TextView) view.findViewById(R.id.tv_name);
            identiView = (TextView) view.findViewById(R.id.iv_state);
            picView = (ImageView) view.findViewById(R.id.iv_pic);

            nameTitle = (TextView) view.findViewById(R.id.tv_title);
            nameDate = (TextView) view.findViewById(R.id.tv_date);
            nameLocation = (TextView) view.findViewById(R.id.tv_location);
            startimeView = (TextView) view.findViewById(R.id.tv_start_time);
            btnEdit = (TextView) view.findViewById(R.id.btn_edit);
            btnSign = (TextView) view.findViewById(R.id.btn_sign);
            btnWeituo = (TextView) view.findViewById(R.id.btn_weituo);
            rlAction = (RelativeLayout) view.findViewById(R.id.rl_action);

        }
    }


    class ReleaseEndHolder extends RecyclerView.ViewHolder {
        private CircleImageView headView;
        private TextView nameView;
        private TextView identiView;
        private ImageView picView;

        private TextView nameTitle;
        private TextView nameDate;
        private TextView nameLocation;
        private TextView startimeView;
        private TextView btnEdit;
        private TextView btnSign;
        private TextView btnWeituo;
        private RelativeLayout rlAction;
        private ImageView ivRed;


        public ReleaseEndHolder(View view) {
            super(view);
            headView = (CircleImageView) view.findViewById(R.id.iv_head);
            nameView = (TextView) view.findViewById(R.id.tv_name);
            identiView = (TextView) view.findViewById(R.id.iv_state);
            picView = (ImageView) view.findViewById(R.id.iv_pic);

            nameTitle = (TextView) view.findViewById(R.id.tv_title);
            nameDate = (TextView) view.findViewById(R.id.tv_date);
            nameLocation = (TextView) view.findViewById(R.id.tv_location);
            startimeView = (TextView) view.findViewById(R.id.tv_start_time);

            btnEdit = (TextView) view.findViewById(R.id.btn_edit);
            btnSign = (TextView) view.findViewById(R.id.btn_sign);
            btnWeituo = (TextView) view.findViewById(R.id.btn_weituo);
            rlAction = (RelativeLayout) view.findViewById(R.id.rl_action);
            ivRed = (ImageView) view.findViewById(R.id.iv_red);


        }
    }


    class EnjoyNewHolder extends RecyclerView.ViewHolder {
        private CircleImageView headView;
        private TextView nameView;
        private TextView identiView;
        private ImageView picView;

        private TextView nameTitle;
        private TextView nameDate;
        private TextView nameLocation;
        private TextView startimeView;

        private RelativeLayout weituoLayout;
        private TextView titleView;
        private TextView btn_weituo;

        public EnjoyNewHolder(View view) {
            super(view);
            headView = (CircleImageView) view.findViewById(R.id.iv_head);
            nameView = (TextView) view.findViewById(R.id.tv_name);
            identiView = (TextView) view.findViewById(R.id.iv_state);
            picView = (ImageView) view.findViewById(R.id.iv_pic);

            nameTitle = (TextView) view.findViewById(R.id.tv_title);
            nameDate = (TextView) view.findViewById(R.id.tv_date);
            nameLocation = (TextView) view.findViewById(R.id.tv_location);
            startimeView = (TextView) view.findViewById(R.id.tv_start_time);

            weituoLayout=(RelativeLayout)view.findViewById(R.id.weituo_layout);
            titleView = (TextView) view.findViewById(R.id.btn_edit);
            btn_weituo= (TextView) view.findViewById(R.id.btn_weituo);

        }
    }


    class EnjoyEndHolder extends RecyclerView.ViewHolder {
        private CircleImageView headView;
        private TextView nameView;
        private TextView identiView;
        private ImageView picView;

        private TextView nameTitle;
        private TextView nameDate;
        private TextView nameLocation;
        private TextView startimeView;

        private TextView titleView;
        private TextView btnWeituo;
        private ImageView ivRed;


        public EnjoyEndHolder(View view) {
            super(view);
            headView = (CircleImageView) view.findViewById(R.id.iv_head);
            nameView = (TextView) view.findViewById(R.id.tv_name);
            identiView = (TextView) view.findViewById(R.id.iv_state);
            picView = (ImageView) view.findViewById(R.id.iv_pic);

            nameTitle = (TextView) view.findViewById(R.id.tv_title);
            nameDate = (TextView) view.findViewById(R.id.tv_date);
            nameLocation = (TextView) view.findViewById(R.id.tv_location);
            startimeView = (TextView) view.findViewById(R.id.tv_start_time);

            titleView = (TextView) view.findViewById(R.id.btn_edit);
            btnWeituo = (TextView) view.findViewById(R.id.btn_weituo);
            ivRed = (ImageView) view.findViewById(R.id.iv_red);
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
