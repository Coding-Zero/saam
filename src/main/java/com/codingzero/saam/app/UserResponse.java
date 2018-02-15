package com.codingzero.saam.app;

import com.codingzero.saam.common.IdentifierType;
import com.codingzero.saam.common.OAuthPlatform;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class UserResponse {

    private String applicationId;
    private String id;
    private long creationTime;
    private List<Role> roles;
    private List<Identifier> identifiers;
    private List<OAuthIdentifier> oAuthIdentifiers;
    private boolean isPasswordSet;

    public UserResponse(String applicationId,
                        String id,
                        Date creationTime,
                        List<Role> roles,
                        List<Identifier> identifiers,
                        List<OAuthIdentifier> oAuthIdentifiers,
                        boolean isPasswordSet) {
        this.applicationId = applicationId;
        this.id = id;
        this.creationTime = creationTime.getTime();
        this.roles = Collections.unmodifiableList(roles);
        this.identifiers = Collections.unmodifiableList(identifiers);
        this.oAuthIdentifiers = Collections.unmodifiableList(oAuthIdentifiers);
        this.isPasswordSet = isPasswordSet;
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

    public boolean isPasswordSet() {
        return isPasswordSet;
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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Role role = (Role) o;
            return getCreationTime().equals(role.getCreationTime()) &&
                    Objects.equals(getId(), role.getId()) &&
                    Objects.equals(getName(), role.getName());
        }

        @Override
        public int hashCode() {

            return Objects.hash(getId(), getCreationTime(), getName());
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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Identifier that = (Identifier) o;
            return isVerified() == that.isVerified() &&
                    getCreationTime().equals(that.getCreationTime()) &&
                    getType() == that.getType() &&
                    Objects.equals(getContent(), that.getContent());
        }

        @Override
        public int hashCode() {

            return Objects.hash(getType(), getContent(), isVerified(), getCreationTime());
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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            OAuthIdentifier that = (OAuthIdentifier) o;
            return getCreationTime().equals(that.getCreationTime()) &&
                    getPlatform() == that.getPlatform() &&
                    Objects.equals(getContent(), that.getContent()) &&
                    Objects.equals(getProperties(), that.getProperties());
        }

        @Override
        public int hashCode() {

            return Objects.hash(getPlatform(), getContent(), getProperties(), getCreationTime());
        }
    }
}
