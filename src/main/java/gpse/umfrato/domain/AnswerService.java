package gpse.umfrato.domain;

public interface AnswerService {
    Answer createAnswer(final String answer, final User user, final String answertype);
}
