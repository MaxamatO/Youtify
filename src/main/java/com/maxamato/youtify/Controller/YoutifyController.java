package com.maxamato.youtify.Controller;

import com.maxamato.youtify.Service.YoutifyService;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.security.GeneralSecurityException;

@RestController
public class YoutifyController {
    @Autowired
    private final YoutifyService youtifyService;

    public YoutifyController(YoutifyService youtifyService) {
        this.youtifyService = youtifyService;
    }

    @GetMapping("/playlists")
    public String getPlaylists() throws IOException, ParseException, GeneralSecurityException {
        return youtifyService.youtubeAPI();
    }

}
