package gpse.umfrato.domain.question;

import gpse.umfrato.domain.user.User;
import gpse.umfrato.domain.answer.Answer;

public interface QuestionService {
    Question createQuestion(String question,Long pollId);

    void deleteQuestion();

}
