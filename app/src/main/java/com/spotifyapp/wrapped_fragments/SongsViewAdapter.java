package com.spotifyapp.wrapped_fragments;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.spotifyapp.R;
import com.spotifyapp.SpotifyAPI;

import java.io.IOException;
import java.util.List;

public class SongsViewAdapter extends RecyclerView.Adapter<SongsViewAdapter.ViewHolder> {

    private List<SpotifyAPI.TopTrack> topSongs;
    private MediaPlayer mediaPlayer;
    private int playingPosition = -1;

    public SongsViewAdapter(List<SpotifyAPI.TopTrack> topSongs) {
        this.topSongs = topSongs;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_wrapped_songs, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        SpotifyAPI.TopTrack topTrack = topSongs.get(position);
        holder.itemNumber.setText(String.valueOf(position + 1));
        holder.content.setText(topTrack.name);

        holder.playPreviewButton.setOnClickListener(v -> {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                if (playingPosition == position) {
                    playingPosition = -1;
                    return;
                }
            }

            playingPosition = position;
            playPreview(topTrack.previewUrl);
        });
    }

    @Override
    public int getItemCount() {
        return topSongs.size();
    }

    public void updateData(List<SpotifyAPI.TopTrack> topSongs) {
        this.topSongs = topSongs;
        notifyDataSetChanged();
    }

    private void playPreview(String previewUrl) {
        if (previewUrl == null || previewUrl.isEmpty()) {
            return;
        }

        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(previewUrl);
            mediaPlayer.prepare();
            mediaPlayer.setOnCompletionListener(mp -> {
                mediaPlayer.release();
                mediaPlayer = null;
                playingPosition = -1;
            });
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemNumber;
        TextView content;
        Button playPreviewButton;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemNumber = itemView.findViewById(R.id.item_number);
            content = itemView.findViewById(R.id.content);
            playPreviewButton = itemView.findViewById(R.id.play_preview_button);
        }
    }
}
