package gpse.umfrato.domain.poll;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    public List<Poll> getAllPolls() {
        final List<Poll> poll = new ArrayList<>();

        pollRepository.findAll().forEach(poll::add);

        return poll;
    }

    @Override
    public Optional<Poll> getPoll(String id) {
        final Long pollId = Long.valueOf(id);
        return pollRepository.findById(pollId);
    }
}
