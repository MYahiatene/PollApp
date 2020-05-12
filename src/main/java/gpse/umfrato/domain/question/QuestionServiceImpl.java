package gpse.umfrato.domain.question;

import gpse.umfrato.domain.poll.Poll;
import gpse.umfrato.domain.poll.PollRepository;
import gpse.umfrato.domain.poll.PollService;
import gpse.umfrato.domain.pollgroup.Group;
import gpse.umfrato.domain.pollgroup.GroupRepository;
import gpse.umfrato.web.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionServiceImpl implements QuestionService {

    /**
     * Initializes the poll repository.
     */
    private final PollRepository pollRepository;

    /**
     * Initializes the question repository.
     */
    private final QuestionRepository questionRepository;

    /**
     * Initializes the poll service.
     */
    final PollService pollService;

    final GroupRepository groupRepository;

    /**
     * This class constructor initializes the poll-, question repository and the poll service.
     *
     * @param pollRepository     the repository for the polls
     * @param questionRepository the repository for the questions
     * @param pollService        the class to work with polls
     * @param groupRepository    the repository for the groups
     */
    @Autowired
    public QuestionServiceImpl(final PollRepository pollRepository, final QuestionRepository questionRepository,
                               final PollService pollService, final GroupRepository groupRepository) {
        this.pollRepository = pollRepository;
        this.questionRepository = questionRepository;
        this.pollService = pollService;
        this.groupRepository = groupRepository;
    }


    /**
     * This method creates a question for a poll.
     *
     * @param questionMessage the given question
     * @return the question which is created
     */
    @Override
    public Question addQuestion(final String questionMessage, final long pollId,
                                final List<String> answerPossibilities,
                                final String questionType) {
        final Question question = new Question(questionMessage, answerPossibilities, questionType);
        pollRepository.findById(pollId).get().getGroupList().get(0).getQuestionList().add(question);
        question.setGroup(pollRepository.findById(pollId).get().getGroupList().get(0));
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
        try {
            questionRepository.deleteById(Long.valueOf(questionId));

        } catch (EntityNotFoundException e) {
            e.printStackTrace();
        }
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
    public List<Question> getAllQuestions(final long pollId) {
        final Poll poll = pollRepository.findById(pollId).orElseThrow(() -> new EntityNotFoundException());
        final List<Group> groups = poll.getGroupList();
        final List<Question> allQuestions = new ArrayList<>();

        for (final Group g : groups) {
            for (final Question q : g.getQuestionList()) {
                allQuestions.add(q);
            }
        }
        if (allQuestions.isEmpty()) {
            throw new BadRequestException();
        }

        return allQuestions;
    }

    @Override
    public List<Question> getQuestionsFromGroup(final long groupId) {
        return questionRepository.findQuestionByGroupId(groupId);
    }


}
