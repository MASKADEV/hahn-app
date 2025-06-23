package com.hahn.domain.model;

import com.hahn.domain.enums.Role;
import lombok.*;

import java.util.Collections;
import java.util.Set;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private Long id;
    private String username;
    private String email;
    private String password;
    private boolean active;
    private Set<Role> roles;

    public static User create(String username, String email, String password, Set<Role> roles) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }

        return new User(null, username.trim(), email.trim(), password.trim(), true,
                roles != null ? roles : Collections.singleton(Role.ROLE_USER));
    }

    public void changePassword(String newPassword) {
        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        this.password = newPassword.trim();
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }
}
