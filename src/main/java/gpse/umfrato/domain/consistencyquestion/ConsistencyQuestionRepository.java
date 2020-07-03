package gpse.umfrato.domain.consistencyquestion;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConsistencyQuestionRepository extends JpaRepository<ConsistencyQuestion, Long> {
    List<ConsistencyQuestion> findConsistencyQuestionsByQuestion1IdOrQuestion2Id(Long question1Id, Long question2Id);
    List<ConsistencyQuestion> findConsistencyQuestionsByPollId(final Long pollId);
}
