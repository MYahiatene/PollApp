package gpse.umfrato.domain.mail;

import gpse.umfrato.domain.user.User;

import java.util.List;

public interface MailService {

    int getPollId();

    List<User> getUserList(int pollId);
}
