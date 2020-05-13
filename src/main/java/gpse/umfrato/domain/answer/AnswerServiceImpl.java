package gpse.umfrato.domain.answer;

import gpse.umfrato.domain.question.Question;
import gpse.umfrato.domain.question.QuestionRepository;
import gpse.umfrato.domain.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
public class AnswerServiceImpl implements AnswerService {

    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    /**
     * This class constructor initializes the answer- and question repository.
     *
     * @param answerRepository   the answer repository with answers
     * @param questionRepository the question repository with questions
     */
    @Autowired
    public AnswerServiceImpl(final AnswerRepository answerRepository, final QuestionRepository questionRepository,
                             final UserRepository userRepository) {
        this.answerRepository = answerRepository;
        this.questionRepository = questionRepository;
        this.userRepository = userRepository;
    }

    /**
     * This method adds an answer to the answer list of the question.
     *
     * @param questionId the id of the given question
     * @param username   name of the user creating the answer
     * @return the given answer
     */
    @Override
    public Answer giveAnswer(final String username,final String questionId, final List<String> answerList) {
        final Answer answer = new Answer(answerList,questionId);
        userRepository.findById(username).orElseThrow(()->new EntityNotFoundException()).getGivenAnswers().add(answer);
        answerRepository.save(answer);
        return answer;
    }

    /**
     * This method deletes an selected answer.
     *
     * @param questionId the id of the question containing the answer
     * @param answerId   the id of the selected answer
     */
    /*@Override
    public void deleteAnswer(final String username,final String questionId, final String answerId) {
        final Question question =
            questionRepository.findById(Long.valueOf(questionId)).orElseThrow(() -> new EntityNotFoundException());
        final List<Answer> answerList = question.getGivenAnswers();
        answerList.removeIf(obj -> obj.getId() == Long.valueOf(answerId));
    }*/

    /**
     * This method returns the requested answer.
     *
     * @param questionId the id of the requested answer
     * @return the requested answer
     */
    @Override
    public List<Answer> getAnswerFromOneQuestion(Long questionId) {
        return answerRepository.findAnswerByQuestionId(questionId);
    }
}
