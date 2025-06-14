//package com.example.CallApiAngular.Configuration;
//
//import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
//import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
//import com.google.api.client.http.javanet.NetHttpTransport;
//import com.google.api.client.json.JsonFactory;
//import com.google.api.client.util.store.FileDataStoreFactory;
//import com.google.api.client.json.jackson2.JacksonFactory;
//import com.google.api.services.drive.Drive;
//import com.google.api.services.drive.DriveScopes;
//import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
//import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
//import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
//import org.springframework.context.annotation.Configuration;
//
//import java.io.InputStreamReader;
//import java.io.InputStream;
//import java.util.List;
//
//@Configuration
//public class GoogleDriveConfig {
//
//    private static final String APPLICATION_NAME = "My Spring Boot App";
//    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
//    private static final List<String> SCOPES = List.of(DriveScopes.DRIVE_FILE);
//    private static final String TOKENS_DIRECTORY_PATH = "tokens";
//
//    public Drive getDriveService() throws Exception {
//        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
//        InputStream in = getClass().getResourceAsStream("/credentials.json");
//
//        if (in == null) {
//            throw new RuntimeException("credentials.json not found");
//        }
//
//        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
//
//        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
//                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
//                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
//                .setAccessType("offline")
//                .build();
//
//        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
//
//        return new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, new AuthorizationCodeInstalledApp(flow, receiver).authorize("user"))
//                .setApplicationName(APPLICATION_NAME)
//                .build();
//    }
//}
