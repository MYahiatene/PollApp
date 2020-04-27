package gpse.umfrato.domain.question;

public interface QuestionService {
    Question addQuestion(String questionMessage, String pollId);

    void removeQuestion(String pollId, String questionId);
}
