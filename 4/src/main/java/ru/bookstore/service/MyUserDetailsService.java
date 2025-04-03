package ru.bookstore.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import ru.bookstore.model.impl.User;

public interface MyUserDetailsService extends UserDetailsService {
  boolean existsByUsername(String username);

  void save(User user);
}
