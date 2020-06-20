package gpse.umfrato.domain.ConsistencyQuestion;

import gpse.umfrato.domain.category.Category;

import java.util.List;

public interface ConsistencyQuestionService {
    ConsistencyQuestion createConsistencyQuestion(final Long pollId, final Long question1Id, final List<Long> answer1Ids, final Long question2Id, final List<Long> answer2Ids);

    void deleteConsistencyQuestion(final long consistencyQuestionId);

    List<ConsistencyQuestion> getAllConsistencyQuestions(final long pollId);

    List<ConsistencyQuestion> getAllConsistencyQuestions(long question1Id, long question2Id);

    void editConsistencyQuestion(final long consistencyQuestionId, final Long pollId, final Long question1Id, final List<Long> answer1Ids, final Long question2Id, final List<Long> answer2Ids);
}
