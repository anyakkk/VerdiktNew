package vrd.repository;

import org.springframework.data.repository.CrudRepository;
import vrd.base.Test;
import vrd.base.User;

import java.util.List;

public interface TestRepository extends CrudRepository<Test, Long> {
    List<Test> findByUser(User user);
}

