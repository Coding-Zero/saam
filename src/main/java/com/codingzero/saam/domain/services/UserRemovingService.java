package com.codingzero.saam.domain.services;

import com.codingzero.saam.domain.IdentifierRepository;
import com.codingzero.saam.domain.OAuthIdentifierRepository;
import com.codingzero.saam.domain.User;
import com.codingzero.saam.domain.UserRepository;

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
