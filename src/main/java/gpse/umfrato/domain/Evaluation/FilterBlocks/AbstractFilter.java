package gpse.umfrato.domain.Evaluation.FilterBlocks;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;

//@Entity
@Data
@NoArgsConstructor
public class AbstractFilter {
    /**First two lines are there for server side parsing*/
    public int xIndex;
    public enum filterTypes {questionFilter, userFilter, statisticsFilter, logicFilter}
    /**That's the actual filter*/
    public String jsonString;




}
