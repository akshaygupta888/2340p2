package com.spotifyapp.wrapped_fragments;
import com.spotifyapp.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.spotifyapp.databinding.FragmentWrappedArtistsBinding;

import java.util.List;

public class ArtistsViewAdapter extends RecyclerView.Adapter<ArtistsViewAdapter.ViewHolder> {

    private final List<String> mArtists;

    public ArtistsViewAdapter(List<String> artists) {
        mArtists = artists;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_wrapped_artists, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mArtist = mArtists.get(position);
        holder.mIdView.setText(String.valueOf(position + 1));
        holder.mContentView.setText(mArtists.get(position));
    }

    @Override
    public int getItemCount() {
        return mArtists.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mIdView;
        public final TextView mContentView;
        public String mArtist;

        public ViewHolder(View view) {
            super(view);
            mIdView = view.findViewById(R.id.item_number);
            mContentView = view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }

    public void updateData(List<String> artists) {
        mArtists.clear();
        mArtists.addAll(artists);
        notifyDataSetChanged();
    }

}

