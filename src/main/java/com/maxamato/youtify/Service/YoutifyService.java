package com.maxamato.youtify.Service;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.PlaylistListResponse;
import com.maxamato.youtify.Credentials;
import com.maxamato.youtify.connection.SpotifyConnection;
import lombok.AllArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;


import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

import static com.maxamato.youtify.connection.YoutubeConnection.getService;


@Service
@AllArgsConstructor
public class YoutifyService {

    private final SpotifyConnection spotifyConnection;



    public String getPlaylists() throws IOException, ParseException {
        String jsonString = spotifyConnection.getPlaylists();
        JSONObject jsonObject = (JSONObject) new JSONParser().parse(jsonString);
        JSONArray jsonArray = (JSONArray) jsonObject.get("items");
        for (Object o : jsonArray) {
            JSONObject record = (JSONObject) o;
            String name = record.get("name").toString();
            if (name.equals(Credentials.getPlName())) {
                return record.get("tracks").toString();
            }
        }
        return spotifyConnection.getPlaylists();
    }

    public String youtubeAPI()throws GeneralSecurityException, IOException, GoogleJsonResponseException {
        YouTube youtubeService = getService();
        // Define and execute the API request
        YouTube.Playlists.List request = youtubeService.playlists()
                .list(Collections.singletonList("snippet,contentDetails"));
        PlaylistListResponse response = request.setMaxResults(25L)
                .setMine(true)
                .execute();
        return response.toString();
    }





}
