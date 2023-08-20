package com.nads.nadsengine.Services;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

public class NewHttpRequest {
    public String postRequestBasicAuth(String uri, String body, String username, String password) throws Exception {
        URL url = new URL(uri);

        // Create HttpURLConnection object
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        // Set request method to POST
        conn.setRequestMethod("POST");

        // Set headers
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");

        if (username != null && password != null) {
            String authString = username + ":" + password;
            String encodedAuthString = Base64.getEncoder().encodeToString(authString.getBytes());
            conn.setRequestProperty("Authorization", "Basic " + encodedAuthString);
        }

        // Set request body
        String requestBody = body;
        byte[] requestBodyBytes = requestBody.getBytes("UTF-8");
        conn.setDoOutput(true);
        OutputStream outputStream = conn.getOutputStream();
        outputStream.write(requestBodyBytes);
        outputStream.flush();
        outputStream.close();

        // Get response code
        int responseCode = conn.getResponseCode();

        // Get response body
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuilder responseBody = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            responseBody.append(inputLine);
        }
        in.close();

        // Print response
        // System.out.println("Response Code: " + responseCode);
        return responseBody.toString();
    }

}
