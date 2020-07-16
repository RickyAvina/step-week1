/**
 * Sample Java code for youtube.captions.download
 * See instructions for running these code samples locally:
 * https://developers.google.com/explorer-help/guides/code_samples#java
 */

package com.google.sps.data; 

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

import com.google.api.services.youtube.YouTube;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collection;

import java.io.Reader;
import java.io.FileInputStream;
import java.io.File;


public class CaptionDownload {
    private static final String CLIENT_SECRETS= "client_secret.json";
    private static final Collection<String> SCOPES =
        Arrays.asList("https://www.googleapis.com/auth/youtube.force-ssl");

    private static final String APPLICATION_NAME = "Caption YT Downloader";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    /**
     * Create an authorized Credential object.
     *
     * @return an authorized Credential object.
     * @throws IOException
     */
    public static Credential authorize(final NetHttpTransport httpTransport) throws IOException {
        // Load client secrets.
        // InputStream in = CaptionDownload.class.getResourceAsStream(CLIENT_SECRETS);
        
        // if (in == null) {
        //     System.out.println("in is NULL");
        // } else {
        //     System.out.println("in aint null");
        // }
        //     //    System.out.println("Working Directory = " + System.getProperty("user.dir"));

        // Reader in = new InputStreamReader(new FileInputStream(CLIENT_SECRETS));

        // GoogleClientSecrets clientSecrets =
        //   GoogleClientSecrets.load(JSON_FACTORY, in);

        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
            new InputStreamReader(new FileInputStream(
            new File("WEB-INF/client_secret.json"))));

        System.out.println("-----------1-----------");

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow =
            new GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY, clientSecrets, SCOPES)
            .build();

        System.out.println("-----------2-----------");
        Credential credential =
            new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
        System.out.println("----------3-------------");
        return credential;
    }

    /**
     * Build and return an authorized API client service.
     *
     * @return an authorized API client service
     * @throws GeneralSecurityException, IOException
     */
    public static YouTube getService() throws GeneralSecurityException, IOException {
        final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
                System.out.println("about to authorize");

        Credential credential = authorize(httpTransport);
        System.out.println("Authorized");
        return new YouTube.Builder(httpTransport, JSON_FACTORY, credential)
            .setApplicationName(APPLICATION_NAME)
            .build();
    }

    /**
     * Call function to create API service object. Define and
     * execute API request. Print API response.
     *
     * @throws GeneralSecurityException, IOException, GoogleJsonResponseException
     */
    public static void runReq()
        throws GeneralSecurityException, IOException, GoogleJsonResponseException {
        System.out.println("runReq()");
        YouTube youtubeService = getService();

        // TODO: Replace "YOUR_FILE" with the location where
        //       the downloaded content should be written.
        OutputStream output = new FileOutputStream("file.txt");

        // Define and execute the API request
        YouTube.Captions.Download request = youtubeService.captions()
            .download("QVKj3LADCnA");
        request.getMediaHttpDownloader();
        request.executeMediaAndDownloadTo(output);
    }

}