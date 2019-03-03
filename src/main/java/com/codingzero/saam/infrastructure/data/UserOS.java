package com.codingzero.saam.infrastructure.data;

import com.codingzero.saam.common.PasswordResetCode;
import com.codingzero.saam.common.PrincipalId;
import com.codingzero.saam.common.PrincipalType;

import java.util.Collections;
import java.util.Date;
import java.util.List;


public class UserOS extends PrincipalOS {

    private String password;
    private PasswordResetCode passwordResetCode;
    private List<String> roleIds;

    public UserOS(PrincipalId id, Date creationTime,
                  String password, PasswordResetCode passwordResetCode, List<String> roleIds) {
        super(id, PrincipalType.USER, creationTime);
        this.password = password;
        this.passwordResetCode = passwordResetCode;
        setRoleIds(roleIds);
    }

    public String getPassword() {
        return password;
    }

    public PasswordResetCode getPasswordResetCode() {
        return passwordResetCode;
    }

    public List<String> getRoleIds() {
        return roleIds;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPasswordResetCode(PasswordResetCode passwordResetCode) {
        this.passwordResetCode = passwordResetCode;
    }

    public void setRoleIds(List<String> roleIds) {
        this.roleIds = Collections.unmodifiableList(roleIds);
    }
}
