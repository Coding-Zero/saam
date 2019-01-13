package com.codingzero.saam.core.services;

import com.codingzero.saam.core.IdentifierRepository;
import com.codingzero.saam.core.OAuthIdentifierRepository;
import com.codingzero.saam.core.User;
import com.codingzero.saam.core.UserRepository;

public class UserRemovingService {

    private UserRepository userRepository;
    private IdentifierRepository identifierRepository;
    private OAuthIdentifierRepository oAuthIdentifierRepository;

    public UserRemovingService(UserRepository userRepository,
                               IdentifierRepository identifierRepository,
                               OAuthIdentifierRepository oAuthIdentifierRepository) {
        this.userRepository = userRepository;
        this.identifierRepository = identifierRepository;
        this.oAuthIdentifierRepository = oAuthIdentifierRepository;
    }

    public void remove(User user) {
        identifierRepository.removeByUser(user);
        oAuthIdentifierRepository.removeByUser(user);
        userRepository.remove(user);
    }
}
