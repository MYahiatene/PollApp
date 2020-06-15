package gpse.umfrato.domain.cmd;

import lombok.Data;

@Data
public class QuestionCategoryChangeCmd {

    private Long questionId;
    private Long oldCategoryId;
    private Long newCategoryId;
}
