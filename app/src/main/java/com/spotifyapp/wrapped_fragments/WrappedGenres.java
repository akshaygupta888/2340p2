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

public class WrappedGenres extends Fragment {

    private static final String ARG_SECTION_NUMBER = "1";
    private RecyclerView recyclerView;
    private PageViewModel pageViewModel;
    private SpotifyAPI spotifyAPI;

    public WrappedGenres() {}

    @SuppressWarnings("unused")
    public static WrappedGenres newInstance(SpotifyAPI spotifyAPI) {
        WrappedGenres fragment = new WrappedGenres();
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
        View view = inflater.inflate(R.layout.fragment_wrapped_genres_list, container, false);

        Log.d("Wrapped Genres", "onCreateView called");

        recyclerView = view.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        GenresViewAdapter adapter = new GenresViewAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        if (spotifyAPI != null && spotifyAPI.getTopGenres() != null) {
            List<String> topGenres = spotifyAPI.getTopGenres();
            Log.d("WrappedGenresFragment", "Top Genres: " + topGenres);
            updateAdapter(topGenres);
        } else {
            Log.d("WrappedGenresFragment", "Spotify API or top genres list is null");
        }

        return view;
    }


    private void updateAdapter(List<String> topGenres) {
        GenresViewAdapter adapter = (GenresViewAdapter) recyclerView.getAdapter();
        if (adapter != null) {
            adapter.updateData(topGenres);
        }
    }
}