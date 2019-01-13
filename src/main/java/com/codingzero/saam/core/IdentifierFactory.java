package com.codingzero.saam.core;

public interface IdentifierFactory {

    Identifier generate(IdentifierPolicy policy, String content, User user);
}
