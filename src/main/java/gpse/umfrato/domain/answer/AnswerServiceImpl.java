package gpse.umfrato.domain.answer;

import gpse.umfrato.domain.category.CategoryRepository;
import gpse.umfrato.domain.poll.PollRepository;
import gpse.umfrato.domain.pollresult.PollResult;
import gpse.umfrato.domain.pollresult.PollResultRepository;
import gpse.umfrato.domain.question.QuestionRepository;
import gpse.umfrato.domain.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
public class AnswerServiceImpl implements AnswerService {

    /* default */ final QuestionRepository questionRepository;
    /* default */ final UserRepository userRepository;
    private final AnswerRepository answerRepository;
    /* default */ final PollRepository pollRepository;
    /* default */ final CategoryRepository categoryRepository;
    private final PollResultRepository pollResultRepository;

    /**
     * This class constructor initializes the answer- and question repository.
     *
     * @param answerRepository     the answer repository with answers
     * @param questionRepository   the question repository with questions
     * @param userRepository       the user repository with all users
     * @param pollRepository       the poll repository with all polls
     * @param categoryRepository   the category repository with all polls
     * @param pollResultRepository the poll result repository with all poll results
     */
    @Autowired
    public AnswerServiceImpl(final AnswerRepository answerRepository, final QuestionRepository questionRepository,
                             final UserRepository userRepository, final PollRepository pollRepository,
                             final CategoryRepository categoryRepository,
                             final PollResultRepository pollResultRepository) {
        this.answerRepository = answerRepository;
        this.questionRepository = questionRepository;
        this.userRepository = userRepository;
        this.pollRepository = pollRepository;
        this.categoryRepository = categoryRepository;
        this.pollResultRepository = pollResultRepository;
    }

    /**
     * This method adds an answer to the answer list of the question.
     *
     * @param questionId the id of the given question
     * @param username   name of the user creating the answer
     * @return the given answer
     */
    @Override
    public Answer giveAnswer(final String username, final String pollId, final String questionId,
                             final List<String> answerList) {
        final Answer answer = new Answer(answerList, questionId);
        PollResult pollResult = pollResultRepository.findPollResultByPollId(Long.valueOf(pollId));
        if (pollResult == null) {
            pollResult = new PollResult(Long.valueOf(pollId), username);
        }
        pollResult.getAnswerList().add(answer);
        pollResultRepository.save(pollResult);
        answerRepository.save(answer);
        return answer;
    }

    /**
     * This method deletes an selected answer.
     *
     * @param answerId the id of the selected answer
     */
    @Override
    public String deleteAnswer(final String answerId) {
        final String givenAnswerList = answerRepository.findById(Long.valueOf(answerId)).
            orElseThrow(EntityNotFoundException::new).getGivenAnswerList().toString();
        answerRepository.deleteById(Long.valueOf(answerId));
        return givenAnswerList;
    }

    /**
     * This method returns the requested answer.
     *
     * @param questionId the id of the requested answer
     * @return the requested answer
     */
    @Override
    public List<Answer> getAnswerFromOneQuestion(final Long questionId) {
        return answerRepository.findAnswerByQuestionId(questionId);
    }

    @Override
    public List<String> getAllAnswersFromPollByUser(final Long pollId, final String username) {
        return null;

    }
}
