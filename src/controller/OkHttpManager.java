/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.util.Iterator;
import java.util.stream.Collectors;
import okhttp3.*;
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
        
        System.out.println("token : " + token);
        
        if (token != null) {
            requestBuilder.addHeader("Authorization", "Bearer " + token);
        }
        
        requestBuilder.post(formBody);
        
        Request request = requestBuilder.build();
        
        return httpClient.newCall(request).execute();
    }
    
    public Response sendPostTokenRequest(String api, JSONObject json) throws Exception {
        FormBody.Builder formBuilder = new FormBody.Builder();
        
        Iterator<String> keys = json.keys();

        while(keys.hasNext()) {
            String key = keys.next();
            formBuilder.add(key, json.getString(key));
        }
        
        RequestBody formBody = formBuilder.build();
        
        Request.Builder requestBuilder = new Request.Builder()
                .url(api)
                .addHeader("User-Agent", "OkHttp Bot")
                .addHeader("Content-Type", "application/x-www-form-urlencoded");
        
        requestBuilder.post(formBody);
        
        Request request = requestBuilder.build();
        
        return httpClient.newCall(request).execute();
    }
    
    public Response sendGetAccountInfo(String api, String token) throws Exception {
        Request.Builder requestBuilder = new Request.Builder()
                .url(api)
                .addHeader("User-Agent", "OkHttp Bot")
                .addHeader("Accept", "application/json");
        
        if (token != null) {
            requestBuilder.addHeader("Authorization", "Bearer " + token);
        }
        
        Request request = requestBuilder.build();
        
        return httpClient.newCall(request).execute();
    }
}
