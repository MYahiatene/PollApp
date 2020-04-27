package gpse.umfrato.domain.answer;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;


    private String answerType;

    @Lob
    private String answer;

    private String username;

    private Long questionId;


    public Answer(final String answer, final String username, final String answerType, Long questionId) {
        this.answer = answer;
        this.username = username;
        this.answerType = answerType;
        this.questionId = questionId;
    }


}
