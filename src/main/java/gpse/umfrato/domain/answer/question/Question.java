package gpse.umfrato.domain.answer.question;

import gpse.umfrato.domain.answer.Answer;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
public class Question {

    //private static final long serialVersionUID = 1;

    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    @Lob
    @Getter
    private String question;

    @Column
    @Getter
    @Setter
    @OneToMany(cascade = CascadeType.ALL)
    private List<Answer> answerList;



    protected Question() {

    }

    public Question(final String question) {
        this.question = question;
    }


}
