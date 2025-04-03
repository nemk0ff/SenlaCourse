package ru.bookstore.controllers.impl;

import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.bookstore.controllers.AuthController;
import ru.bookstore.dto.AuthDTO;
import ru.bookstore.exceptions.UserAlreadyExistsException;
import ru.bookstore.model.Role;
import ru.bookstore.model.impl.User;
import ru.bookstore.security.JwtUtils;
import ru.bookstore.service.MyUserDetailsService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthControllerImpl implements AuthController {
  private final MyUserDetailsService userDetailsService;
  private final PasswordEncoder passwordEncoder;

  @PostMapping("/login")
  @Override
  public ResponseEntity<?> login(@RequestBody @Valid AuthDTO request) {
    UserDetails user = userDetailsService.loadUserByUsername(request.getUsername());
    if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
      String role = user.getAuthorities().iterator().next().getAuthority();
      String token = JwtUtils.generateToken(user.getUsername(), role);
      return ResponseEntity.ok(Map.of("token", token, "role", role));
    }
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(Map.of("error", "Неверный логин или пароль"));
  }

  @PostMapping("/register")
  @Override
  public ResponseEntity<?> register(@RequestBody @Valid AuthDTO request) {
    if (userDetailsService.existsByUsername(request.getUsername())) {
      throw new UserAlreadyExistsException("Пользователь " + request.getUsername()
          + " уже существует");
    }
    User user = new User();
    user.setUsername(request.getUsername());
    user.setPassword(passwordEncoder.encode(request.getPassword()));
    user.setRole(Role.USER);
    userDetailsService.save(user);
    return ResponseEntity.ok(new AuthDTO(request.getUsername(), request.getPassword()));
  }

//  @PostMapping("/logout")
//  public ResponseEntity<?> logout(HttpServletRequest request) {
//    String token = null;
//    String header = request.getHeader("Authorization");
//    if (header != null && header.startsWith("Bearer")) {
//      token = header.substring(7);
//    }
//    SecurityContextHolder.clearContext();
//    return ResponseEntity.ok().body(Map.of(
//        "message", "logout successful.",
//        "invalidated_token", token
//    ));
//  }
}
