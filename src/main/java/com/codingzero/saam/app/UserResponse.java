package com.codingzero.saam.app;

import com.codingzero.saam.common.IdentifierType;
import com.codingzero.saam.common.OAuthPlatform;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;


public class UserResponse {

    private String applicationId;
    private String id;
    private long creationTime;
    private List<Role> roles;
    private List<Identifier> identifiers;
    private List<OAuthIdentifier> oAuthIdentifiers;

    public UserResponse(String applicationId, String id, Date creationTime,
                        List<Role> roles, List<Identifier> identifiers, List<OAuthIdentifier> oAuthIdentifiers) {
        this.applicationId = applicationId;
        this.id = id;
        this.creationTime = creationTime.getTime();
        this.roles = Collections.unmodifiableList(roles);
        this.identifiers = Collections.unmodifiableList(identifiers);
        this.oAuthIdentifiers = Collections.unmodifiableList(oAuthIdentifiers);
    }

    public String getApplicationId() {
        return applicationId;
    }

    public String getId() {
        return id;
    }

    public Date getCreationTime() {
        return new Date(creationTime);
    }

    public List<Role> getRoles() {
        return roles;
    }

    public List<Identifier> getIdentifiers() {
        return identifiers;
    }

    public List<OAuthIdentifier> getOAuthIdentifiers() {
        return oAuthIdentifiers;
    }

    public static class Role {

        private String id;
        private long creationTime;
        private String name;

        public Role(String id, Date creationTime, String name) {
            this.id = id;
            this.creationTime = creationTime.getTime();
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public Date getCreationTime() {
            return new Date(creationTime);
        }

        public String getName() {
            return name;
        }
    }

    public static class Identifier {

        private IdentifierType type;
        private String content;
        private boolean isVerified;
        private long creationTime;

        public Identifier(IdentifierType type, String content, boolean isVerified,
                          Date creationTime) {
            this.type = type;
            this.content = content;
            this.isVerified = isVerified;
            this.creationTime = creationTime.getTime();
        }

        public IdentifierType getType() {
            return type;
        }

        public String getContent() {
            return content;
        }

        public boolean isVerified() {
            return isVerified;
        }

        public Date getCreationTime() {
            return new Date(creationTime);
        }

    }

    public static class OAuthIdentifier {

        private OAuthPlatform platform;
        private String content;
        private Map<String, Object> properties;
        private long creationTime;

        public OAuthIdentifier(OAuthPlatform platform, String content,
                               Map<String, Object> properties, Date creationTime) {
            this.platform = platform;
            this.content = content;
            this.properties = Collections.unmodifiableMap(properties);
            this.creationTime = creationTime.getTime();
        }

        public OAuthPlatform getPlatform() {
            return platform;
        }

        public String getContent() {
            return content;
        }

        public Map<String, Object> getProperties() {
            return properties;
        }

        public Date getCreationTime() {
            return new Date(creationTime);
        }
    }
}
