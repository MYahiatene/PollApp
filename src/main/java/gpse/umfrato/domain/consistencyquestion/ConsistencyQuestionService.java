package gpse.umfrato.domain.consistencyquestion;

import gpse.umfrato.domain.cmd.ConsistencyQuestionCmd;

import java.util.List;

public interface ConsistencyQuestionService {
    ConsistencyQuestion createConsistencyQuestion(ConsistencyQuestionCmd consistencyQuestionCmd);

    void deleteConsistencyQuestion(final long consistencyQuestionId);

    List<ConsistencyQuestion> getAllConsistencyQuestions(final long pollId);

    List<ConsistencyQuestion> getAllConsistencyQuestions(final long question1Id, final long question2Id);

    void editConsistencyQuestion(final long consistencyQuestionId, final ConsistencyQuestionCmd consistencyQuestionCmd);
}
