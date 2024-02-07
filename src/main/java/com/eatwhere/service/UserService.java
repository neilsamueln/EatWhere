package com.eatwhere.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.eatwhere.exception.ApiException;
import com.eatwhere.exception.ApiException.ApiExceptionType;
import com.eatwhere.model.User;
import com.eatwhere.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User createUser(User user) throws ApiException {
        if (user == null || StringUtils.isBlank(user.getName())) {
            throw new ApiException(ApiExceptionType.INVALID_INPUT);
        }

        return userRepository.save(user);
    }

    public User findUser(Long id) throws ApiException {
        return userRepository.findById(id).orElseThrow(() -> new ApiException(ApiExceptionType.NOT_FOUND));
    }

    public User findUserByName(String name) throws ApiException {
        if (StringUtils.isBlank(name)) {
            throw new ApiException(ApiExceptionType.INVALID_INPUT);
        }

        User user = userRepository.findByNameIgnoreCase(name.trim());
        if (user == null) {
            throw new ApiException(ApiExceptionType.NOT_FOUND);
        }

        return user;
    }
    
}
