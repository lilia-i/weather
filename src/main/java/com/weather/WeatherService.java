package com.weather;

import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.GZIPInputStream;

public class WeatherService {

    private static final String API_KEY = "d2a494c7b46243feb0b49573187409da"; // 和风天气 API Key
    private static final String BASE_URL = "https://devapi.qweather.com/v7/"; // 和风天气的实时天气 API URL

    public static JSONObject getWeather(String city, String type) throws IOException {
        String urlString = String.format("%s%s/now?location=%s&key=%s", BASE_URL, type, city, API_KEY);
        if (type == "24h") {
            urlString = String.format("%sweather/%s?location=%s&key=%s", BASE_URL, type, city, API_KEY);
        }
        if (type == "city") {
            urlString = String.format("https://geoapi.qweather.com/v2/city/lookup?location=%s&key=%s", city, API_KEY);
        }
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(10000);
        connection.setReadTimeout(10000);

        InputStream inputStream = connection.getInputStream();
        inputStream = new GZIPInputStream(inputStream);
        String response = readStream(inputStream);
        JSONObject jsonResponse = new JSONObject(response);

        return jsonResponse;
    }

    private static String readStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int bytesRead;

        while ((bytesRead = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, bytesRead);
        }

        return byteArrayOutputStream.toString("UTF-8");
    }
}
