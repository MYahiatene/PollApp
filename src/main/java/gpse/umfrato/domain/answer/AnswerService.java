package gpse.umfrato.domain.answer;

import java.util.List;

public interface AnswerService {
    Answer addAnswer(final String message, final String questionId, final String answerType, final String username);

    void deleteAnswer(final String questionId, final String answerId);

    Answer getAnswer(Long answerId);

    List<Answer> getAllAnswers(String questionId);
}
