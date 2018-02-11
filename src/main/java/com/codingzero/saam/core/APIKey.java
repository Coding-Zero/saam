package com.codingzero.saam.core;

public interface APIKey extends Principal {

    String getSecretKey();

    String getName();

    void setName(String name);

    User getOwner();

    boolean isActive();

    void setActive(boolean isActive);

}
