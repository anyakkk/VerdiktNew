package vrd.repository;

import org.springframework.data.repository.CrudRepository;
import vrd.base.Answer;
import vrd.base.Quest;

import java.util.Optional;

public interface AnswerRepository extends CrudRepository<Answer, Long> {
    Optional<Answer> findByQuestAndOrder(Quest quest, int order);
}
