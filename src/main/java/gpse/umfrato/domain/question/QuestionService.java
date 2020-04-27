package gpse.umfrato.domain.question;

import java.util.List;

public interface QuestionService {
    Question addQuestion(String questionMessage, String pollId);

    void removeQuestion(String pollId, String questionId);

    Question getQuestion(Long questionId);

    List<Question> getAllQuestions();
}
