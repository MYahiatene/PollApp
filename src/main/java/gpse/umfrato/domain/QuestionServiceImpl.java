package gpse.umfrato.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;

    @Autowired
    public QuestionServiceImpl(final QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    @Override
    public Question createQuestion(String message) {

        Question question = new Question(message);

        questionRepository.save(question);

        return question;
    }

    @Override
    public void deleteQuestion() {

    }
}
