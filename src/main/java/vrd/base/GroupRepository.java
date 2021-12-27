package vrd.base;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GroupRepository extends CrudRepository<Group, Long> {

    @Query(value="SELECT * FROM QUESTGROUP gr WHERE gr.TESTID = :testId ORDER BY gr.NAMEORDER;", nativeQuery = true)
    List<Group> findFromTest(@Param("testId") Long testId);
}
