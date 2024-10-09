package com.dragontho.aqtakehome.repositories;

import com.dragontho.aqtakehome.models.User;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends ListCrudRepository<User, Long> {
    List<User> findByUsername(String username);
}
