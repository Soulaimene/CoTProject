package com.lifeguardian.lifeguardian.services;

import java.util.Map;

public interface CommonService {

    Map<String, String> getCurrentUser(String token);
}
