package com.example.userbalanceapi.io.repository;

import com.example.userbalanceapi.io.entity.UserEntity;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends PagingAndSortingRepository<UserEntity, String> {
    UserEntity findByEmail(String email);
}
