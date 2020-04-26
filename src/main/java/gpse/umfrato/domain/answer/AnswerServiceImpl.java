package gpse.umfrato.domain.answer;

import gpse.umfrato.domain.question.Question;
import gpse.umfrato.domain.question.QuestionRepository;
import gpse.umfrato.domain.user.User;
import org.springframework.stereotype.Service;

@Service
public class AnswerServiceImpl implements AnswerService {

    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;


    public AnswerServiceImpl(final AnswerRepository answerRepository, final QuestionRepository questionRepository) {
        this.answerRepository = answerRepository;
        this.questionRepository = questionRepository;
    }

    @Override
    public Answer createAnswer(String answer, User user, String answertype, Long questionId) {
        Answer newAnswer = new Answer(answer, user, answertype, questionRepository.getOne(questionId));
        questionRepository.getOne(questionId).getAnswerList().add(newAnswer);
        return newAnswer;
    }
}
