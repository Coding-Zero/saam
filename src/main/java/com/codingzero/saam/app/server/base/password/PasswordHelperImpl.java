package com.codingzero.saam.app.server.base.password;

import com.codingzero.saam.common.IdentifierType;
import com.codingzero.saam.infrastructure.database.PasswordHelper;
import com.codingzero.utilities.key.HMACKey;
import com.codingzero.utilities.key.RandomKey;


public class PasswordHelperImpl implements PasswordHelper {

    @Override
    public String encodePassword(String password) {
        try {
            return PasswordStorage.createHash(password);
        } catch (PasswordStorage.CannotPerformOperationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean verify(String password, String encodedPassword) {
        try {
            return PasswordStorage.verifyPassword(password, encodedPassword);
        } catch (PasswordStorage.CannotPerformOperationException e) {
            throw new RuntimeException(e);
        } catch (PasswordStorage.InvalidHashException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String generateResetCode(IdentifierType type) {
        return RandomKey.nextUUIDKey()
                .toRandomHMACKey(HMACKey.Algorithm.SHA256)
                .toBase64String(true);
    }

}
