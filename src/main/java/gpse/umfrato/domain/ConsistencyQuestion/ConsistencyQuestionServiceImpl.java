package gpse.umfrato.domain.ConsistencyQuestion;

import gpse.umfrato.domain.answer.AnswerRepository;
import gpse.umfrato.domain.pollresult.PollResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
public class ConsistencyQuestionServiceImpl implements ConsistencyQuestionService {
    private final ConsistencyQuestionRepository consistencyQuestionRepository;

    @Autowired
    public ConsistencyQuestionServiceImpl(final ConsistencyQuestionRepository consistencyQuestionRepository) {
        this.consistencyQuestionRepository = consistencyQuestionRepository;
    }

    @Override
    public ConsistencyQuestion createConsistencyQuestion(Long pollId, Long question1Id, List<Long> answer1Ids, Long question2Id, List<Long> answer2Ids) {
        ConsistencyQuestion cq = new ConsistencyQuestion();
        cq.setPollId(pollId);
        cq.setQuestion1Id(question1Id);
        cq.setAnswer1Ids(answer1Ids);
        cq.setQuestion2Id(question2Id);
        cq.setAnswer2Ids(answer2Ids);
        return consistencyQuestionRepository.save(cq);
    }

    @Override
    public void deleteConsistencyQuestion(long consistencyQuestionId) {
        consistencyQuestionRepository.findById(consistencyQuestionId)
                .orElseThrow(EntityNotFoundException :: new);
        consistencyQuestionRepository.deleteById(consistencyQuestionId);
    }

    @Override
    public List<ConsistencyQuestion> getAllConsistencyQuestions(long pollId) {
        return consistencyQuestionRepository.findConsistencyQuestionsByPollId(pollId);
    }

    @Override
    public List<ConsistencyQuestion> getAllConsistencyQuestions(long question1Id, long question2Id) {
        return consistencyQuestionRepository.findConsistencyQuestionsByQuestion1IdOrQuestion2Id(question1Id,question2Id);
    }

    @Override
    public void editConsistencyQuestion(long consistencyQuestionId, Long pollId, Long question1Id, List<Long> answer1Ids, Long question2Id, List<Long> answer2Ids) {
        ConsistencyQuestion cq = null;
        cq = consistencyQuestionRepository.findById(consistencyQuestionId).orElseThrow(EntityNotFoundException::new);
        cq.setPollId(pollId);
        cq.setQuestion1Id(question1Id);
        cq.setAnswer1Ids(answer1Ids);
        cq.setQuestion2Id(question2Id);
        cq.setAnswer2Ids(answer2Ids);
        consistencyQuestionRepository.save(cq);
    }
}
