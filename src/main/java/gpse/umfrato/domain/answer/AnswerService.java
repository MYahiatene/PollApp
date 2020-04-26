package gpse.umfrato.domain.answer;

import gpse.umfrato.domain.question.Question;
import gpse.umfrato.domain.user.User;

public interface AnswerService {
    Answer createAnswer(final String answer, final User user, final String answertype, final Long questionId);

}
