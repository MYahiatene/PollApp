package gpse.umfrato.domain.answer.poll;


public interface PollService {
    Poll createPoll(String pollname);
    Poll getPoll(Long pollId);
}
