package com.yousails.chrenai.publish.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yousails.chrenai.R;
import com.yousails.chrenai.app.ui.BaseFragment;

/**
 * Created by sym on 17/7/15.
 */

public class RichEditorFragment extends BaseFragment {



    @Override
    public View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_richeditor, container, false);
        return null;
    }

    @Override
    public void initData() {

    }
}
