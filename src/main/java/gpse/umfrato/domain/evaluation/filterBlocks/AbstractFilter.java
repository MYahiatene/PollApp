package gpse.umfrato.domain.evaluation.filterBlocks;

import lombok.Data;
import lombok.NoArgsConstructor;

//@Entity
@Data
@NoArgsConstructor
public class AbstractFilter {
    /**First two lines are there for server side parsing*/
    public int xIndex;
    //public enum filterTypes {questionFilter, userFilter, statisticsFilter, logicFilter}
    /**That's the actual filter*/
    public String jsonString;




}
