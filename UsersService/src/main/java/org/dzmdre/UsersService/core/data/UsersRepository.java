package org.dzmdre.UsersService.core.data;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepository extends JpaRepository<UserEntity, String> {
    UserEntity findByUserId(String userId);
}
