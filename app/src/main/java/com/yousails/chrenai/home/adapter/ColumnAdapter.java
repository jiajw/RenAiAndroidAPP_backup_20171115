package com.yousails.chrenai.home.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yousails.chrenai.home.listener.OnItemClickListener;
import com.bumptech.glide.Glide;
import com.yousails.chrenai.R;
import com.yousails.chrenai.home.bean.ActivitiesBean;
import com.yousails.chrenai.utils.StringUtil;
import com.yousails.chrenai.utils.TimeUtil;
import com.yousails.chrenai.view.CircleImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/7/3.
 */

public class ColumnAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    //加载更多item 状态切换
    private int mSwitch = 0;

    public static final int TYPE_HEADER = 0;
    public static final int TYPE_NORMAL = 1;
    public static final int TYPE_FOOTER = 2;
    private Context mContext;
    private List<ActivitiesBean> activitiesBeanList = new ArrayList<ActivitiesBean>();
    private List<String> mDatas;

    //适配器初始化
    public ColumnAdapter(Context context, List<ActivitiesBean> activitiesBeanList) {
        mContext = context;
        this.activitiesBeanList = activitiesBeanList;
    }

    private View mHeaderView;

    private OnItemClickListener mListener;

    public void setOnItemClickListener(OnItemClickListener li) {
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
                View view = LayoutInflater.from(mContext).inflate(R.layout.column_item, parent, false);
                return new CustomHolder(view);
            } else {
                View footerView = LayoutInflater.from(mContext).inflate(R.layout.column_item_footer, parent, false);
                return new FootViewHolder(footerView);
            }

        } else {
            if (viewType == TYPE_HEADER) {
                return new BannderHolder(mHeaderView);
            } else if (viewType == TYPE_NORMAL) {
                View view = LayoutInflater.from(mContext).inflate(R.layout.column_item, parent, false);
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
            ((BannderHolder) viewHolder).bindItem();
        } else if (getItemViewType(position) == TYPE_FOOTER) {
              ((FootViewHolder) viewHolder).footView.setText(R.string.loading);
        } else {
            final int pos = getRealPosition(viewHolder);
            final ActivitiesBean activitiesBean = activitiesBeanList.get(pos);
            final String data = activitiesBean.getUser().getName();
            String headUrl = activitiesBean.getUser().getAvatar();
            boolean isCertificated = activitiesBean.getUser().is_certificated();
            String coverimage = activitiesBean.getCover_image();
            String starTime = activitiesBean.getStarted_at();
            if (viewHolder instanceof CustomHolder) {
                ((CustomHolder) viewHolder).nameView.setText(data);
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
