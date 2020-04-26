package gpse.umfrato.domain.question;

import gpse.umfrato.domain.poll.Poll;
import gpse.umfrato.domain.poll.PollRepository;
import gpse.umfrato.domain.poll.PollService;
import gpse.umfrato.domain.user.User;
import gpse.umfrato.domain.answer.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
    public Question createQuestion(String message, Long pollId) {

        Question question = new Question(message);
        Poll poll = pollService.getPoll(pollId);
        poll.getQuestionList().add(question);
        return question;
    }


    @Override
    public void deleteQuestion() {

    }
}
