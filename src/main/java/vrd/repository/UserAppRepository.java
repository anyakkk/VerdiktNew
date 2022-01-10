package vrd.repository;

import org.springframework.data.repository.CrudRepository;
import vrd.base.Session;
import vrd.base.StatusApp;
import vrd.base.User;
import vrd.base.UserApp;

import java.util.Optional;
import java.util.Set;

public interface UserAppRepository extends CrudRepository<UserApp, Long> {
    Set<UserApp> findBySessionAndStatus(Session session, StatusApp status);
    Optional<UserApp> findBySessionAndUser(Session session, User user);
}
