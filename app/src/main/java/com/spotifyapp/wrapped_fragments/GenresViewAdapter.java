package com.spotifyapp.wrapped_fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.spotifyapp.R;

import java.util.List;

public class GenresViewAdapter extends RecyclerView.Adapter<GenresViewAdapter.ViewHolder> {

    private final List<String> mGenres;

    public GenresViewAdapter(List<String> genres) {
        mGenres = genres;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_wrapped_genres, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mGenres = mGenres.get(position);
        holder.mIdView.setText(String.valueOf(position + 1));
        holder.mContentView.setText(mGenres.get(position));
    }

    @Override
    public int getItemCount() {
        return mGenres.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mIdView;
        public final TextView mContentView;
        public String mGenres;

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

    public void updateData(List<String> genres) {
        mGenres.clear();
        mGenres.addAll(genres);
        notifyDataSetChanged();
    }

}

