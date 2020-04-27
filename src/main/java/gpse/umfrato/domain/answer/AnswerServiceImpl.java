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
    public Answer addAnswer(String answerMessage, String questionId) {
        Question question =
            questionRepository.findById(Long.valueOf(questionId)).orElseThrow(() -> new EntityNotFoundException());
        String username = "mokrane";
        Answer answer = new Answer(answerMessage, username, "answerType", Long.valueOf(questionId));
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
}
