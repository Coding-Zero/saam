package com.codingzero.saam.domain.application;

import com.codingzero.saam.common.Errors;
import com.codingzero.saam.common.IdentifierType;
import com.codingzero.saam.domain.Application;
import com.codingzero.saam.domain.services.IdentifierPolicyHelper;
import com.codingzero.saam.infrastructure.data.EmailPolicyOS;
import com.codingzero.utilities.error.BusinessError;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class EmailPolicyFactoryService {

    public static final int MIN_LENGTH = 5;
    public static final int MAX_LENGTH = 255;
    private static final Pattern DOMAIN_PATTERN = Pattern.compile("^[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$");

    private IdentifierPolicyHelper identifierPolicyHelper;

    public EmailPolicyFactoryService(IdentifierPolicyHelper identifierPolicyHelper) {
        this.identifierPolicyHelper = identifierPolicyHelper;
    }

    public EmailPolicyEntity generate(
            Application application, boolean isVerificationRequired, List<String> domains) {
        identifierPolicyHelper.checkForDuplicateType(application, IdentifierType.EMAIL);
        deduplicateDomains(domains);
        checkForDomainFormat(domains);
        Date currentDateTime = new Date(System.currentTimeMillis());
        EmailPolicyOS os = new EmailPolicyOS(
                application.getId(),
                isVerificationRequired,
                MIN_LENGTH,
                MAX_LENGTH,
                true,
                currentDateTime,
                currentDateTime,
                domains);
        EmailPolicyEntity entity = reconstitute(os, application);
        entity.markAsNew();
        return entity;
    }

    public List<String> deduplicateDomains(List<String> domains) {
        Set<String> filter = new HashSet<>();
        List<String> filteredDomains = new ArrayList<>(domains.size());
        for (String domain: domains) {
            String newDomain = domain.trim().toLowerCase();
            if (!filter.contains(newDomain)) {
                filteredDomains.add(domain.trim());
                filter.add(newDomain);
            }
        }
        return filteredDomains;
    }

    public void checkForDomainFormat(List<String> domains) {
        for (String domain: domains) {
            if (!DOMAIN_PATTERN.matcher(domain).matches()) {
                throw BusinessError.raise(Errors.ILLEGAL_DOMAIN_NAME_FORMAT)
                        .message("Try valid email domain, for example: foo.com")
                        .build();
            }
        }
    }

    public EmailPolicyEntity reconstitute(EmailPolicyOS os, Application application) {
        if (null == os) {
            return null;
        }
        return new EmailPolicyEntity(os, application, this);
    }

}