package ru.bookstore.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bookstore.dao.UserDao;
import ru.bookstore.model.impl.User;
import ru.bookstore.service.MyUserDetailsService;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailsServiceImpl implements MyUserDetailsService {
  protected final UserDao userDao;

  @Override
  @Transactional(readOnly = true)
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userDao.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    return org.springframework.security.core.userdetails.User.builder()
        .username(user.getUsername())
        .password(user.getPassword())
        .roles(user.getRole().name())
        .build();
  }

  @Transactional(readOnly = true)
  @Override
  public boolean existsByUsername(String username) {
    return userDao.findByUsername(username).isPresent();
  }

  @Transactional
  @Override
  public void save(User user) {
    userDao.save(user);
  }
}
