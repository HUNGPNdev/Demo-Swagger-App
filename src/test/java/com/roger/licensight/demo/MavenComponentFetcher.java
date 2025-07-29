package com.roger.licensight.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;

@SpringBootTest
public class MavenComponentFetcher {
    private static final String API_URL = "https://search.maven.org/solrsearch/select?q=*:*&rows=100&wt=json&start=";
    private static final int MAX_CONSECUTIVE_FAILURES = 10;

    @Test
    void ReadNexusMavenRepositoryIndex() {
        int start = 0;
        int failureCount = 0;

        while (true) {
            try {
                // Kết nối API
                HttpURLConnection conn = (HttpURLConnection) new URL(API_URL + start).openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);

                int status = conn.getResponseCode();

                if (status >= 200 && status < 300) {
                    // Reset lỗi
                    failureCount = 0;

                    // Đọc toàn bộ response
                    InputStream inputStream = conn.getInputStream();
                    String jsonText = new String(inputStream.readAllBytes());
                    inputStream.close();

                    // Parse JSON
                    JSONObject root = new JSONObject(jsonText);
                    JSONObject response = root.getJSONObject("response");
                    JSONArray docs = response.getJSONArray("docs");

                    if (docs.length() == 0) {
                        System.out.println("Không còn artifact nào. Dừng.");
                        break;
                    }

                    // In từng document
                    for (int i = 0; i < docs.length(); i++) {
                        try {
                            JSONObject doc = docs.getJSONObject(i);
                            String id = doc.optString("id", "");
                            String groupId = doc.optString("g", "");
                            String artifactId = doc.optString("a", "");

                            String cpn = "" + id + ";"+groupId+";"+artifactId;
                            System.out.println(cpn);
                            DemoApplicationTests.logToFile(cpn);
                            System.out.println("-----------------------------");
                        } catch (Exception e) {
                            System.err.println("Exception JSONObject doc " + start + ": " + e.getMessage());
                        }
                    }

                } else {
                    System.err.println("HTTP error " + status + " at start=" + start);
                    failureCount++;
                    if (failureCount > MAX_CONSECUTIVE_FAILURES) {
                        System.err.println("Quá 5 lần lỗi liên tiếp. Dừng.");
                        break;
                    }
                    Thread.sleep(1000);
                }
                start++; // Tăng chỉ số
            } catch (Exception e) {
                System.err.println("Exception at start=" + start + ": " + e.getMessage());
                failureCount++;
                if (failureCount > MAX_CONSECUTIVE_FAILURES) {
                    System.err.println("Quá 5 lần lỗi liên tiếp. Dừng.");
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }


    public static void main(String[] args) {
        int start = 0;
        int failureCount = 0;


    }
}
