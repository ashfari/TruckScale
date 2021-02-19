/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Sahab
 */
public class ApiManager {
    
    int status = 0;
    Reader in = null;
    BufferedReader br = null;
    StringBuilder sb = null;
    String response = null;
    
    public String apiPost(String api, Map params, String token) throws MalformedURLException, UnsupportedEncodingException, IOException, JSONException {
        URL url = new URL(api);
        StringBuilder postData = new StringBuilder();
        for (Iterator it = params.entrySet().iterator(); it.hasNext();) {
            Map.Entry param = (Map.Entry) it.next();
            if (postData.length() != 0) {
                postData.append('&');
            }
            postData.append(URLEncoder.encode((String) param.getKey(), "UTF-8"));
            postData.append('=');
            postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
        }
        byte[] postDataBytes = postData.toString().getBytes("UTF-8");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        if (token != null) {
            conn.setRequestProperty("Authorization", "Bearer " + token);
        }
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
        conn.setDoOutput(true);
        conn.getOutputStream().write(postDataBytes);
        status = conn.getResponseCode();
        
        if (status == 422) {
            br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            sb = new StringBuilder();
            String output;
            while ((output = br.readLine()) != null) {
              sb.append(output);
            }
//            return "abc";
            System.out.println(sb.toString());
            return "adsawdsads";
        } else {
            br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line+"\n");
            }
            br.close();
            return sb.toString();
        }
//
//        Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
//        StringBuilder sb = new StringBuilder();
//        for (int c; (c = in.read()) >= 0;) {
//            sb.append((char) c);
//        }
//        String response = sb.toString();
//        
//        return response;
//        JSONObject myResponse = new JSONObject(response.toString());
//        JSONObject form_data = myResponse.getJSONObject("form");
    }
}
