package gpse.umfrato.domain.evaluation.filterblocks;

import lombok.Data;
import lombok.NoArgsConstructor;

//@Entity
@Data
@NoArgsConstructor
public class AbstractFilter {

    /**
     * That's the actual filter.
     */
    private String jsonString;

    /**
     * First two lines are there for server side parsing.
     */
    private int xIndex;
    //public enum filterTypes {questionFilter, userFilter, statisticsFilter, logicFilter}

}
