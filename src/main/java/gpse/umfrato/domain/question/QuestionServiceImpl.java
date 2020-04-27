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

    /**
     * initializes the poll repository.
     */
    private final PollRepository pollRepository;

    /**
     * initializes the question repository.
     */
    private final QuestionRepository questionRepository;

    /**
     * initializes the poll service.
     */
    private final PollService pollService;

    /**
     * This class constructor initializes the poll-, question repository and the poll service.
     *
     * @param pollRepository     the repository for the polls
     * @param questionRepository the repository for the questions
     * @param pollService        the class to work with polls
     */
    @Autowired
    public QuestionServiceImpl(final PollRepository pollRepository, final QuestionRepository questionRepository,
                               final PollService pollService) {
        this.pollRepository = pollRepository;
        this.questionRepository = questionRepository;
        this.pollService = pollService;
    }

    /**
     * This method creates a question for a poll.
     *
     * @param questionMessage the given question
     * @param pollId          the id of the poll
     * @return the question which is created
     */
    @Override
    public Question addQuestion(final String questionMessage, final String pollId) {
        final Poll poll = pollRepository.findById(Long.valueOf(pollId)).orElseThrow(() ->
            new EntityNotFoundException());
        final Question question = new Question(questionMessage);
        question.setPoll(poll);
        poll.getQuestionList().add(question);
        pollRepository.save(poll);
        return question;
    }

    /**
     * This method removes a selected question.
     *
     * @param pollId     the id of the poll where the question is setted
     * @param questionId the id of the selectes question
     */
    @Override
    public void removeQuestion(final String pollId, final String questionId) {
        final Poll poll = pollRepository.findById(Long.valueOf(pollId)).orElseThrow(() ->
            new EntityNotFoundException());
        final List<Question> questionList = poll.getQuestionList();
        questionList.removeIf(obj -> obj.getId() == Long.valueOf(questionId));
        pollRepository.save(poll);
    }

    /**
     * This method returns the requested question.
     *
     * @param questionId the id of the requested question
     * @return the requested question
     */
    @Override
    public Question getQuestion(final Long questionId) {

        final Question question = questionRepository.findById(questionId).orElseThrow(() ->
            new EntityNotFoundException());

        return question;
    }

    /**
     * This method returns all questions from a poll.
     *
     * @return all questions from a poll
     */
    @Override
    public List<Question> getAllQuestions() {

        final List<Question> questions = questionRepository.findAll();

        if (questions.isEmpty()) {
            throw new BadRequestException();
        }

        return questions;
    }


}
