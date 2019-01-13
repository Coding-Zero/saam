package com.codingzero.saam.core.application;

import com.codingzero.saam.common.Errors;
import com.codingzero.saam.core.Application;
import com.codingzero.saam.core.EmailPolicy;
import com.codingzero.saam.core.identifier.IdentifierFactoryService;
import com.codingzero.saam.core.identifier.IdentifierRepositoryService;
import com.codingzero.saam.infrastructure.database.EmailPolicyOS;
import com.codingzero.utilities.error.BusinessError;

import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

public class EmailPolicyEntity
        extends IdentifierPolicyEntity<EmailPolicyOS> implements EmailPolicy {

    private static final String EMAIL_PATTERN_STRING = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_PATTERN_STRING);

    private EmailPolicyFactoryService factory;

    public EmailPolicyEntity(EmailPolicyOS objectSegment,
                             Application application,
                             EmailPolicyFactoryService factory,
                             IdentifierFactoryService identifierFactory,
                             IdentifierRepositoryService identifierRepository) {
        super(objectSegment, application, identifierFactory, identifierRepository);
        this.factory = factory;
    }

    @Override
    public List<String> getDomains() {
        return getObjectSegment().getDomains();
    }

    @Override
    public void setDomains(List<String> domains) {
        domains = factory.deduplicateDomains(domains);
        factory.checkForDomainFormat(domains);
        if (getDomains().equals(domains)) {
            return;
        }
        getObjectSegment().setDomains(domains);
        getObjectSegment().setUpdateTime(new Date(System.currentTimeMillis()));
        markAsDirty();
    }

    @Override
    public void setMinLength(int length) {
        throw new UnsupportedOperationException("setMinLength");
    }

    @Override
    public void setMaxLength(int length) {
        throw new UnsupportedOperationException("setMaxLength");
    }

    @Override
    public void check(String identifier) {
        super.check(identifier);
        if (!EMAIL_PATTERN.matcher(identifier).matches()) {
            throw BusinessError.raise(Errors.ILLEGAL_IDENTIFIER_FORMAT)
                    .message("Try valid email domain, for example: foo.com")
                    .build();
        }
        String domain = identifier.split("@")[1].toLowerCase();
        if (getDomains().size() == 0) {
            return;
        }
        for (String allowedDomain: getDomains()) {
            if (allowedDomain.equalsIgnoreCase(domain)) {
                return;
            }
        }
        throw BusinessError.raise(Errors.ILLEGAL_IDENTIFIER_FORMAT)
                .message("Email domain is not allowed.")
                .details("domains", getDomains())
                .build();
    }
}
