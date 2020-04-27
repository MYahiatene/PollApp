package gpse.umfrato.domain.answer;

import gpse.umfrato.domain.question.Question;
import gpse.umfrato.domain.question.QuestionRepository;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
public class AnswerServiceImpl implements AnswerService {

    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;


    public AnswerServiceImpl(final AnswerRepository answerRepository, final QuestionRepository questionRepository) {
        this.answerRepository = answerRepository;
        this.questionRepository = questionRepository;
    }

    @Override
    public Answer addAnswer(final String message, final String questionId, final String answerType, final String username) {
        Question question =
            questionRepository.findById(Long.valueOf(questionId)).orElseThrow(() -> new EntityNotFoundException());
        Answer answer = new Answer(message, questionId, answerType, username);
        question.getAnswerList().add(answer);
        questionRepository.save(question);
        return answer;
    }


    @Override
    public void deleteAnswer(String questionId, String answerId) {
        Question question =
            questionRepository.findById(Long.valueOf(questionId)).orElseThrow(() -> new EntityNotFoundException());
        List<Answer> answerList = question.getAnswerList();
        answerList.removeIf(obj -> obj.getId() == Long.valueOf(answerId));
        questionRepository.save(question);
    }

    @Override
    public Answer getAnswer(Long answerId) {
        Question question = questionRepository.findById(answerRepository.findById(answerId).orElseThrow(() ->
            new EntityNotFoundException()).getQuestionId()).orElseThrow(() -> new EntityNotFoundException());

        List<Answer> answerList = question.getAnswerList();

        return answerList.stream().filter(o -> o.getId() == answerId).findFirst().get();
    }

    @Override
    public List<Answer> getAllAnswers(String questionId) {
        List<Answer> answers = questionRepository.findById(Long.valueOf(questionId)).orElseThrow(() -> new EntityNotFoundException()).getAnswerList();

        return answers;
    }
}
