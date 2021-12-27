package vrd.base;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.lang.Boolean;

public interface CurrentTestRepository  extends CrudRepository<CurrentTest, Long>  {
    boolean existsByTest(Test test);
    Optional<CurrentTest> findBySessionAndUser(Session session, User user);
    boolean existsBySessionAndUser(Session session, User user);
}
