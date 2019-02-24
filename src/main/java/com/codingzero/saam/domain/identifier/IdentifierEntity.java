package com.codingzero.saam.domain.identifier;

import com.codingzero.saam.common.Errors;
import com.codingzero.saam.common.IdentifierVerificationCode;
import com.codingzero.saam.domain.Application;
import com.codingzero.saam.domain.ApplicationRepository;
import com.codingzero.saam.domain.Identifier;
import com.codingzero.saam.domain.IdentifierPolicy;
import com.codingzero.saam.domain.User;
import com.codingzero.saam.domain.principal.user.UserRepositoryService;
import com.codingzero.saam.infrastructure.database.IdentifierOS;
import com.codingzero.saam.infrastructure.database.IdentifierVerificationCodeGenerator;
import com.codingzero.utilities.ddd.EntityObject;
import com.codingzero.utilities.error.BusinessError;

import java.util.Date;

public class IdentifierEntity extends EntityObject<IdentifierOS> implements Identifier {

    private Application application;
    private User user;
    private IdentifierVerificationCodeGenerator verificationCodeGenerator;
    private UserRepositoryService userRepository;
    private ApplicationRepository applicationRepository;

    public IdentifierEntity(IdentifierOS objectSegment,
                            Application application,
                            User user,
                            IdentifierVerificationCodeGenerator verificationCodeGenerator,
                            UserRepositoryService userRepository,
                            ApplicationRepository applicationRepository) {
        super(objectSegment);
        this.application = application;
        this.user = user;
        this.verificationCodeGenerator = verificationCodeGenerator;
        this.userRepository = userRepository;
        this.applicationRepository = applicationRepository;
    }

    @Override
    public Application getApplication() {
        if (null == application) {
            application = applicationRepository.findById(getObjectSegment().getKey().getApplicationId());
        }
        return application;
    }

    @Override
    public User getUser() {
        if (null == user) {
            user = userRepository.findById(getApplication(), getObjectSegment().getUserId());
        }
        return user;
    }

    @Override
    public String getContent() {
        return getObjectSegment().getKey().getContent();
    }

    @Override
    public boolean isVerified() {
        return getObjectSegment().isVerified();
    }

    @Override
    public IdentifierVerificationCode generateVerificationCode(long timeout) {
        checkForVerificationIsNotRequired();
        String code = verificationCodeGenerator.generate(getObjectSegment().getType());
        Date expirationTime = new Date(System.currentTimeMillis() + timeout);
        getObjectSegment().setVerificationCode(
                new IdentifierVerificationCode(code, expirationTime));
        getObjectSegment().setVerified(false);
        getObjectSegment().setUpdateTime(new Date(System.currentTimeMillis()));
        markAsDirty();
        return getObjectSegment().getVerificationCode();
    }

    @Override
    public IdentifierPolicy getPolicy() {
        return getApplication().fetchIdentifierPolicy(getObjectSegment().getType());
    }

    private void checkForVerificationIsNotRequired() {
        if (!getPolicy().isVerificationRequired()) {
            throw BusinessError.raise(Errors.INVALID_IDENTIFIER_POLICY)
                    .message("Verification is not required for identifier, " + getPolicy().getType())
                    .details("applicationId", getPolicy().getApplication().getId())
                    .details("identifierType", getPolicy().getType())
                    .details("identifier", getContent())
                    .build();
        }
    }

    @Override
    public IdentifierVerificationCode getVerificationCode() {
        return getObjectSegment().getVerificationCode();
    }

    @Override
    public void verify(String code) {
        IdentifierVerificationCode verificationCode = getObjectSegment().getVerificationCode();
        if (null == verificationCode
                || !verificationCode.getCode().equals(code)) {
            throw BusinessError.raise(Errors.INVALID_IDENTIFIER_VERIFICATION_CODE)
                    .message("Wrong verification code.")
                    .details("code", code)
                    .build();
        }
        if (verificationCode.getExpirationTime().getTime() <= System.currentTimeMillis()) {
            throw BusinessError.raise(Errors.INVALID_IDENTIFIER_VERIFICATION_CODE)
                    .message("Verification code has expired.")
                    .details("code", code)
                    .details("expirationTime", verificationCode.getExpirationTime())
                    .build();
        }
        getObjectSegment().setVerificationCode(null);
        getObjectSegment().setVerified(true);
        getObjectSegment().setUpdateTime(new Date(System.currentTimeMillis()));
        markAsDirty();
    }

    @Override
    public Date getCreationTime() {
        return getObjectSegment().getCreationTime();
    }

    @Override
    public Date getUpdateTime() {
        return getObjectSegment().getUpdateTime();
    }
}
