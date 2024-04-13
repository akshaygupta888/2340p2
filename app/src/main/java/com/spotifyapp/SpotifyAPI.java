package com.spotifyapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SpotifyAPI {

    private final String accessToken;
    private final OkHttpClient client;
    private String timeRange;
    private final static String limit = "10";
    private final static String artistsEndpoint = "https://api.spotify.com/v1/me/top/artists";
    private final static String songsEndpoint = "https://api.spotify.com/v1/me/top/tracks";
    private final WeakReference<Context> contextRef;
    private SpotifyDataListener dataListener;
    private List<String> topArtists;
    private List<String> topSongs;
    private List<String> topGenres;

    public List<String> getTopArtists() {
        return topArtists;
    }

    public List<String> getTopSongs() {
        return topSongs;
    }

    public List<String> getTopGenres() {
        return topGenres;
    }

    public interface SpotifyDataListener {
        void onDataLoaded();
        void onDataLoadError(String errorMessage);
    }

    public SpotifyAPI(String accessToken, Context context, SpotifyDataListener listener) {
        this.accessToken = accessToken;
        this.client = new OkHttpClient();
        this.timeRange = "long_term";
        this.contextRef = new WeakReference<>(context);
        this.dataListener = listener;
        new UpdateDataTask().execute();
    }

    @SuppressLint("StaticFieldLeak")
    private class UpdateDataTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                topArtists = fetchTopArtists();
                topSongs = fetchTopSongs();
                topGenres = fetchTopGenres();
                return true;
            } catch (IOException | JSONException e) {
                Log.e("SpotifyAPI", "Error fetching data", e);
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                // Notify the listener that data loading is complete
                if (dataListener != null) {
                    dataListener.onDataLoaded();
                }
            } else {
                // Notify the listener about the error
                if (dataListener != null) {
                    dataListener.onDataLoadError("Error loading data");
                }
            }
        }
    }

    private String urlBuilder(String endpoint, String limit) {
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(endpoint)).newBuilder();
        urlBuilder.addQueryParameter("time_range", timeRange);
        urlBuilder.addQueryParameter("limit", limit);
        return urlBuilder.build().toString();
    }

    private String urlBuilder(String endpoint) {
        return urlBuilder(endpoint, limit);
    }

    private List<String> fetchTopArtists() throws IOException, JSONException {
        List<String> topArtists = new ArrayList<>();

        Request request = new Request.Builder()
                .url(urlBuilder(artistsEndpoint))
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                Log.d("SpotifyAPI", "Top artists fetched unsuccessfully.");
                throw new IOException("Unexpected code " + response);
            }

            JSONObject jsonObject = new JSONObject(response.body().string());
            JSONArray items = jsonObject.getJSONArray("items");

            for (int i = 0; i < items.length(); i++) {
                JSONObject artistObject = items.getJSONObject(i);
                String name = artistObject.getString("name");
                topArtists.add(name);
            }
        }

        Log.d("SpotifyAPI", "Top artists fetched successfully.");

        return topArtists;
    }

    private List<String> fetchTopSongs() throws IOException, JSONException {
        List<String> topSongs = new ArrayList<>();

        Request request = new Request.Builder()
                .url(urlBuilder(songsEndpoint))
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                Log.d("SpotifyAPI", "Top songs fetched unsuccessfully.");
                throw new IOException("Unexpected code " + response);
            }

            JSONObject jsonObject = new JSONObject(response.body().string());
            JSONArray items = jsonObject.getJSONArray("items");

            for (int i = 0; i < items.length(); i++) {
                JSONObject artistObject = items.getJSONObject(i);
                String name = artistObject.getString("name");
                topSongs.add(name);
            }
        }

        Log.d("SpotifyAPI", "Top songs fetched successfully.");

        return topSongs;
    }

    private List<String> fetchTopGenres() throws IOException, JSONException {
        List<String> topGenres = new ArrayList<>();
        Map<String, Integer> topGenresMap = new HashMap<>();

        Request request = new Request.Builder()
                .url(urlBuilder(artistsEndpoint, "50"))
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                Log.d("SpotifyAPI", "Top genres fetched unsuccessfully.");
                throw new IOException("Unexpected code " + response);
            }

            JSONObject jsonObject = new JSONObject(response.body().string());
            JSONArray items = jsonObject.getJSONArray("items");

            for (int i = 0; i < items.length(); i++) {
                JSONObject artistObject = items.getJSONObject(i);
                JSONArray genres = artistObject.getJSONArray("genres");

                for (int j = 0; j < genres.length(); j++) {
                    String genre = genres.getString(j);
                    topGenresMap.put(genre, topGenresMap.getOrDefault(genre, 0) + 1);
                }
            }
        }

        List<Map.Entry<String, Integer>> sortedList = new ArrayList<>(topGenresMap.entrySet());
        sortedList.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        // Extracting top 10 genres
        int count = 0;
        for (Map.Entry<String, Integer> entry : sortedList) {
            if (count >= 10) break;
            topGenres.add(entry.getKey());
            count++;
        }

        Log.d("SpotifyAPI", "Top genres fetched successfully.");

        return topGenres;
    }

    public void setTimeRange(String timeRange) {
        this.timeRange = timeRange;
        new UpdateDataTask().execute();
    }
}
