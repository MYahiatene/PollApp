package gpse.umfrato.domain.ConsistencyQuestion;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConsistencyQuestionRepository extends JpaRepository<ConsistencyQuestion, Long> {
    List<ConsistencyQuestion> findConsistencyQuestionsByQuestion1IdOrQuestion2Id(final Long question1Id, final Long question2Id);
    List<ConsistencyQuestion> findConsistencyQuestionsByPollId(final Long pollId);
}
