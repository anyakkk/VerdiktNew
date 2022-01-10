package vrd.repository;

import org.springframework.data.repository.CrudRepository;
import vrd.base.User;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {
    User getByUsername(String username);

    Optional<User> findByUsername(String username);
}
