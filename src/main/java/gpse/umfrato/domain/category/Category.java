package gpse.umfrato.domain.category;

import gpse.umfrato.domain.question.Question;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Category {


    /**
     * This attribute is an unique id from the object group.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long categoryId;

    private String categoryName;

    /**
     * This attribute represents the poll where the group is assigned.
     */
    private Long pollId;

    /**
     * This attribute represents a question list with all questions of this poll.
     */
    @OneToMany/*(cascade = CascadeType.ALL, fetch = FetchType.EAGER)*/
    private List<Question> questionList = new ArrayList<>();

    public Category(final String name, final Long pollId) {
        this.categoryName = name;
        this.pollId = pollId;
    }

}
