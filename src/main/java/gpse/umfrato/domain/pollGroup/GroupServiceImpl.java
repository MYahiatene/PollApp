package gpse.umfrato.domain.pollGroup;

import gpse.umfrato.domain.poll.PollRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GroupServiceImpl implements GroupService {
    private final GroupRepository groupRepository;
    private final PollRepository pollRepository;

    @Autowired
    public GroupServiceImpl(final GroupRepository groupRepository,final PollRepository pollRepository) {
        this.groupRepository = groupRepository;
        this.pollRepository = pollRepository;
    }

    @Override
    public Group addGroup(final String name, final long pollId) {
        Group group = new Group(name);
        group.setPoll(pollRepository.getOne(pollId));
        groupRepository.save(group);
        return group;
    }


}
