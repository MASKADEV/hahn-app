package com.hahn.infrastructure.persistence.user;

import com.hahn.domain.model.User;

interface UserMapper {
    static UserEntity toEntity(User user) {
        UserEntity entity = new UserEntity();
        entity.setId(user.getId());
        entity.setUsername(user.getUsername());
        entity.setEmail(user.getEmail());
        entity.setPassword(user.getPassword());
        entity.setActive(user.isActive());
        entity.setRoles(user.getRoles());
        return entity;
    }

    static User toDomain(UserEntity entity) {
        User user = User.create(
                entity.getUsername(),
                entity.getEmail(),
                entity.getPassword(),
                entity.getRoles()
        );

        user.setId(entity.getId());

        if (entity.isActive()) {
            user.activate();
        } else {
            user.deactivate();
        }

        return user;
    }
}
