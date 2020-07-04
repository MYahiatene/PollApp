package gpse.umfrato.domain.question;

import gpse.umfrato.domain.cmd.QuestionCmd;

import java.util.List;

public interface QuestionService {

    /**
     * This method removes a selected question.
     *
     * @param categoryId the id of the category where the question is set
     * @param questionId the id of the selected question
     */
    void removeQuestion(Long categoryId, Long questionId);

    /**
     * This method returns the requested question.
     *
     * @param questionId the id of the requested question
     * @return the requested question
     */

    Question getQuestion(Long questionId);

    /**
     * This method adds a question.
     * @param questionCmd the Cmd which includes the necessary details
     * @return returns the question object
     */
    Question addQuestion(final QuestionCmd questionCmd);

    /**
     * This method returns all questions with the categoryId.
     *
     * @param categoryId the id of the category
     * @return All the questions
     */
    List<Question> getAllQuestions(final long categoryId);

    // todo: muss die mit rein?
    Question addQuestion(final Long pollId, final Question question);


    /**
     * This method edits a question.
     * @param questionCmd the Cmd which includes the necessary details
     * @return returns the edited question object
     */
    Question editQuestion(QuestionCmd questionCmd);

    /**
     * This method changes the category of a question.
     * @param questionId the id of the question
     * @param newCategoryId the new category id of the question
     * @param newIndex the new index in the question list of the question
     * @return returns the question
     */
    Question changeCategory(final Long questionId, final Long newCategoryId, final Long newIndex);

    /**
     * Adds a new answerPossibility to the List of answerPossibilities of the given question.
     * @param question
     * @param answer
     */
    void setNewAnswer(final Question question, final String answer);

    /**
     * Copies a choiceQuestion to a category. Needs to adds every answer separately to the new question.
     * @param question
     * @param pollId
     * @return
     */
    Question addChoiceQuestion(final Question question, final Long pollId);

    /**
     * Copies all questions from one category.
     * @param categoryId
     * @param pollId
     * @param questions
     */
    void copyQuestions(final Long categoryId, final Long pollId, final List<Question> questions);

}
