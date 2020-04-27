package gpse.umfrato.domain.poll;

import gpse.umfrato.domain.question.Question;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * This Object represents a poll.
 */
@Entity
@Data
@NoArgsConstructor
public class Poll {

    /**
     * This attribute is an unique id from the object poll.
     */
    @Id
    @GeneratedValue
    private Long id;

    /**
     * This attribute represents the creator of the poll.
     */
    private String pollcreator;

    /**
     * This attribute represents the date when the poll was created.
     */
    private LocalDateTime pollCreatedAt;

    /**
     * This attribute represents the date when the poll was edited the last time.
     */
    private LocalDateTime lastEditAt;

    /**
     * This attribute represents the date when the poll was activated.
     */
    // use Instant instead , cause of time zone
    private LocalDateTime activatedAt;

    /**
     * This attribute represents the date when the poll was deactivated.
     */
    private LocalDateTime deactivatedAt;

    /**
     * This attribute represents the status of the anonymity if it is anonymous, part-anonymous
     * or not anonymous.
     */
    private String anonymityStatus;

    /**
     * This attribute represents the name of the poll.
     */
    private String pollname;

    /**
     * This attribute represents the status of the poll if it is activated or deactivated.
     */
    private int pollStatus;

    /**
     * This attribute represents a question list with all questions of this poll.
     */
    @OneToMany(cascade = CascadeType.ALL)
    private List<Question> questionList = new ArrayList<>();

    /**
     * This constructor receives a poll name and saves in the poll object.
     *
     * @param pollName
     */
    public Poll(final String pollName) {
        this.pollname = pollName;
    }

}
