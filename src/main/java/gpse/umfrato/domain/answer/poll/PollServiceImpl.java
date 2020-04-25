package gpse.umfrato.domain.answer.poll;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
class PollServiceImpl implements PollService {

    private final PollRepository pollRepository;

    @Autowired
    public PollServiceImpl(final PollRepository pollRepository) {
        this.pollRepository = pollRepository;
    }

    @Override
    public Poll createPoll(final String pollname) {
        final Poll poll = new Poll(pollname);

        pollRepository.save(poll);

        return poll;
    }

    @Override
    public Poll getPoll(Long pollId) {
        return pollRepository.findById(pollId).get();
    }
}
