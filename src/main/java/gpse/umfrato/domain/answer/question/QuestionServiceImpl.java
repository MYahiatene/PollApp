package gpse.umfrato.domain.answer.question;

import gpse.umfrato.domain.answer.user.User;
import gpse.umfrato.domain.answer.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    private Question question;

    @Autowired
    public QuestionServiceImpl(final QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    @Override
    public Question createQuestion(String message) {

        this.question = new Question(message);

        questionRepository.save(question);

        return question;
    }

    @Override
    public Answer createAnswer(String answer, User user, String answertype, Long questionId) {

        Optional<Question> tmp = questionRepository.findById(questionId);
        Question questionByAnswer = tmp.get();
        Answer ans = new Answer(answer, user, answertype, questionByAnswer);
        questionByAnswer.getAnswerList().add(ans);
        questionRepository.save(questionByAnswer);
        return ans;
    }


    @Override
    public void deleteQuestion() {

    }
}
