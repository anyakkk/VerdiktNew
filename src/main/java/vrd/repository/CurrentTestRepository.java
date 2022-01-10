package vrd.repository;

import org.springframework.data.repository.CrudRepository;
import vrd.base.CurrentTest;
import vrd.base.Session;
import vrd.base.Test;
import vrd.base.User;

import java.util.Optional;
import java.lang.Boolean;

public interface CurrentTestRepository  extends CrudRepository<CurrentTest, Long>  {
    boolean existsByTest(Test test);
    Optional<CurrentTest> findBySessionAndUser(Session session, User user);
    boolean existsBySessionAndUser(Session session, User user);
}
