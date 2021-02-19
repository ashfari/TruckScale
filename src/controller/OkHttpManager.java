/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.util.Iterator;
import java.util.Map;
import okhttp3.*;

/**
 *
 * @author Sahab
 */
public class OkHttpManager {
    
    // one instance, reuse
    private final OkHttpClient httpClient = new OkHttpClient();
    String value = "";
    
    public String sendPost(String api, Map params, String token) throws Exception {
        // form parameters
        FormBody.Builder formBuilder = new FormBody.Builder();
        
        for (Iterator it = params.entrySet().iterator(); it.hasNext();) {
            Map.Entry param = (Map.Entry) it.next();
            formBuilder.add(param.getKey().toString(), param.getValue().toString());
        }
        
        RequestBody formBody = formBuilder.build();

        Request.Builder requestBuilder = new Request.Builder()
                .url(api)
                .addHeader("User-Agent", "OkHttp Bot")
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .post(formBody);
        
        if (token != null) {
            requestBuilder.addHeader("Authorization", "Bearer " + token);
        }
        
        Request request = requestBuilder.build();

        try (Response response = httpClient.newCall(request).execute()) {
            value = response.body().string();
        }
        
        return value;
    }
}
