package gpse.umfrato.domain;

public interface QuestionService {
    Question createQuestion(String question);

    Answer createAnswer(final String answer, final User user, final String answertype);

    void deleteQuestion();

}
