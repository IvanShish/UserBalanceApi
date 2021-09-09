package com.example.userbalanceapi;

import com.example.userbalanceapi.io.entity.UserEntity;
import com.example.userbalanceapi.io.repository.TransactionRepository;
import com.example.userbalanceapi.io.repository.UserRepository;
import com.example.userbalanceapi.service.UserService;
import com.example.userbalanceapi.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(UserServiceImpl.class)
class UserBalanceApiApplicationTests {

    @Autowired
    UserRepository userRepository;

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    UserServiceImpl userService;

    @Test
    void contextLoads() {
        assertThat(userRepository).isNotNull();
        assertThat(transactionRepository).isNotNull();
        assertThat(userService).isNotNull();
    }

    @Test
    void subtractAndAddBalanceTest() {
        UserEntity user = new UserEntity("test_name_1", "test_surname_1", "email_test@test.com", "encryptedPass1");
        userRepository.save(user);

        String id = userRepository.findByEmail(user.getEmail()).getId();

        userService.addBalance(id, 150.0);

        Double balanceBefore = userService.getUserBalance(id);
        Double newBalance = userService.subtractBalance(id, 50.0);

        assertThat(newBalance).isEqualTo(balanceBefore - 50.0);
    }

    @Test
    void subtractWhenBalanceIsNullTest() {
        UserEntity user = new UserEntity("test_name_1", "test_surname_1", "email_test@test.com", "encryptedPass1");
        userRepository.save(user);

        String id = userRepository.findByEmail(user.getEmail()).getId();

        Double balanceBefore = userService.getUserBalance(id);
        try {
            userService.subtractBalance(id, 50.0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertThat(userService.getUserBalance(id)).isEqualTo(balanceBefore);
    }

    @Test
    void subtractWhenBalanceIsNotEnoughTest() {
        UserEntity user = new UserEntity("test_name_1", "test_surname_1", "email_test@test.com", "encryptedPass1");
        userRepository.save(user);

        String id = userRepository.findByEmail(user.getEmail()).getId();

        userService.addBalance(id, 150.0);
        Double balanceBefore = userService.getUserBalance(id);
        try {
            userService.subtractBalance(id, 200.0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertThat(userService.getUserBalance(id)).isEqualTo(balanceBefore);
    }

    @Test
    void transferBalanceTest() {
        UserEntity user1 = new UserEntity("test_name_1", "test_surname_1", "email_test@test.com", "encryptedPass1");
        userRepository.save(user1);

        String id1 = userRepository.findByEmail(user1.getEmail()).getId();

        userService.addBalance(id1, 150.0);

        UserEntity user2 = new UserEntity("test_name_2", "test_surname_2", "email_test2@test.com", "encryptedPass2");
        userRepository.save(user2);

        String id2 = userRepository.findByEmail(user2.getEmail()).getId();

        Double balanceBefore1 = userService.getUserBalance(id1);
        Double balanceBefore2 = userService.getUserBalance(id2);
        if (balanceBefore2 == null) balanceBefore2 = 0.0;
        userService.transferBalance(id1, id2, 100.0);

        assertThat(userService.getUserBalance(id1)).isEqualTo(balanceBefore1 - 100.0);
        assertThat(userService.getUserBalance(id2)).isEqualTo(balanceBefore2 + 100.0);
    }

    @Test
    void transferWhenBalanceIsNullTest() {
        UserEntity user1 = new UserEntity("test_name_1", "test_surname_1", "email_test@test.com", "encryptedPass1");
        userRepository.save(user1);

        String id1 = userRepository.findByEmail(user1.getEmail()).getId();

        UserEntity user2 = new UserEntity("test_name_2", "test_surname_2", "email_test2@test.com", "encryptedPass2");
        userRepository.save(user2);

        String id2 = userRepository.findByEmail(user2.getEmail()).getId();

        Double balanceBefore1 = userService.getUserBalance(id1);
        Double balanceBefore2 = userService.getUserBalance(id2);
        try {
            userService.transferBalance(id1, id2, 100.0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertThat(userService.getUserBalance(id1)).isEqualTo(balanceBefore1);
        assertThat(userService.getUserBalance(id2)).isEqualTo(balanceBefore2);
    }

    @Test
    void transferWhenBalanceIsNotEnoughTest() {
        UserEntity user1 = new UserEntity("test_name_1", "test_surname_1", "email_test@test.com", "encryptedPass1");
        userRepository.save(user1);

        String id1 = userRepository.findByEmail(user1.getEmail()).getId();
        userService.addBalance(id1, 150.0);

        UserEntity user2 = new UserEntity("test_name_2", "test_surname_2", "email_test2@test.com", "encryptedPass2");
        userRepository.save(user2);

        String id2 = userRepository.findByEmail(user2.getEmail()).getId();

        Double balanceBefore1 = userService.getUserBalance(id1);
        Double balanceBefore2 = userService.getUserBalance(id2);
        try {
            userService.transferBalance(id1, id2, 200.0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertThat(userService.getUserBalance(id1)).isEqualTo(balanceBefore1);
        assertThat(userService.getUserBalance(id2)).isEqualTo(balanceBefore2);
    }

    @Test
    void statementSizeTest() {
        UserEntity user1 = new UserEntity("test_name_1", "test_surname_1", "email_test@test.com", "encryptedPass1");
        userRepository.save(user1);

        String id1 = userRepository.findByEmail(user1.getEmail()).getId();

        UserEntity user2 = new UserEntity("test_name_2", "test_surname_2", "email_test2@test.com", "encryptedPass2");
        userRepository.save(user2);

        String id2 = userRepository.findByEmail(user2.getEmail()).getId();

        int statementSize1Before = userService.getStatement(id1, 0, 10).size();
        int statementSize2Before = userService.getStatement(id2, 0, 10).size();

        userService.addBalance(id1, 150.0);
        userService.addBalance(id1, 50.0);
        try {
            userService.subtractBalance(id1, 201.0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        userService.transferBalance(id1, id2, 100.0);
        userService.subtractBalance(id2, 100.0);

        assertThat(userService.getStatement(id1, 0, 10).size()).isEqualTo(statementSize1Before + 3);
        assertThat(userService.getStatement(id2, 0, 10).size()).isEqualTo(statementSize2Before + 2);
    }
}
