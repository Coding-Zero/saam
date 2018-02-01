package com.codingzero.saam.common;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Action action = (Action) o;
        return isAllowed() == action.isAllowed() &&
                Objects.equals(getCode(), action.getCode());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getCode(), isAllowed());
    }
}
