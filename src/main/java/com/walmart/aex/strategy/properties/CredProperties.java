package com.walmart.aex.strategy.properties;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Component
@Slf4j
public class CredProperties {

    private static final String LOCAL = "local";
    private static final String MIDAS_AUTH_LOC = "/secrets/midasApi.authorization.txt";
    private static final String TRUST_STORE_LOC = "/etc/secrets/ssl.truststore.password.txt";

    @Value("#{systemProperties['sql.username']}")
    private String localSqlUsername;

    @Value("#{systemProperties['sql.password']}")
    private String localSqlPassword;

    @Value("#{systemProperties['midasApi.authorization']}")
    private String midasAPIAuthorization;

    @Value("${spring.profiles.active:local}")
    private String activeProfile;

    public String fetchMidasApiAuthorization() {
        String midasAuth = null;
        try {
            midasAuth = activeProfile.contains(LOCAL) ? midasAPIAuthorization : new String(Files.readAllBytes(Paths.get("/etc" +
                    MIDAS_AUTH_LOC)));
        } catch (IOException e) {
            log.error("Error reading midas api authorization" + e.getMessage());
        }
        return midasAuth;
    }

    public String fetchSQLServerUserName() throws IOException {
        return activeProfile.contains(LOCAL) ? localSqlUsername : new String(Files.readAllBytes(Paths.get("/etc" +
                "/secrets/sql.username.txt")));
    }

    public String fetchSQLServerPassword() throws IOException {
      return activeProfile.contains(LOCAL) ? localSqlPassword : new String(Files.readAllBytes(Paths.get("/etc" +
                "/secrets/sql.password.txt")));
    }

    public String getSasURL() throws IOException {
        if(activeProfile.contains(LOCAL)){
            return "https://test/name";
        }
        ObjectReader reader = new ObjectMapper().readerFor(new TypeReference<List<String>>() {
        });
        List<String> results = reader.readValue(Files.readString(Paths.get("/etc" +
                "/secrets/azure.blob.txt")));
        return  results.get(0);
    }

    public String getSasToken() throws IOException {
        if(activeProfile.contains(LOCAL)){
            return "token";
        }
        ObjectReader reader = new ObjectMapper().readerFor(new TypeReference<List<String>>() {
        });
        List<String> results = reader.readValue(Files.readString(Paths.get("/etc" +
                "/secrets/azure.blob.txt")));
        return  results.get(1);
    }

    public String getContainerName() throws IOException {
        if(activeProfile.contains(LOCAL)){
            return "empty";
        }
        ObjectReader reader = new ObjectMapper().readerFor(new TypeReference<List<String>>() {
        });
        List<String> results = reader.readValue(Files.readString(Paths.get("/etc" +
                "/secrets/azure.blob.txt")));
        return  results.get(2);
    }
}
