package com.codingzero.saam.app;

import com.codingzero.saam.common.ApplicationStatus;
import com.codingzero.saam.common.IdentifierType;
import com.codingzero.saam.common.PasswordPolicy;
import com.codingzero.saam.common.UsernameFormat;

import java.util.Collections;
import java.util.Date;
import java.util.List;


public class ApplicationResponse {

    private String id;
    private String name;
    private String description;
    private long creationTime;
    private ApplicationStatus status;
    private PasswordPolicy passwordPolicy;
    private List<UsernamePolicy> usernamePolicies;
    private List<EmailPolicy> emailPolicies;

    public ApplicationResponse(String id,
                               String name,
                               String description,
                               Date creationTime,
                               ApplicationStatus status,
                               PasswordPolicy passwordPolicy,
                               List<UsernamePolicy> usernamePolicies,
                               List<EmailPolicy> emailPolicies) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.creationTime = creationTime.getTime();
        this.status = status;
        this.passwordPolicy = passwordPolicy;
        this.usernamePolicies = Collections.unmodifiableList(usernamePolicies);
        this.emailPolicies = Collections.unmodifiableList(emailPolicies);
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

    public List<UsernamePolicy> getUsernamePolicies() {
        return usernamePolicies;
    }

    public List<EmailPolicy> getEmailPolicies() {
        return emailPolicies;
    }

    public static abstract class IdentifierPolicy {

        private String code;
        private IdentifierType type;
        private boolean isVerificationRequired;
        private int minLength;
        private int maxLength;
        private boolean isActive;
        private long creationTime;
        private long updateTime;

        public IdentifierPolicy(String code,
                                IdentifierType type,
                                boolean isVerificationRequired,
                                int minLength,
                                int maxLength,
                                boolean isActive,
                                Date creationTime,
                                Date updateTime) {
            this.code = code;
            this.type = type;
            this.isVerificationRequired = isVerificationRequired;
            this.minLength = minLength;
            this.maxLength = maxLength;
            this.isActive = isActive;
            this.creationTime = creationTime.getTime();
            this.updateTime = updateTime.getTime();
        }

        public String getCode() {
            return code;
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

        public UsernamePolicy(String applicationId,
                              String code,
                              boolean isVerificationRequired,
                              int minLength,
                              int maxLength,
                              boolean isActive,
                              Date creationTime,
                              Date updateTime,
                              UsernameFormat format) {
            super(
                    code,
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

        public EmailPolicy(String applicationId,
                           String code,
                           boolean isVerificationRequired,
                           int minLength,
                           int maxLength,
                           boolean isActive,
                           Date creationTime,
                           Date updateTime,
                           List<String> domains) {
            super(
                    code,
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
}
