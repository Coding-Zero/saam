package com.codingzero.saam.domain;

import com.codingzero.saam.common.UsernameFormat;

public interface UsernamePolicy extends IdentifierPolicy {

    UsernameFormat getFormat();

}
