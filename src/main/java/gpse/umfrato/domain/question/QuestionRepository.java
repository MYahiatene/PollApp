package gpse.umfrato.domain.question;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
/**
 * The repository in which questions are stored.
 */
public interface QuestionRepository extends JpaRepository<Question, Long> {
    /**
     * This method returns all questions belonging to the same category id.
     * @param categoryId the category id
     * @return returns all questions as a list
     */
    List<Question> findQuestionsByCategoryId(final long categoryId);
}
