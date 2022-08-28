package com.maxamato.youtify.Service;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.json.Json;
import com.google.api.client.json.JsonParser;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.*;
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
import java.util.*;

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
            String name = record.get("name").toString().toLowerCase();
            if (name.equals(Credentials.getPlName().toLowerCase())) {
                JSONObject href =  (JSONObject) record.get("tracks");
                return href.get("href").toString();
            }
        }
        throw new IllegalStateException(new Exception(String.format("No \"%s\" playlist found", Credentials.getPlName())));
    }

    /**
     Function searches for tracks in Spotify from your Youtube's playlist based on the popularity index.
     Keep in mind, that the most popular track, may not be the one you are looking for.
     Spotify's API search item function, does not work perfectly.
    **/
    public String searchForTrackBasedOnPopularity() throws IOException, ParseException, GeneralSecurityException {
        List<String> ytTracks = youtubeAPI();
        String jsonString = spotifyConnection.searchForTrackBasedOnPopularity(ytTracks.get(0)
                .replace("- ", "").replace(" ", "%2B"));
        JSONObject jsonObject = (JSONObject) new JSONParser().parse(jsonString);
        JSONObject tracks = (JSONObject) jsonObject.get("tracks");
        JSONArray items = (JSONArray) tracks.get("items");
        List<Long> popularityValues = new ArrayList<>();
        for(Object o:items){
            JSONObject toCompare = (JSONObject) o;
            popularityValues.add((Long) toCompare.get("popularity"));
        }
        int indexOfTheMostPopular = popularityValues.indexOf(Collections.max(popularityValues));
        System.out.println(items.get(indexOfTheMostPopular));
        return "works?";
    }


    public List<String> youtubeAPI()throws GeneralSecurityException, IOException, GoogleJsonResponseException {
        YouTube youtubeService = getService();
        // Define and execute the API request
        YouTube.PlaylistItems.List request = youtubeService.playlistItems()
                .list(Collections.singletonList("snippet,contentDetails"));
        PlaylistItemListResponse response = request.setMaxResults(25L)
                .setPlaylistId(obtainYoutifiesPlaylistId(youtubeService))
                .execute();
        List<PlaylistItem> items = response.getItems();
        List<String> result = new ArrayList<>();
        for(PlaylistItem playlistItem:items){
            result.add(playlistItem.getSnippet().getTitle());
        }
        return result;
    }

    public String youtubeAPIs()throws GeneralSecurityException, IOException, GoogleJsonResponseException {
        YouTube youtubeService = getService();
        // Define and execute the API request
        YouTube.PlaylistItems.List request = youtubeService.playlistItems()
                .list(Collections.singletonList("snippet,contentDetails"));
        PlaylistItemListResponse response = request.setMaxResults(25L)
                .setPlaylistId(obtainYoutifiesPlaylistId(youtubeService))
                .execute();
        List<PlaylistItem> items = response.getItems();
        for(PlaylistItem playlistItem:items){
            System.out.println(playlistItem.toString());
        }
        return response.toString();
    }

    private String obtainYoutifiesPlaylistId(YouTube youtubeService) throws GeneralSecurityException, IOException {
        // Define and execute the API request
        YouTube.Playlists.List request = youtubeService.playlists()
                .list(Collections.singletonList("snippet,contentDetails"));
        PlaylistListResponse response = request.setMaxResults(25L)
                .setMine(true)
                .execute();
        List<Playlist> items = response.getItems();
        String playlistName = Credentials.getPlName().toLowerCase();
        for(Playlist playlist:items){
            if(playlist.getSnippet().getTitle().toLowerCase().equals(playlistName)){
                return playlist.getId();
            }
        }
        throw new IllegalStateException(new Exception(String.format("No \"%s\" playlist found", playlistName)));
    }

    // TODO: Implement logic to search for tracks obtained in YoutubeApi() in Spotify and add them to respective playlist.





}
