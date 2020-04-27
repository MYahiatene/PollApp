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
    private Long id;


    private String answerType;

    @Lob
    private String answer;

    private String username;

    private Long questionId;


    public Answer(final String message, final String questionId, final String answerType, final String username) {
        this.answer = message;
        this.username = username;
        this.answerType = answerType;
        this.questionId = Long.valueOf(questionId);
    }


}
