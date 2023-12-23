package com.lifeguardian.lifeguardian.services;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Base64;

public class CommonServiceImpl implements CommonService {
    @Override
    public Map<String, String> getCurrentUser(String token) {
        // Decode the token
        Map<String, Object> tokenInfo = decodeToken(token);

        // Extract the username and roles
        String username = (String) tokenInfo.get("jti");
        List<String> roles = (List<String>) tokenInfo.get("roles");

        // Prepare the response
        Map<String, String> response = new HashMap<>();
        response.put("username", username);
        response.put("role", roles != null && !roles.isEmpty() ? roles.get(0) : "No role");

        return response;
    }
    private Map<String, Object> decodeToken(String token) {
        // Split the token into parts
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid token");
        }

        // Decode the payload part
        String payload = new String(Base64.getUrlDecoder().decode(parts[1]));

        // Deserialize the JSON payload to a map
        Jsonb jsonb = JsonbBuilder.create();
        return jsonb.fromJson(payload, Map.class);
    }
}
