package com.codingzero.saam.core;

import com.codingzero.saam.common.PasswordResetCode;

import java.util.List;

public interface User extends Principal {

    /**Role**/

    List<Role> getPlayingRoles();

    void setPlayingRoles(List<Role> roles);

    /**Password**/

    boolean isPasswordSet();

    void changePassword(String oldPassword, String newPassword);

    boolean verifyPassword(String password);

    PasswordResetCode generatePasswordResetCode(Identifier identifier, long timeout);

    void resetPassword(String code, String newPassword);

}
