package com.example.userbalanceapi.service;

import com.example.userbalanceapi.io.entity.UserEntity;
import com.example.userbalanceapi.ui.model.request.UserDetailsRequestModel;

import java.util.List;

public interface UserService {
    UserEntity createUser(UserDetailsRequestModel userDetails);
    UserEntity getUser(String userId);
    List<UserEntity> getUsers(int page, int limit);
    UserEntity updateUser(String userId, UserDetailsRequestModel userDetails);
    void deleteUser(String userId);
    double addBalance(String userId, Double value);
    double subtractBalance(String userId, Double value);
    double getUserBalance(String userId);
    void transferBalance(String userIdFrom, String userIdTo, Double value);
}
