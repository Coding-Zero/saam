package com.codingzero.saam.core;

import com.codingzero.saam.common.IdentifierType;
import com.codingzero.utilities.pagination.PaginatedResult;

import java.util.Date;
import java.util.List;

public interface IdentifierPolicy {

    Application getApplication();

    IdentifierType getType();

    boolean isVerificationRequired();

    void setVerificationRequired(boolean isVerificationRequired);

    int getMinLength();

    void setMinLength(int length);

    int getMaxLength();

    void setMaxLength(int length);

    boolean isActive();

    void setActive(boolean isActive);

    Date getCreationTime();

    Date getUpdateTime();

    void check(String identifier);

    /**Identifier**/

    Identifier addIdentifier(String content, User user);

    void updateIdentifier(Identifier identifier);

    void removeIdentifier(Identifier identifier);

    Identifier fetchIdentifierByContent(String content);

    Identifier fetchIdentifierByUserAndContent(User user, String content);

    List<Identifier> fetchIdentifiersByUser(User user);

    PaginatedResult<List<Identifier>> fetchAllIdentifiers();

}
