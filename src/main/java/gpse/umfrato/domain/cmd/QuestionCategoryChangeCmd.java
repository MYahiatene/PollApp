package gpse.umfrato.domain.cmd;

import lombok.Data;

/**
 * The command design class for the category change of questions.
 */
@Data
public class QuestionCategoryChangeCmd {

    private String questionId;
    private String newIndex;
    private String newCategoryId;
}
