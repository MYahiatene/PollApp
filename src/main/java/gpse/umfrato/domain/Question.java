package gpse.umfrato.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
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
    private String question;

    @Column
    @Getter
    @Setter
    @OneToMany
    private List<Answer> answerList = new ArrayList<>();

    public List<Answer> getAnswerList() {
        return answerList;
    }

    protected Question() {

    }

    public Question(final String question) {
        this.question = question;
    }


}
