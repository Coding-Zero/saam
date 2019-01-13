package com.codingzero.saam.core;

import java.util.Map;

public interface OAuthIdentifierFactory {

    OAuthIdentifier generate(OAuthIdentifierPolicy policy, String content,
                             Map<String, Object> properties, User user);

}
