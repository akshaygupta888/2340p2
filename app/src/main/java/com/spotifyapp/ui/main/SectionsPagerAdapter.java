package com.spotifyapp.ui.main;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.spotifyapp.R;
import com.spotifyapp.SpotifyAPI;
import com.spotifyapp.wrapped_fragments.WrappedArtists;
import com.spotifyapp.wrapped_fragments.WrappedGenres;
import com.spotifyapp.wrapped_fragments.WrappedSongs;

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_text_1, R.string.tab_text_2, R.string.tab_text_3};
    private final Context mContext;
    private final SpotifyAPI spotifyAPI;

    public SectionsPagerAdapter(Context context, FragmentManager fragmentManager, SpotifyAPI spotifyAPI) {
        super(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        mContext = context;
        this.spotifyAPI = spotifyAPI;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return WrappedArtists.newInstance(spotifyAPI);
        } else if (position == 1) {
            return WrappedSongs.newInstance(spotifyAPI);
        } else if (position == 2) {
            return WrappedGenres.newInstance(spotifyAPI);
        }

        return WrappedArtists.newInstance(spotifyAPI);
    }

    @Override
    public int getCount() {
        return TAB_TITLES.length;
    }

    @Nullable
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }
}