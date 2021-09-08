package com.example.userbalanceapi.service.impl;

import com.example.userbalanceapi.io.entity.UserEntity;
import com.example.userbalanceapi.io.repository.UserRepository;
import com.example.userbalanceapi.service.UserService;
import com.example.userbalanceapi.ui.model.request.UserDetailsRequestModel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public UserEntity createUser(UserDetailsRequestModel userDetails) {
        if (userRepository.findByEmail(userDetails.getEmail()) != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Error: Record already exists");
        }

        UserEntity userEntity = new UserEntity();
        BeanUtils.copyProperties(userDetails, userEntity);

        userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(userDetails.getPassword()));

        return userRepository.save(userEntity);
    }

    @Override
    public UserEntity getUser(String userId) {

        return userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Error: User with ID: " + userId + " not found"));
    }

    @Override
    public List<UserEntity> getUsers(int page, int limit) {
        Pageable pageableRequest = PageRequest.of(page, limit);
        Page<UserEntity> usersPage = userRepository.findAll(pageableRequest);
        return usersPage.getContent();
    }

    @Override
    public UserEntity updateUser(String userId, UserDetailsRequestModel userDetails) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Error: User with ID: " + userId + " not found"));

        userEntity.setFirstName(userDetails.getFirstName());
        userEntity.setLastName(userDetails.getLastName());

        return userRepository.save(userEntity);
    }

    @Override
    public void deleteUser(String userId) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Error: User with ID: " + userId + " not found"));
        userRepository.deleteById(userEntity.getId());
    }

    @Override
    public double addBalance(String userId, Double value) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Error: User with ID: " + userId + " not found"));

        if (value == null) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Error: Balance not provided");
        }

        Double currentBalance = userEntity.getBalance();
        if (currentBalance == null) {
            userEntity.setBalance(value);
        } else {
            userEntity.setBalance(currentBalance + value);
        }

        return userRepository.save(userEntity).getBalance();
    }

    @Override
    public double subtractBalance(String userId, Double value) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Error: User with ID: " + userId + " not found"));

        if (value == null) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Error: Balance not provided");
        }

        Double currentBalance = userEntity.getBalance();
        if (currentBalance == null) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Error: User balance is null now");
        }

        userEntity.setBalance(currentBalance - value);

        return userRepository.save(userEntity).getBalance();
    }

    @Override
    public double getUserBalance(String userId) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Error: User with ID: " + userId + " not found"));

        return userEntity.getBalance();
    }

    @Override
    public void transferBalance(String userIdFrom, String userIdTo, Double value) {
        UserEntity userEntityFrom = userRepository.findById(userIdFrom)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Error: User with ID: " + userIdFrom + " not found"));

        UserEntity userEntityTo = userRepository.findById(userIdTo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Error: User with ID: " + userIdTo + " not found"));

        if (value == null) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Error: Balance not provided");
        }

        Double currentBalanceUserFrom = userEntityFrom.getBalance();
        if (currentBalanceUserFrom == null) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "Error: Balance of user with ID: " + userIdFrom + " is null now. Transfer rejected");
        }
        else if (currentBalanceUserFrom - value < 0.0) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "The balance on the account of the user with the ID: " + userIdFrom + " is not enough. Transfer rejected");
        }

        Double currentBalanceUserTo = userEntityTo.getBalance();
        if (currentBalanceUserTo == null) {
            userEntityTo.setBalance(value);
        }
        else {
            userEntityTo.setBalance(currentBalanceUserTo + value);
        }

        userEntityFrom.setBalance(currentBalanceUserFrom - value);
        userRepository.save(userEntityFrom);
        userRepository.save(userEntityTo);
    }
}
