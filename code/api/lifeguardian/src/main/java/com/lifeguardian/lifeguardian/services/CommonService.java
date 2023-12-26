package com.lifeguardian.lifeguardian.services;

import jakarta.json.JsonObject;

import java.util.Map;

public interface CommonService {

    Map<String, String> getCurrentUser(String token);

    JsonObject stringToJson(String dataString);
}
