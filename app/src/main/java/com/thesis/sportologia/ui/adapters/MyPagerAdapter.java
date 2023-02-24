package com.thesis.sportologia.ui.adapters;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.thesis.sportologia.ui.ListEventsFragment;
import com.thesis.sportologia.ui.ListPostsFragment;
import com.thesis.sportologia.ui.ListServicesFragment;
import com.thesis.sportologia.ui.base.CustomPager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vihaan on 1/9/15.
 */
public class MyPagerAdapter extends FragmentPagerAdapter {

    private final List<Fragment> fragments;
    private int mCurrentPosition = -1;

    public MyPagerAdapter(FragmentManager fm) {
        super(fm);
        this.fragments = new ArrayList<Fragment>();
        fragments.add(new ListPostsFragment());
        fragments.add(new ListServicesFragment());
        fragments.add(new ListEventsFragment());
    }

    @Override
    public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        super.setPrimaryItem(container, position, object);
        if (position != mCurrentPosition) {
            Fragment fragment = (Fragment) object;
            CustomPager pager = (CustomPager) container;
            if (fragment != null && fragment.getView() != null) {
                mCurrentPosition = position;
                pager.measureCurrentView(fragment.getView());
            }
        }
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}
