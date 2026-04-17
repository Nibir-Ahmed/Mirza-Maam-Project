package com.medimate.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * AIService - Calls Groq API for symptom analysis
 * Migrated from frontend groq.js service
 */
public class AIService {

    private static final String GROQ_API_KEY = System.getenv("GROQ_API_KEY");
    private static final String GROQ_API_URL = "https://api.groq.com/openai/v1/chat/completions";
    private static final String MODEL = "llama-3.3-70b-versatile";

    private final Gson gson = new Gson();

    /**
     * Analyze symptoms using Groq AI
     * @param symptoms user's symptoms text
     * @return AI-generated medical advice in Bangla
     */
    public String analyzeSymptoms(String symptoms) {
        try {
            // Build the prompt (same as web version)
            String prompt = "You are an experienced doctor. Provide medical advice based on the following symptoms.\n\n"
                    + "Symptoms: " + symptoms + "\n\n"
                    + "Please provide your response in the following format:\n"
                    + "1. Possible Condition\n"
                    + "2. First Aid / Immediate Steps\n"
                    + "3. When to See a Doctor\n"
                    + "4. Precautions\n\n"
                    + "(Note: This is only preliminary advice. Always consult a real doctor for formal treatment.)";

            // Build request body
            JsonObject messageObj = new JsonObject();
            messageObj.addProperty("role", "user");
            messageObj.addProperty("content", prompt);

            JsonArray messages = new JsonArray();
            messages.add(messageObj);

            JsonObject requestBody = new JsonObject();
            requestBody.addProperty("model", MODEL);
            requestBody.add("messages", messages);
            requestBody.addProperty("max_tokens", 1024);

            // Make HTTP request to Groq API
            URL url = new URL(GROQ_API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + GROQ_API_KEY);
            conn.setDoOutput(true);

            // Send request body
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = gson.toJson(requestBody).getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // Read response
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                // Parse JSON response
                JsonObject jsonResponse = gson.fromJson(response.toString(), JsonObject.class);
                return jsonResponse.getAsJsonArray("choices")
                        .get(0).getAsJsonObject()
                        .getAsJsonObject("message")
                        .get("content").getAsString();
            } else {
                return "[!] API Error (Code: " + responseCode + "). Please try again.";
            }

        } catch (Exception e) {
            return "[!] Something went wrong: " + e.getMessage() + "\nPlease try again.";
        }
    }
}
