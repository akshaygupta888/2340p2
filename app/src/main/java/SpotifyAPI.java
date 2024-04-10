import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SpotifyAPI {

    private enum Time {
        SHORT_TERM,
        MEDIUM_TERM,
        LONG_TERM
    }
    private final String accessToken;
    private final OkHttpClient client;

    private String timeRange;
    private final static String limit = "10";
    private final static String artistsEndpoint = "https://api.spotify.com/v1/me/top/artists";
    private final static String songsEndpoint = "https://api.spotify.com/v1/me/top/tracks";
    public List<String> topArtists;
    public List<String> topSongs;
    public List<String> topGenres;

    public SpotifyAPI(String accessToken) {
        this.client = new OkHttpClient();
        this.accessToken = accessToken;
        this.timeRange = "long_term";
    }

    private void updateData() {
        try {
            topArtists = topArtists();
            topSongs = topSongs();
            topGenres = topGenres();
        } catch (IOException e) {
            System.out.println("Network failure");
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

    private List<String> topArtists() throws IOException {
        List<String> topArtists = new ArrayList<>();


        Request request = new Request.Builder()
                .url(urlBuilder(artistsEndpoint))
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            assert response.body() != null;
            JSONObject jsonObject = new JSONObject(response.body().string());
            JSONArray items = jsonObject.getJSONArray("items");

            for (int i = 0; i < items.length(); i++) {
                JSONObject artistObject = items.getJSONObject(i);
                String name = artistObject.getString("name");
                topArtists.add(name);
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return topArtists;
    }

    private List<String> topSongs() {
        List<String> topSongs = new ArrayList<>();


        Request request = new Request.Builder()
                .url(urlBuilder(songsEndpoint))
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            assert response.body() != null;
            JSONObject jsonObject = new JSONObject(response.body().string());
            JSONArray items = jsonObject.getJSONArray("items");

            for (int i = 0; i < items.length(); i++) {
                JSONObject artistObject = items.getJSONObject(i);
                String name = artistObject.getString("name");
                topSongs.add(name);
            }
        } catch (JSONException | IOException e) {
            throw new RuntimeException(e);
        }

        return topSongs;
    }

    private List<String> topGenres() {
        List<String> topGenres = new ArrayList<>();
        Map<String, Integer> topGenresMap = new HashMap<>();

        Request request = new Request.Builder()
                .url(urlBuilder(artistsEndpoint, "50"))
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            assert response.body() != null;
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
        } catch (JSONException | IOException e) {
            throw new RuntimeException(e);
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

        return topGenres;
    }

    public void setTimeRange(String timeRange) {
        this.timeRange = timeRange;
        updateData();
    }
}