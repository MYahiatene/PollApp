package gpse.umfrato.domain.answer;

import gpse.umfrato.domain.poll.PollRepository;
import gpse.umfrato.domain.pollresult.PollResult;
import gpse.umfrato.domain.pollresult.PollResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Service
public class AnswerServiceImpl implements AnswerService {

    private final AnswerRepository answerRepository;
    private final PollRepository pollRepository;
    private final PollResultRepository pollResultRepository;

    /**
     * This class constructor initializes the answer- and question repository.
     *
     * @param pollResultRepository the repository where the pollResults are saved
     * @param answerRepository     the answer repository with answers
     */
    @Autowired
    public AnswerServiceImpl(final AnswerRepository answerRepository, PollRepository pollRepository,
                                        final PollResultRepository pollResultRepository) {
        this.answerRepository = answerRepository;
        this.pollRepository = pollRepository;
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
    public Answer giveAnswer(final String username, final Long pollId, final Long questionId,
                                       final List<String> answerList) {
        if(pollRepository.getOne(pollId).getPollStatus() == 2) {
            final Answer answer = new Answer(answerList, questionId);
            PollResult pollResult = pollResultRepository.findPollResultByPollIdAndPollTaker(pollId, username);
            if (pollResult == null) {
                pollResult = new PollResult(pollId, username);
            }
            int idx = 0;
            boolean answerDa = false;
            for (final Answer a: pollResult.getAnswerList()) {
                if (a.getQuestionId().equals(answer.getQuestionId())) {
                    pollResult.getAnswerList().set(idx, answer);
                    answerDa = true;
                    break;
                }
                idx++;
            }
            if (!answerDa) {
                pollResult.getAnswerList().add(answer);
            }
            pollResult.setLastEditAt(ZonedDateTime.now());
            // todo: check line below
            answerRepository.save(answer);
            pollResultRepository.save(pollResult);
            return answer;
        }
        return null;
    }

    /**
     * This method deletes an selected answer.
     *
     * @param answerId the id of the selected answer
     */
    @Override
    public String deleteAnswer(final Long answerId) {
        final String givenAnswerList = answerRepository.findById(answerId)
                .orElseThrow(EntityNotFoundException :: new).getGivenAnswerList().toString();
        answerRepository.deleteById(answerId);
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
    /**
     * This method returns all answers from a selected question.
     *
     * @param pollId the id of the poll
     * @param username the id of the user
     * @return all answers from a question in a list
     */
    @Override
    public List<String> getAllAnswersFromPollByUser(final Long pollId, final String username) {
        return null;
    }
}
