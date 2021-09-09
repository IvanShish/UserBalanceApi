package com.example.userbalanceapi.ui.controller;

import com.example.userbalanceapi.io.entity.TransactionEntity;
import com.example.userbalanceapi.io.entity.UserEntity;
import com.example.userbalanceapi.service.UserService;
import com.example.userbalanceapi.ui.model.request.FundsTransferRequestModel;
import com.example.userbalanceapi.ui.model.request.UserDetailsRequestModel;
import com.example.userbalanceapi.ui.model.response.UserBalanceResponse;
import com.example.userbalanceapi.ui.model.response.UserRest;
import com.example.userbalanceapi.ui.model.response.UserTransactionsResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("users")
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping(path = "/{id}", produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public UserRest getUser(@PathVariable String id) {
        UserRest returnValue = new UserRest();

        UserEntity userEntity = userService.getUser(id);
        BeanUtils.copyProperties(userEntity, returnValue);

        return returnValue;
    }

    @GetMapping(produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public List<UserRest> getUsers(@RequestParam(value = "page", defaultValue = "0") int page,
                                   @RequestParam(value = "limit", defaultValue = "5") int limit) {
        List<UserRest> returnValue = new ArrayList<>();

        List<UserEntity> users = userService.getUsers(page, limit);

        for (UserEntity user : users) {
            UserRest userModel = new UserRest();
            BeanUtils.copyProperties(user, userModel);
            returnValue.add(userModel);
        }

        return returnValue;
    }

    @PostMapping(consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails) {
        UserRest returnValue = new UserRest();

        UserEntity userEntity = userService.createUser(userDetails);
        BeanUtils.copyProperties(userEntity, returnValue);

        return returnValue;
    }

    @PutMapping(path = "/{id}", consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public UserRest updateUser(@PathVariable String id, @RequestBody UserDetailsRequestModel userDetails) {
        UserRest returnValue = new UserRest();

        UserEntity updateUser = userService.updateUser(id, userDetails);
        BeanUtils.copyProperties(updateUser, returnValue);

        return returnValue;
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);

        return ResponseEntity.ok().body("User with ID: " + id + " deleted");
    }

    @PatchMapping(path = "/{id}/balance/add", consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> addUserBalance(@PathVariable String id, @RequestBody UserDetailsRequestModel userDetails) {
        Double newBalance = userService.addBalance(id, userDetails.getBalance());

        return ResponseEntity.ok().body("Balance of the user with ID: " + id + " now " + newBalance);
    }

    @PatchMapping(path = "/{id}/balance/subtract", consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> subtractUserBalance(@PathVariable String id, @RequestBody UserDetailsRequestModel userDetails) {
        Double newBalance = userService.subtractBalance(id, userDetails.getBalance());

        return ResponseEntity.ok().body("Balance of the user with ID: " + id + " now " + newBalance);
    }

    @GetMapping(path = "/{id}/balance", produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public UserBalanceResponse getUserBalance(@PathVariable String id,
                                              @RequestParam(value = "currency", defaultValue = "RUB") String currency) {

        Double balance = userService.getUserBalance(id);
        UserBalanceResponse returnValue = new UserBalanceResponse(id, balance);
        returnValue.convertBalance(currency);

        return returnValue;
    }

    @PatchMapping(path = "/{id}/balance/transfer", consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> fundsTransfer(@PathVariable String id, @RequestBody FundsTransferRequestModel fundsTransfer) {
        userService.transferBalance(id, fundsTransfer.getUserIdToTransfer(), fundsTransfer.getBalance());

        return ResponseEntity.ok().body("Balance of the user with ID: " + id + " now " + userService.getUserBalance(id) +
                "\nBalance of the user with ID: " + fundsTransfer.getUserIdToTransfer() +
                " now " + userService.getUserBalance(fundsTransfer.getUserIdToTransfer()));
    }

    @GetMapping(path = "/{id}/balance/statement", produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public UserTransactionsResponse getStatement(@PathVariable String id, @RequestParam(value = "page", defaultValue = "0") int page,
                                          @RequestParam(value = "limit", defaultValue = "5") int limit) {

        List<TransactionEntity> transactions = userService.getStatement(id, page, limit);
        return new UserTransactionsResponse(userService.getUserBalance(id), transactions);
    }
}
