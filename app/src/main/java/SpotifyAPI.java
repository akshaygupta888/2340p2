import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.OkHttpClient;

// TODO: generate algorithm for top genres

// TODO: generate algorithm for total minutes listened maybe???? i don't think its possible to be accurate tbh
// TODO: (don't think its possible to be accurate for this)

// NOTE: time_range=(long_term | medium_term | short_term)
// long_term = all_time, medium_term = 6 months, short_term 1 month
// TODO: generate algorithm for getting 1 week and 1 year durations

// TODO: create TimeDuration class or enum

public class SpotifyAPI {
    private final String accessToken;
    private final OkHttpClient client;
    private final String artistsEndpoint = "https://api.spotify.com/v1/me/top/artists";
    private final String songsEndpoint = "https://api.spotify.com/v1/me/top/tracks";


    public SpotifyAPI(String accessToken) {
        this.client = new OkHttpClient();
        this.accessToken = accessToken;
    }

    public static List<String> topArtists() {
        List<String> topArtists = new ArrayList<>();

        // TODO: generate top 10 artists by time period

        return topArtists;
    }

    public static List<String> topSongs() {
        List<String> topSongs = new ArrayList<>();

        // TODO: generate top 10 songs by time period

        return topSongs;
    }

    public static List<String> topGenres() {
        List<String> topGenres = new ArrayList<>();

        // TODO: generate top 10 genres by time period

        /*
        * Algorithm idea:
        * Take top 100 artists in selected time period
        * Count up the genres listed in a hashmap with count
        * Return top 10
        * */

        return topGenres;
    }

    private enum Time {
        WEEK,
        MONTH,
        YEAR,
        ALL
    }

    private class TimeDuration {
        private Time time;

        public TimeDuration(Time time) {
            this.time = time;
        }

        public String[] getBounds() {
            Date d = new Date();
            return null;
            // TODO: generate a date bound [start_date, end_date] from the given enum and the current day
        }
    }
}