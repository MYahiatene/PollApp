package gpse.umfrato.domain.answer;

public interface AnswerService {
    Answer addAnswer(final String message, final String questionId);

    void deleteAnswer(final String questionId, final String answerId);
}
