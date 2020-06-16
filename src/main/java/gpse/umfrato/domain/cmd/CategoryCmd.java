package gpse.umfrato.domain.cmd;

import lombok.Data;

@Data
public class CategoryCmd {
    private String categoryName;
    private String pollId;
    private String categoryId;
}
