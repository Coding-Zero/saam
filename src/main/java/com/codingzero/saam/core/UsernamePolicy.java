package com.codingzero.saam.core;

import com.codingzero.saam.common.UsernameFormat;

public interface UsernamePolicy extends IdentifierPolicy {

    UsernameFormat getFormat();

}
