package com.codingzero.saam.app;

import com.codingzero.saam.common.ApplicationStatus;
import com.codingzero.saam.common.IdentifierType;
import com.codingzero.saam.common.OAuthPlatform;
import com.codingzero.saam.common.PasswordPolicy;
import com.codingzero.saam.common.UsernameFormat;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;


public class ApplicationResponse {

    private String id;
    private String name;
    private String description;
    private long creationTime;
    private ApplicationStatus status;
    private PasswordPolicy passwordPolicy;
    private UsernamePolicy usernamePolicy;
    private EmailPolicy emailPolicy;
    private List<OAuthIdentifierPolicy> oAuthIdentifierPolicies;

    public ApplicationResponse(String id,
                               String name,
                               String description,
                               Date creationTime,
                               ApplicationStatus status,
                               PasswordPolicy passwordPolicy,
                               UsernamePolicy usernamePolicy,
                               EmailPolicy emailPolicy,
                               List<OAuthIdentifierPolicy> oAuthIdentifierPolicies) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.creationTime = creationTime.getTime();
        this.status = status;
        this.passwordPolicy = passwordPolicy;
        this.usernamePolicy = usernamePolicy;
        this.emailPolicy = emailPolicy;
        this.oAuthIdentifierPolicies = Collections.unmodifiableList(oAuthIdentifierPolicies);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Date getCreationTime() {
        return new Date(creationTime);
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public PasswordPolicy getPasswordPolicy() {
        return passwordPolicy;
    }

    public UsernamePolicy getUsernamePolicy() {
        return usernamePolicy;
    }

    public EmailPolicy getEmailPolicy() {
        return emailPolicy;
    }

    public List<OAuthIdentifierPolicy> getoAuthIdentifierPolicies() {
        return oAuthIdentifierPolicies;
    }

    public static abstract class IdentifierPolicy {

        private IdentifierType type;
        private boolean isVerificationRequired;
        private int minLength;
        private int maxLength;
        private boolean isActive;
        private long creationTime;
        private long updateTime;

        public IdentifierPolicy(IdentifierType type,
                                boolean isVerificationRequired,
                                int minLength,
                                int maxLength,
                                boolean isActive,
                                Date creationTime,
                                Date updateTime) {
            this.type = type;
            this.isVerificationRequired = isVerificationRequired;
            this.minLength = minLength;
            this.maxLength = maxLength;
            this.isActive = isActive;
            this.creationTime = creationTime.getTime();
            this.updateTime = updateTime.getTime();
        }

        public IdentifierType getType() {
            return type;
        }

        public boolean isVerificationRequired() {
            return isVerificationRequired;
        }

        public int getMinLength() {
            return minLength;
        }

        public int getMaxLength() {
            return maxLength;
        }

        public boolean isActive() {
            return isActive;
        }

        public Date getCreationTime() {
            return new Date(creationTime);
        }

        public Date getUpdateTime() {
            return new Date(updateTime);
        }

    }

    public static class UsernamePolicy extends IdentifierPolicy {

        private UsernameFormat format;

        public UsernamePolicy(boolean isVerificationRequired,
                              int minLength,
                              int maxLength,
                              boolean isActive,
                              Date creationTime,
                              Date updateTime,
                              UsernameFormat format) {
            super(
                    IdentifierType.USERNAME,
                    isVerificationRequired,
                    minLength,
                    maxLength,
                    isActive,
                    creationTime,
                    updateTime);
            this.format = format;
        }

        public UsernameFormat getFormat() {
            return format;
        }
    }

    public static class EmailPolicy extends IdentifierPolicy {

        private List<String> domains;

        public EmailPolicy(boolean isVerificationRequired,
                           int minLength,
                           int maxLength,
                           boolean isActive,
                           Date creationTime,
                           Date updateTime,
                           List<String> domains) {
            super(
                    IdentifierType.EMAIL,
                    isVerificationRequired,
                    minLength,
                    maxLength,
                    isActive,
                    creationTime,
                    updateTime);
            this.domains = Collections.unmodifiableList(domains);
        }

        public List<String> getDomains() {
            return domains;
        }

    }

    public static class OAuthIdentifierPolicy {

        private OAuthPlatform platform;
        private Map<String, Object> configurations;
        private boolean isActive;

        public OAuthIdentifierPolicy(OAuthPlatform platform,
                                     Map<String, Object> configurations,
                                     boolean isActive) {
            this.platform = platform;
            this.configurations = Collections.unmodifiableMap(configurations);
            this.isActive = isActive;
        }

        public OAuthPlatform getPlatform() {
            return platform;
        }

        public Map<String, Object> getConfigurations() {
            return configurations;
        }

        public boolean isActive() {
            return isActive;
        }
    }
}
