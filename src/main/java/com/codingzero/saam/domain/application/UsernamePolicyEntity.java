package com.codingzero.saam.domain.application;

import com.codingzero.saam.common.Errors;
import com.codingzero.saam.common.UsernameFormat;
import com.codingzero.saam.domain.Application;
import com.codingzero.saam.domain.UsernamePolicy;
import com.codingzero.saam.infrastructure.data.UsernamePolicyOS;
import com.codingzero.utilities.error.BusinessError;

import java.util.regex.Pattern;

public class UsernamePolicyEntity
        extends IdentifierPolicyEntity<UsernamePolicyOS> implements UsernamePolicy {

    private static final String URL_SAFE_PATTERN_STRING = "^[a-zA-Z0-9]+[a-zA-Z0-9 _.-]+$";
    private static final Pattern URL_SAFE_PATTERN = Pattern.compile(URL_SAFE_PATTERN_STRING);

    public UsernamePolicyEntity(UsernamePolicyOS objectSegment,
                                Application application) {
        super(objectSegment, application);
    }

    @Override
    public UsernameFormat getFormat() {
        return getObjectSegment().getFormat();
    }

    @Override
    public void setVerificationRequired(boolean isVerificationRequired) {
        throw new UnsupportedOperationException("setVerificationRequired");
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
        if (!URL_SAFE_PATTERN.matcher(identifier).matches()) {
            throw BusinessError.raise(Errors.ILLEGAL_IDENTIFIER_FORMAT)
                    .message("May only contain digits (0-9), alphabets (a-z), "
                            + "dash (-), dot (.), space and underscore (_).")
                    .details("applicationId", getApplication().getId())
                    .details("identifier", identifier)
                    .build();
        }
    }
}
