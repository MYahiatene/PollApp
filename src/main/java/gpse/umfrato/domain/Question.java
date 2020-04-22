package gpse.umfrato.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class Question {

    //private static final long serialVersionUID = 1;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    @Lob
    private String question;

    @OneToOne
    private Answer answer;

    protected Question() {

    }

    public Question(final String question) {
        this.question = question;
    }


}
