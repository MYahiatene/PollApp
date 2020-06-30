package gpse.umfrato.domain.answer;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * The repository in which answers are stored.
 */
public interface AnswerRepository extends JpaRepository<Answer, Long> {
    /**
     * This method finds all answers belonging to one question id.
     * @param questionId the id of the question
     * @return returns all the answers in a list
     */
    List<Answer> findAnswerByQuestionId(final Long questionId);
}
