/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.util.Iterator;
import java.util.Map;
import okhttp3.*;
import okio.ByteString;
import org.json.JSONObject;

/**
 *
 * @author Sahab
 */
public class OkHttpManager {
    
    // one instance, reuse
    private final OkHttpClient httpClient = new OkHttpClient();
    
    public Response sendPost(String api, JSONObject json, String token) throws Exception {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        
        RequestBody formBody = RequestBody.create(JSON, String.valueOf(json));
        
        Request.Builder requestBuilder = new Request.Builder()
                .url(api)
                .addHeader("User-Agent", "OkHttp Bot")
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json");
        
        if (token != null) {
            requestBuilder.addHeader("Authorization", "Bearer " + token);
        }
        
        requestBuilder.post(formBody);
        
        Request request = requestBuilder.build();
        
        return httpClient.newCall(request).execute();
    }
}
