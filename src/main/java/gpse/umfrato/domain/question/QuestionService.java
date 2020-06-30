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
    void removeQuestion(String categoryId, String questionId);

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

    void setNewAnswer(final Question question, final String answer);
}
