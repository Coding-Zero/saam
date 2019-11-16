package com.codingzero.saam.app.server.infrastructure.oauth;

import com.codingzero.utilities.key.HMACKey;
import com.codingzero.utilities.key.RandomKey;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class OAuthPlatformAgentHelper {

    public Map<String, Object> prepareConfiguration(Map<String, Object> configuration,
                                                    Map<String, Object> parameters) {
        Map<String, Object> newConfiguration = new HashMap<>(configuration);
        if (null != parameters.get("redirect_uri")) {
            newConfiguration.put("callback", parameters.get("redirect_uri"));
        }
        if (null != parameters.get("scope")) {
            newConfiguration.put("scope", parameters.get("scope"));
        }
        return Collections.unmodifiableMap(newConfiguration);
    }

    public Map<String, String> prepareParametersForAuthUrl(Map<String, Object> parameters) {
        Map<String, String> newParameters = new HashMap<>(parameters.size());
        for (Map.Entry<String, Object> entry: parameters.entrySet()) {
            newParameters.put(entry.getKey(), String.valueOf(entry.getValue()));
        }
        if (null == parameters.get("state")) {
            String state = RandomKey.nextUUIDKey()
                    .toRandomHMACKey(HMACKey.Algorithm.SHA1)
                    .toBase64String(true);
            newParameters.put("state", state);
        }
        newParameters.remove("redirect_uri");
        newParameters.remove("scope");
        return Collections.unmodifiableMap(newParameters);
    }
}
