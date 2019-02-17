package com.codingzero.saam.domain;

public interface IdentifierFactory {

    Identifier generate(IdentifierPolicy policy, String content, User user);
}
