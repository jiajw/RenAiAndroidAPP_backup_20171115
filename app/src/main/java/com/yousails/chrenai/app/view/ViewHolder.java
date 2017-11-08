package com.yousails.chrenai.app.view;

import android.view.View;

import java.util.Observable;


public abstract class ViewHolder<Group, Child> extends Observable {
	protected View mRootView;
	protected Group mGroupData;
	protected Child mChildData;
	protected int groupPosition = 0;
	protected int childPosition = 0;
	protected boolean isExpanded = false;
	protected Object datas;

	public ViewHolder(View itemView) {
		mRootView = itemView;
		if (itemView != null) {
			mRootView.setTag(this);
		}
	}

	public View getView() {
		return mRootView;
	}

	public Child getChildData() {
		return mChildData;
	}

	public Group getGroupData() {
		return mGroupData;
	}

	public void setExpanded(boolean isExpanded) {
		this.isExpanded = isExpanded;
	}

	public boolean expanded() {
		return isExpanded;
	}

	public void setDataAtPosition(Group groupData, Child childData) {
		mGroupData = groupData;
		mChildData = childData;
	}

	public void setData(Object data) {
		this.datas = data;
	}

	public Object getData() {
		return datas;
	}

	public void setPosition(int groupPosition, int childPosition) {
		this.childPosition = childPosition;
		this.groupPosition = groupPosition;
	}

	public int getGroupPosition() {
		return groupPosition;
	}

	public int getChildPosition() {
		return childPosition;
	}

	public boolean specialHandling() {
		return false;
	}

	/** 用于释放资源 */
	public void recycle() {

	}

}
