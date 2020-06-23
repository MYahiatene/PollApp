package gpse.umfrato.domain.answer;

import gpse.umfrato.domain.pollresult.PollResult;
import gpse.umfrato.domain.pollresult.PollResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

@Service public class AnswerServiceImpl implements AnswerService {

    private static final Logger LOGGER = Logger.getLogger("AnswerServiceImpl");
    private final AnswerRepository answerRepository;
    private final PollResultRepository pollResultRepository;

    /**
     * This class constructor initializes the answer- and question repository.
     *
     * @param pollResultRepository the repository where the pollResults are saved
     * @param answerRepository     the answer repository with answers
     */
    @Autowired public AnswerServiceImpl(final AnswerRepository answerRepository,
                                        final PollResultRepository pollResultRepository) {
        this.answerRepository = answerRepository;
        this.pollResultRepository = pollResultRepository;
    }

    /**
     * This method adds an answer to the answer list of the question.
     *
     * @param questionId the id of the given question
     * @param username   name of the user creating the answer
     * @return the given answer
     */
    @Override public Answer giveAnswer(final String username, final Long pollId, final String questionId,
                                       final List<String> answerList) {
        final Answer answer = new Answer(answerList, questionId);
        // LOGGER.info(answer.toString());
        PollResult pollResult = pollResultRepository.findPollResultByPollIdAndPollTaker(pollId, username);
        if (pollResult == null) {
            pollResult = new PollResult(pollId, username);
        }
        int i = 0;
        boolean answerDa = false;
        for(Answer a:pollResult.getAnswerList())
        {
            if(a.getQuestionId().equals(answer.getQuestionId()))
            {
                pollResult.getAnswerList().set(i, answer);
                answerDa = true;
                break;
            }
            i++;
        }
        if(!answerDa)
        {
            pollResult.getAnswerList().add(answer);
        }
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        Date dateobj = new Date();
        pollResult.setLastEditAt(df.format(dateobj));
        // LOGGER.info(pollResult.toString());
        pollResultRepository.save(pollResult);
        return answer;
    }

    /**
     * This method deletes an selected answer.
     *
     * @param answerId the id of the selected answer
     */
    @Override public String deleteAnswer(final String answerId) {
        final String givenAnswerList = answerRepository.findById(Long.valueOf(answerId))
                .orElseThrow(EntityNotFoundException :: new).getGivenAnswerList().toString();
        answerRepository.deleteById(Long.valueOf(answerId));
        return givenAnswerList;
    }

    /**
     * This method returns the requested answer.
     *
     * @param questionId the id of the requested answer
     * @return the requested answer
     */
    @Override public List<Answer> getAnswerFromOneQuestion(final Long questionId) {
        return answerRepository.findAnswerByQuestionId(questionId);
    }

    @Override public List<String> getAllAnswersFromPollByUser(final Long pollId, final String username) {
        return null;
    }
}
