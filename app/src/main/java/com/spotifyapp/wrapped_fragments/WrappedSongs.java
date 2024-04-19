package com.spotifyapp.wrapped_fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.spotifyapp.R;
import com.spotifyapp.SpotifyAPI;
import com.spotifyapp.ui.main.PageViewModel;

import java.util.ArrayList;
import java.util.List;

public class WrappedSongs extends Fragment {

    private static final String ARG_SECTION_NUMBER = "2";
    private RecyclerView recyclerView;
    private SpotifyAPI spotifyAPI;

    public WrappedSongs() {}

    @SuppressWarnings("unused")
    public static WrappedSongs newInstance(SpotifyAPI spotifyAPI) {
        WrappedSongs fragment = new WrappedSongs();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, 2);
        fragment.setSpotifyAPI(spotifyAPI);
        fragment.setArguments(bundle);
        return fragment;
    }

    public void setSpotifyAPI(SpotifyAPI spotifyAPI) {
        this.spotifyAPI = spotifyAPI;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PageViewModel pageViewModel = new ViewModelProvider(this).get(PageViewModel.class);
        int index = 2;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        pageViewModel.setIndex(index);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wrapped_songs_list, container, false);

        Log.d("Wrapped Songs", "onCreateView called");

        recyclerView = view.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        SongsViewAdapter adapter = new SongsViewAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        if (spotifyAPI != null && spotifyAPI.getTopSongs() != null) {
            List<SpotifyAPI.TopTrack> topSongs = spotifyAPI.getTopSongs();
            Log.d("WrappedSongsFragment", "Top Songs: " + topSongs);
            updateAdapter(topSongs);
        } else {
            Log.d("WrappedSongsFragment", "Spotify API or top songs list is null");
        }

        return view;
    }


    private void updateAdapter(List<SpotifyAPI.TopTrack> topSongs) {
        SongsViewAdapter adapter = (SongsViewAdapter) recyclerView.getAdapter();
        if (adapter != null) {
            adapter.updateData(topSongs);
        }
    }
}