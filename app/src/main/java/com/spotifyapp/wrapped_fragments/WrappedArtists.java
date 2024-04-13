package com.spotifyapp.wrapped_fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.spotifyapp.R;
import com.spotifyapp.SpotifyAPI;
import com.spotifyapp.ui.main.PageViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class WrappedArtists extends Fragment {

    private static final String ARG_SECTION_NUMBER = "1";
    private RecyclerView recyclerView;
    private PageViewModel pageViewModel;
    private SpotifyAPI spotifyAPI;

    public WrappedArtists() {}

    @SuppressWarnings("unused")
    public static WrappedArtists newInstance(SpotifyAPI spotifyAPI) {
        WrappedArtists fragment = new WrappedArtists();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, 1);
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
        pageViewModel = new ViewModelProvider(this).get(PageViewModel.class);
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        pageViewModel.setIndex(index);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wrapped_artists_list, container, false);

        Log.d("Wrapped Artists", "onCreateView called");

        recyclerView = view.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        ArtistsViewAdapter adapter = new ArtistsViewAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        if (spotifyAPI != null && spotifyAPI.getTopArtists() != null) {
            List<String> topArtists = spotifyAPI.getTopArtists();
            Log.d("WrappedArtistsFragment", "Top Artists: " + topArtists); // Log top artists
            updateAdapter(topArtists);
        } else {
            Log.d("WrappedArtistsFragment", "Spotify API or top artists list is null");
        }

        return view;
    }


    private void updateAdapter(List<String> topArtists) {
        ArtistsViewAdapter adapter = (ArtistsViewAdapter) recyclerView.getAdapter();
        if (adapter != null) {
            adapter.updateData(topArtists);
        }
    }
}