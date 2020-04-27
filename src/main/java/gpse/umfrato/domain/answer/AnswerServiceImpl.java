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

    /**
     * This class constructor initializes the answer- and question repository.
     *
     * @param answerRepository   the answer repository with answers
     * @param questionRepository the question repository with questions
     */
    public AnswerServiceImpl(final AnswerRepository answerRepository, final QuestionRepository questionRepository) {
        this.answerRepository = answerRepository;
        this.questionRepository = questionRepository;
    }

    /**
     * This method adds an answer to the answer list of the question.
     *
     * @param message    the answer from the user
     * @param questionId the id of the given question
     * @param answerType type of given answer
     * @param username   name of the user creating the answer
     * @return the given answer
     */
    @Override
    public Answer addAnswer(final String message, final String questionId, final String answerType, final String
        username) {
        final Question question =
            questionRepository.findById(Long.valueOf(questionId)).orElseThrow(() -> new EntityNotFoundException());
        final Answer answer = new Answer(message, questionId, answerType, username);
        question.getAnswerList().add(answer);
        questionRepository.save(question);
        return answer;
    }

    /**
     * This method deletes an selected answer.
     *
     * @param questionId the id of the question containing the answer
     * @param answerId   the id of the selected answer
     */
    @Override
    public void deleteAnswer(final String questionId, final String answerId) {
        final Question question =
            questionRepository.findById(Long.valueOf(questionId)).orElseThrow(() -> new EntityNotFoundException());
        final List<Answer> answerList = question.getAnswerList();
        answerList.removeIf(obj -> obj.getId() == Long.valueOf(answerId));
        questionRepository.save(question);
    }

    /**
     * This method returns the requested answer.
     *
     * @param answerId the id of the requested answer
     * @return the requested answer
     */
    @Override
    public Answer getAnswer(final Long answerId) {
        final Question question = questionRepository.findById(answerRepository.findById(answerId).orElseThrow(() ->
            new EntityNotFoundException()).getQuestionId()).orElseThrow(() -> new EntityNotFoundException());

        final List<Answer> answerList = question.getAnswerList();

        return answerList.stream().filter(o -> o.getId() == answerId).findFirst().get();
    }

    /**
     * This method returns all answers from a selected question.
     *
     * @param questionId the id of the selected question
     * @return all answers from a question in a list
     */
    @Override
    public List<Answer> getAllAnswers(final String questionId) {
        final List<Answer> answers = questionRepository.findById(Long.valueOf(questionId)).orElseThrow(() -> new
            EntityNotFoundException()).getAnswerList();

        return answers;
    }
}
