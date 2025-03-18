package base;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;
import org.json.JSONArray;

public class CloudflareBypass {

    private static final String LOG_FILE_PATH = "reports/logs/json_log.log"; // Log file path

    public static Map<String, String> getBypassedData(String url) {
        Map<String, String> data = new HashMap<>();

        try {
            // Ensure logs directory exists
            Files.createDirectories(Paths.get("logs"));

            // Path to Puppeteer script
            String scriptPath = new File("src/test/resources/bypass_cloudflare.js").getAbsolutePath();
            ProcessBuilder builder = new ProcessBuilder("node", scriptPath, url);
            builder.redirectErrorStream(true);

            // Start Puppeteer process
            Process process = builder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            // Read output from Puppeteer
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            process.waitFor();
            String jsonResponse = response.toString().trim();

            // Log output to a file instead of console
            logToFile(jsonResponse);

            // Ensure response is valid JSON
            if (!jsonResponse.startsWith("{")) {
                throw new RuntimeException("Invalid JSON from Puppeteer: " + jsonResponse);
            }

            // Parse JSON response
            JSONObject jsonObject = new JSONObject(jsonResponse);

            // Handle Puppeteer errors gracefully
            if (jsonObject.has("error")) {
                throw new RuntimeException("Puppeteer Error: " + jsonObject.getString("error"));
            }

            // Extract HTML content and cookies
            data.put("content", jsonObject.optString("html", ""));
            JSONArray cookiesArray = jsonObject.optJSONArray("cookies");
            data.put("cookies", (cookiesArray != null) ? cookiesArray.toString() : "[]");

        } catch (Exception e) {
            logToFile("Failed to execute Puppeteer script: " + e.getMessage());
            e.printStackTrace();
        }

        return data;
    }

    // Method to log output to a file
    private static void logToFile(String message) {
        try {
            Files.write(Paths.get(LOG_FILE_PATH),
                    (message + "\n").getBytes(),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
