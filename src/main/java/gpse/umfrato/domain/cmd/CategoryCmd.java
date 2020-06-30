package gpse.umfrato.domain.cmd;

import lombok.Data;

/**
 * The command design class for category.
 */
@Data
public class CategoryCmd {
    private String categoryName;
    private String pollId;
    private String categoryId;
    private String questionState;
}
