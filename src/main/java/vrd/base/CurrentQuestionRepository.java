package vrd.base;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CurrentQuestionRepository extends CrudRepository<CurrentQuestion, Long> {
    Optional<CurrentQuestion> findByTestAndInTestIndex(CurrentTest test, Integer inTestIndex);
}
