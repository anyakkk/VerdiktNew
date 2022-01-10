package vrd.repository;

import org.springframework.data.repository.CrudRepository;
import vrd.base.Quest;

public interface QuestRepository extends CrudRepository<Quest, Long> {
}
