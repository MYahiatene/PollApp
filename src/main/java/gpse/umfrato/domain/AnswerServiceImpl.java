package gpse.umfrato.domain;

import org.springframework.beans.factory.annotation.Autowired;

public class AnswerServiceImpl implements AnswerService {

    private final QuestionRepository questionRepository;

    @Autowired
    public AnswerServiceImpl(final QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    @Override
    public Answer createAnswer(String answer, User user, String answertype) {

        Answer ans = new Answer(answer, user, answertype);

        questionRepository.save();

        return ans;
    }
}
