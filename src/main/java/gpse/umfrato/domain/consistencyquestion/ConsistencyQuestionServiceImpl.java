package gpse.umfrato.domain.consistencyquestion;

import gpse.umfrato.domain.cmd.ConsistencyQuestionCmd;
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
    public ConsistencyQuestion createConsistencyQuestion(final ConsistencyQuestionCmd consistencyQuestionCmd) {
        final ConsistencyQuestion cq = new ConsistencyQuestion();
        cqCmdToCq(consistencyQuestionCmd, cq);
        return consistencyQuestionRepository.save(cq);
    }

    @Override
    public void deleteConsistencyQuestion(final long consistencyQuestionId) {
        consistencyQuestionRepository.findById(consistencyQuestionId).orElseThrow(EntityNotFoundException :: new);
        consistencyQuestionRepository.deleteById(consistencyQuestionId);
    }

    @Override
    public List<ConsistencyQuestion> getAllConsistencyQuestions(final long pollId) {
        return consistencyQuestionRepository.findConsistencyQuestionsByPollId(pollId);
    }

    @Override
    public List<ConsistencyQuestion> getAllConsistencyQuestions(final long question1Id, final long question2Id) {
        return consistencyQuestionRepository.findConsistencyQuestionsByQuestion1IdOrQuestion2Id(question1Id, question2Id);
    }

    @Override
    public void editConsistencyQuestion(final long consistencyQuestionId, final ConsistencyQuestionCmd consistencyQuestionCmd) {
        ConsistencyQuestion cq;
        cq = consistencyQuestionRepository.findById(consistencyQuestionId).orElseThrow(EntityNotFoundException::new);
        cqCmdToCq(consistencyQuestionCmd, cq);
        consistencyQuestionRepository.save(cq);
    }

    private static void cqCmdToCq(final ConsistencyQuestionCmd consistencyQuestionCmd, final ConsistencyQuestion cq) {
        cq.setPollId(consistencyQuestionCmd.getPollId());
        cq.setQuestion1Id(consistencyQuestionCmd.getQuestion1Id());
        cq.setQuestion1Slider(consistencyQuestionCmd.getQuestion1Slider());
        cq.setAnswer1Indices(consistencyQuestionCmd.getAnswer1Indices());
        cq.setQuestion2Id(consistencyQuestionCmd.getQuestion2Id());
        cq.setQuestion2Slider(consistencyQuestionCmd.getQuestion2Slider());
        cq.setAnswer2Indices(consistencyQuestionCmd.getAnswer2Indices());
    }
}
