package vrd.base;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {
    User getByUsername(String username);

    Optional<User> findByUsername(String username);
}
