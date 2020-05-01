package gpse.umfrato;

import gpse.umfrato.domain.Evaluation.Statistics;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class StatisticUnitTest {

    @Test
    public void trueTest() {
        Assertions.assertThat(true).isTrue();
    }


    @Test
    public void normalizeOneValueTest() {
        double expected = 0.2;
        double actual;
        try {
            actual = Statistics.normalizeOneValue(2, 10);
        }
        catch (Exception e){
            actual = 0;
        }
        assertEquals(expected, actual);
    }

    @Test
    public void normalizeAllDoubleValueTest() {

        List<Double> expected = new ArrayList<>();
        expected.add(0.2);
        expected.add(0.5);
        expected.add(0.3);

        List<Double> absoluteValues = new ArrayList<>();
        absoluteValues.add(2.0);
        absoluteValues.add(5.0);
        absoluteValues.add(3.0);



        List<Double> actual = Statistics.normalizeAllDoubleValues(absoluteValues);

        assertEquals(expected, actual);
    }


    @Test
    public void normalizeAllIntegerValueTest() {

        List<Double> expected = new ArrayList<>();
        expected.add(0.2);
        expected.add(0.5);
        expected.add(0.3);

        List<Integer> absoluteValues = new ArrayList<>();
        absoluteValues.add(2);
        absoluteValues.add(5);
        absoluteValues.add(3);



        List<Double> actual = Statistics.normalizeAllIntegerValues(absoluteValues);

        assertEquals(expected, actual);
    }

}
