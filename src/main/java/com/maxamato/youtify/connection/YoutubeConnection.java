package com.maxamato.youtify.connection;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collection;
import java.util.Collections;

public class YoutubeConnection {

    // You need to add "client_secret":"<YOUR_CLIENT_SECRET>" while getting 400 bad request - missing client secret error
    private static final String CLIENT_SECRETS= "/client_secret.json";
    private static final Collection<String> SCOPES =
            Collections.singletonList("https://www.googleapis.com/auth/youtube.readonly");
    private static final String APPLICATION_NAME = "Youtify";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();


    public static Credential authorize(final NetHttpTransport httpTransport) throws IOException {
        // Load client secrets.
        InputStream in = YoutubeConnection.class.getResourceAsStream(CLIENT_SECRETS);
        assert in != null;
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY, clientSecrets, SCOPES)
                        // Set remove setAccessType, if you want it to be online. Offline means refresh_token working
                        // *Ignore if using OAuth2*
                        .build();
        // Needed to set up another port for Jetty, since it had conflicts with Tomcat. - Recommended
        LocalServerReceiver localServerReceiver = new LocalServerReceiver.Builder().setHost("localhost").setPort(8081).build();
        return new AuthorizationCodeInstalledApp(flow, localServerReceiver).authorize("user");
    }

    public static YouTube getService() throws GeneralSecurityException, IOException{
        final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        Credential credential = authorize(httpTransport);
        return new YouTube.Builder(httpTransport, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }
}
