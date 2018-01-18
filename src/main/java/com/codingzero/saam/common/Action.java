package com.codingzero.saam.common;

public class Action {

    private String code;
    private boolean isAllowed;

    public Action(String code, boolean isAllowed) {
        this.code = code;
        this.isAllowed = isAllowed;
    }

    public String getCode() {
        return code;
    }

    public boolean isAllowed() {
        return isAllowed;
    }

}
