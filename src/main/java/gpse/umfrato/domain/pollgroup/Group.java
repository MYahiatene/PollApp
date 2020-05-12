package gpse.umfrato.domain.pollgroup;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gpse.umfrato.domain.poll.Poll;
import gpse.umfrato.domain.question.Question;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Group {


    /**
     * This attribute is an unique id from the object group.
     */
    @Id
    @GeneratedValue
    private long id;

    private String groupName;

    /**
     * This attribute represents the poll where the group is assigned.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private Poll poll;

    /**
     * This attribute represents a question list with all questions of this poll.
     */
    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL)
    private List<Question> questionList = new ArrayList<>();

    public Group(final String name) {
        this.groupName = name;
    }

}
