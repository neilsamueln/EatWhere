package com.eatwhere.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.eatwhere.model.User;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    
    User findByNameIgnoreCase(String name);

}
