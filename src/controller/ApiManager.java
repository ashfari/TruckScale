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

/**
 *
 * @author Sahab
 */
public class ApiManager {
    
    public String apiPost(String api, Map params) throws MalformedURLException, UnsupportedEncodingException, IOException {
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
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
        conn.setDoOutput(true);
        conn.getOutputStream().write(postDataBytes);

        Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
        StringBuilder sb = new StringBuilder();
        for (int c; (c = in.read()) >= 0;) {
            sb.append((char) c);
        }
        String response = sb.toString();
        
        return response;
    }
}
