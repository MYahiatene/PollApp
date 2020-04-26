package gpse.umfrato.domain.poll;


public interface PollService {
    Poll createPoll(String pollname);
    Poll getPoll(Long pollId);
}
