package com.codingzero.saam.core;

import java.util.List;

public interface EmailPolicy extends IdentifierPolicy {

    List<String> getDomains();

    void setDomains(List<String> domains);

}
