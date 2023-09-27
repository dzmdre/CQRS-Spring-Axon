package org.dzmdre.UsersService.core.data;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface UsersRepository extends MongoRepository<UserEntity, String> {
    UserEntity findByUserId(String userId);
}
