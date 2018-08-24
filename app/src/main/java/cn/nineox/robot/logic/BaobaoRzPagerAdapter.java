/*
 *
 *  MIT License
 *
 *  Copyright (c) 2017 Alibaba Group
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 *
 */

package cn.nineox.robot.logic;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;

import cn.nineox.robot.ui.fragment.BaobaoRzFragment;

/**
 * Created by mikeafc on 15/11/26.
 */
public class BaobaoRzPagerAdapter extends FragmentPagerAdapter {


    private AppCompatActivity mActivity;

    public BaobaoRzPagerAdapter(AppCompatActivity activity){
        super(activity.getSupportFragmentManager());
        this.mActivity = activity;
    }


    @Override
    public BaobaoRzFragment getItem(int position) {
        BaobaoRzFragment fragment = new BaobaoRzFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("position",position);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
