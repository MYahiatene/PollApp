package gpse.umfrato.domain.answer.question;

import gpse.umfrato.domain.answer.user.User;
import gpse.umfrato.domain.answer.Answer;

public interface QuestionService {
    Question createQuestion(String question,Long pollId);

    Answer createAnswer(final String answer, final User user, final String answertype, final Long questionId);
    void deleteQuestion();

}
