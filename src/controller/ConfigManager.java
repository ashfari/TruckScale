/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Sahab
 */
public class ConfigManager {

    public JSONObject getConfig() throws JSONException, FileNotFoundException {
        String path = "./config";
        JSONObject config = new JSONObject();
        File file = new File(path);
        if (!file.exists()) {
            config = createConfig();
        } else {
            config = readConfig();
        }
        return config;
    }

    public JSONObject readConfig() throws JSONException, FileNotFoundException {
        String data = "";
        File file = new File("./config");
        Scanner scanner = new Scanner(file);
        while (scanner.hasNextLine()) {
            data += scanner.nextLine();
        }
        scanner.close();
        JSONObject config = new JSONObject(data);
        return config;
    }

    public JSONObject createConfig() throws JSONException {
//        Config JSON
        JSONObject config = new JSONObject();
        config.put("title", "Truck Scale");
        config.put("comPort", "0");
        config.put("kodeTimbangan", "1");
        config.put("trackName", "weigh-in");
        config.put("accessToken", "");
        config.put("apiTimbangan", "http://localhost:8000/api/v1/weigh-bridge/scans");
        config.put("apiQrCode", "http://localhost:8000/api/customer/v1/orders/scan");
        config.put("apiUpdateTrack", "http://localhost:8000/api/customer/v1/orders/tracks");
        config.put("minLengthQrCode", "100");
        config.put("minStepWeight", "50");
        config.put("refreshRate", "1");
        
//        Write file
        try (FileWriter file = new FileWriter("./config")) {
            file.write(config.toString());
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return config;
    }

    public JSONObject updateConfig(JSONObject config) {
//        Write file
        try (FileWriter file = new FileWriter("./config")) {
            file.write(config.toString());
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return config;
    }
}
