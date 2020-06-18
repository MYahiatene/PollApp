package gpse.umfrato.domain.cmd;

import lombok.Data;

@Data
public class CategoryCmd {
    private String name;
    private Long pollId;
    private Long categoryId;
}
