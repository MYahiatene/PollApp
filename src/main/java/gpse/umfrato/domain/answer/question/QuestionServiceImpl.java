package gpse.umfrato.domain.answer.question;

import gpse.umfrato.domain.answer.poll.Poll;
import gpse.umfrato.domain.answer.poll.PollRepository;
import gpse.umfrato.domain.answer.user.User;
import gpse.umfrato.domain.answer.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class QuestionServiceImpl implements QuestionService {

    private final PollRepository pollRepository;
    private final QuestionRepository questionRepository;

    @Autowired
    public QuestionServiceImpl(final PollRepository pollRepository, final QuestionRepository questionRepository) {
        this.pollRepository = pollRepository;
        this.questionRepository = questionRepository;
    }

    @Override
    public Question createQuestion(String message, Long pollId) {

        Question question = new Question(message);
        Optional<Poll> pollOptional = pollRepository.findById(pollId);
        Poll poll = pollOptional.get();
        poll.getQuestionList().add(question);
        pollRepository.save(poll);

        return question;
    }

    @Override
    public Answer createAnswer(String answer, User user, String answertype, Long questionId) {

        Optional<Question> tmp = questionRepository.findById(questionId);
        Question questionByAnswer = tmp.get();
        Answer ans = new Answer(answer, user, answertype, questionByAnswer);
        questionByAnswer.getAnswerList().add(ans);
        questionRepository.save(questionByAnswer);
        return ans;
    }


    @Override
    public void deleteQuestion() {

    }
}
