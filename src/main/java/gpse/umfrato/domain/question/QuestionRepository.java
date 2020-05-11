package gpse.umfrato.domain.question;

import gpse.umfrato.domain.pollGroup.Group;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    public List<Question> findQuestionByGroup_Id(final long groupId);
}
