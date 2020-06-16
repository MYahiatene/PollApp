package gpse.umfrato.domain.cmd;

import lombok.Data;

@Data
public class QuestionCategoryChangeCmd {

    private String questionId;
    private String newIndex;
    private String newCategoryId;
}
