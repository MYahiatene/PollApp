package gpse.umfrato.domain.cmd;

import lombok.Data;

import java.util.List;

/**
 * The command design class for category.
 */
@Data
public class CategoryCmd {
    private String categoryName;
    private List<QuestionCmd> questionList;
    private Long pollId;
    private Long categoryId;
    private Long questionState;
}
