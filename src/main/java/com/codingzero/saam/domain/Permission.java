package com.codingzero.saam.domain;

import com.codingzero.saam.common.Action;

import java.util.Date;
import java.util.List;

public interface Permission {

    Resource getResource();

    Principal getPrincipal();

    Date getCreationTime();

    void setActions(List<Action> actions);

    List<Action> getActions();

    boolean containAction(String actionCode);
    
}
