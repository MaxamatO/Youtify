package com.maxamato.youtify.connection;

import com.google.api.client.json.JsonFactory;
import com.maxamato.youtify.Credentials;
import okhttp3.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

@Component
public class SpotifyConnection {

    private static final String userId = com.maxamato.youtify.Credentials.getUserId();
    private static final URI redirectUri = URI.create("https://example.com/spotify-redirect");
    private static final String plName = com.maxamato.youtify.Credentials.getPlName();
    private static final String refreshToken = Credentials.getRefreshToken();
    private static final String encoded = Credentials.getEncodedIdSecret();


    public String getPlaylists() throws IOException, ParseException {
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        Request request = new Request.Builder()
                .url(String.format("https://api.spotify.com/v1/users/%s/playlists", userId))
                .addHeader("Authorization", String.format("Bearer %s", obtainAccessToken()))
                .addHeader("Content-Type", "application/application/json")
                .build();
        Response response = client.newCall(request).execute();
        return Objects.requireNonNull(response.body()).string();
    }

    public String addTracks(String playlistId, String trackId) throws IOException, ParseException {
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder().build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "");
        Request request = new Request.Builder()
                .url(String.format("https://api.spotify.com/v1/playlists/%s/tracks?uris=spotify:track:%s", playlistId, trackId))
                .method("POST", body)
                .addHeader("Authorization", String.format("Bearer %s", obtainAccessToken()))
                .build();
        Response response = okHttpClient.newCall(request).execute();
        return Objects.requireNonNull(response.body()).string();
    }

    public String searchForTrackBasedOnPopularity(String ytTrack) throws IOException, ParseException {
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        Request request = new Request.Builder()
                .url(String.format("https://api.spotify.com/v1/search?q=track%%253A%s&type=track&offset=0", ytTrack))
                .addHeader("Authorization", String.format("Bearer %s", obtainAccessToken()))
                .addHeader("Content-Type", "application/application/json")
                .build();
        Response response = client.newCall(request).execute();
        return Objects.requireNonNull(response.body()).string();
    }

    public String obtainAccessToken() throws IOException, ParseException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody
                .create(mediaType,
                        String.format("grant_type=refresh_token&refresh_token=%s", refreshToken));

        Request request = new Request.Builder()
                .url("https://accounts.spotify.com/api/token")
                .method("POST", body)
                .addHeader("Authorization", String.format("Basic %s", encoded))
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();
        Response response = client.newCall(request).execute();
        String jsonString = Objects.requireNonNull(response.body()).string();
        JSONParser parser = new JSONParser();
        JSONObject jsonObject;
        jsonObject = (JSONObject) parser.parse(jsonString);
        return jsonObject.get("access_token").toString();
    }



}
