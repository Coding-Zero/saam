package com.codingzero.saam.app.server.infrastructure.mysql.commons;

import com.codingzero.utilities.key.HMACKey;
import com.codingzero.utilities.key.Key;

public class ResourceKeyHashUtil {

    public static String hash(String key) {
        if (null == key
                || key.trim().length() == 0) {
            return null;
        }
        return Key.fromString(key.toLowerCase())
                .toHMACKey(HMACKey.Algorithm.SHA256)
                .toHexString();
    }

}
