package com.maxamato.youtify.Service;

import com.maxamato.youtify.Credentials;
import com.maxamato.youtify.connection.YoutifyConnection;
import lombok.AllArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

import java.io.IOException;


@Service
@AllArgsConstructor
public class YoutifyService {

    private final YoutifyConnection youtifyConnection;

    public String getPlaylists() throws IOException, ParseException {
        String jsonString = youtifyConnection.getPlaylists();
        JSONObject jsonObject = (JSONObject) new JSONParser().parse(jsonString);
        JSONArray jsonArray = (JSONArray) jsonObject.get("items");
        for (Object o : jsonArray) {
            JSONObject record = (JSONObject) o;
            String name = record.get("name").toString();
            if (name.equals(Credentials.getPlName())) {
                return record.get("tracks").toString();
            }
        }


        return youtifyConnection.getPlaylists();
    }





}
