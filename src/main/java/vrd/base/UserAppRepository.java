package vrd.base;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.Set;

public interface UserAppRepository extends CrudRepository<UserApp, Long> {
    Set<UserApp> findBySessionAndStatus(Session session, StatusApp status);
    Optional<UserApp> findBySessionAndUser(Session session, User user);
}
