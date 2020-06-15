package gpse.umfrato.domain.question;

import gpse.umfrato.domain.cmd.QuestionCmd;

import java.util.List;

public interface QuestionService {

    /**
     * This method removes a selected question.
     *
     * @param categoryId     the id of the category where the question is setted
     * @param questionId the id of the selectes question
     */
    void removeQuestion(String categoryId, String questionId);

    /**
     * This method returns the requested question.
     *
     * @param questionId the id of the requested question
     * @return the requested question
     */

    Question getQuestion(Long questionId);

    Question addQuestion(final QuestionCmd questionCmd);

    List<Question> getAllQuestions(final long categoryId);

    Question editQuestion(QuestionCmd questionCmd);


    Question changeCategory(final Long questionId, final Long oldCategoryId, final Long newCategoryId);

}
