package gpse.umfrato.domain.question;

import java.util.List;

public interface QuestionService {

    /**
     * This method removes a selected question.
     *
     * @param pollId     the id of the poll where the question is setted
     * @param questionId the id of the selectes question
     */
    void removeQuestion(String pollId, String questionId);

    /**
     * This method returns the requested question.
     *
     * @param questionId the id of the requested question
     * @return the requested question
     */
    Question getQuestion(Long questionId);

    List<Question> getAllQuestions(final long categoryId);

    Question editQuestion(final Long questionID, final List<String> answerPossibilities, final int numberOfPossibleAnswers,
                      final String questionMessage,
                      final String questionType);


    Question changeCategory(final Long questionId, final Long oldCategoryId, final Long newCategoryId);

}
