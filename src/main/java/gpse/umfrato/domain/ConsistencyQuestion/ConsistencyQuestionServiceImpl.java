package gpse.umfrato.domain.ConsistencyQuestion;

import gpse.umfrato.domain.answer.AnswerRepository;
import gpse.umfrato.domain.cmd.ConsistencyQuestionCmd;
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
    public ConsistencyQuestion createConsistencyQuestion(ConsistencyQuestionCmd consistencyQuestionCmd) {
        ConsistencyQuestion cq = new ConsistencyQuestion();
        cq.setPollId(consistencyQuestionCmd.getPollId());
        cq.setQuestion1Id(consistencyQuestionCmd.getQuestion1Id());
        cq.setAnswer1Ids(consistencyQuestionCmd.getAnswer1Ids());
        cq.setQuestion2Id(consistencyQuestionCmd.getQuestion2Id());
        cq.setAnswer2Ids(consistencyQuestionCmd.getAnswer2Ids());
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
    public List<ConsistencyQuestion> getAllConsistencyQuestions(final long question1Id, final long question2Id) {
        return consistencyQuestionRepository.findConsistencyQuestionsByQuestion1IdOrQuestion2Id(question1Id,question2Id);
    }

    @Override
    public void editConsistencyQuestion(long consistencyQuestionId, final ConsistencyQuestionCmd consistencyQuestionCmd) {
        ConsistencyQuestion cq = null;
        cq = consistencyQuestionRepository.findById(consistencyQuestionId).orElseThrow(EntityNotFoundException::new);
        cq.setPollId(consistencyQuestionCmd.getPollId());
        cq.setQuestion1Id(consistencyQuestionCmd.getQuestion1Id());
        cq.setAnswer1Ids(consistencyQuestionCmd.getAnswer1Ids());
        cq.setQuestion2Id(consistencyQuestionCmd.getQuestion2Id());
        cq.setAnswer2Ids(consistencyQuestionCmd.getAnswer2Ids());
        consistencyQuestionRepository.save(cq);
    }
}
