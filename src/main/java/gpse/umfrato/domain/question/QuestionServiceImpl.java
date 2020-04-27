package gpse.umfrato.domain.question;

import gpse.umfrato.domain.poll.Poll;
import gpse.umfrato.domain.poll.PollRepository;
import gpse.umfrato.domain.poll.PollService;
import gpse.umfrato.web.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
public class QuestionServiceImpl implements QuestionService {

    private final PollRepository pollRepository;
    private final QuestionRepository questionRepository;
    private final PollService pollService;

    @Autowired
    public QuestionServiceImpl(final PollRepository pollRepository, final QuestionRepository questionRepository,
                               final PollService pollService) {
        this.pollRepository = pollRepository;
        this.questionRepository = questionRepository;
        this.pollService = pollService;
    }

    @Override
    public Question addQuestion(String questionMessage, String pollId) {
        Poll poll = pollRepository.findById(Long.valueOf(pollId)).orElseThrow(() -> new EntityNotFoundException());
        Question question = new Question(questionMessage);
        question.setPoll(poll);
        poll.getQuestionList().add(question);
        pollRepository.save(poll);
        return question;
    }

    @Override
    public void removeQuestion(String pollId, String questionId) {
        Poll poll = pollRepository.findById(Long.valueOf(pollId)).orElseThrow(() -> new EntityNotFoundException());
        List<Question> questionList = poll.getQuestionList();
        questionList.removeIf(obj -> obj.getId() == Long.valueOf(questionId));
        pollRepository.save(poll);
    }

    @Override
    public Question getQuestion(Long questionId) {

        Question question = questionRepository.findById(questionId).orElseThrow(() -> new EntityNotFoundException());

        return question;
    }

    @Override
    public List<Question> getAllQuestions() {

        List<Question> questions = questionRepository.findAll();

        if (questions.isEmpty()) {
            throw new BadRequestException();
        }

        return questions;
    }


}
