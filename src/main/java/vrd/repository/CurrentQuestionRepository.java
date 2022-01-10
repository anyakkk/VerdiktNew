package vrd.repository;

import org.springframework.data.repository.CrudRepository;
import vrd.base.CurrentQuestion;
import vrd.base.CurrentTest;

import java.util.Optional;

public interface CurrentQuestionRepository extends CrudRepository<CurrentQuestion, Long> {
    Optional<CurrentQuestion> findByTestAndInTestIndex(CurrentTest test, Integer inTestIndex);
}
