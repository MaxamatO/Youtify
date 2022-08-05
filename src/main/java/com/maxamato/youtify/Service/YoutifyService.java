package com.maxamato.youtify.Service;

import com.fasterxml.jackson.databind.util.JSONPObject;
import com.maxamato.youtify.Credentials;
import okhttp3.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.util.Objects;

@Service
public class YoutifyService {

    private static final String userId = com.maxamato.youtify.Credentials.getUserId();
    private static final URI redirectUri = URI.create("https://example.com/spotify-redirect");
    private static final String plName = com.maxamato.youtify.Credentials.getPlName();
    private static final String refreshToken = Credentials.getRefreshToken();
    private static final String encoded = Credentials.getEncodedIdSecret();


    public String getPlaylists() throws IOException, ParseException {
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
        String accessToken = jsonObject.get("access_token").toString();

        return redirectUri.toString();

    }

}
