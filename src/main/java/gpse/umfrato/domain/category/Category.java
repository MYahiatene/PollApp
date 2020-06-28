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

    /**
     * This attribute represents the name of the category.
     */
    private String categoryName;

    /**
     * This attribute represents the poll where the group is assigned.
     */
    private Long pollId;

    /**
     * This attribute represents a question list with all questions of this poll.
     */
    @Column(unique = true)
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Question> questionList = new ArrayList<>();

    /**
     * The constructor of the class category.
     * @param name the name of the category
     * @param pollId the poll id of the category
     */
    public Category(final String name, final Long pollId) {
        this.categoryName = name;
        this.pollId = pollId;
    }

}
