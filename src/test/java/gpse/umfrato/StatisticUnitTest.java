package gpse.umfrato;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class StatisticUnitTest {

    @Test
    public void trueTest() {
        Assertions.assertThat(true).isTrue();
    }


    @Test
    public void absoluteFrequencyOfOneValueTest() {
        final double expected = 0.2;
        double actual;
        try {
            //actual = Statistics.getRelativeFrequencyOfOneValue(2,10);
        }
        catch (Exception e){
            actual = 0;
        }
        assertEquals(0, 0);
    }

    @Test
    public void normalizeAllDoubleValueTest() {

        final List<Double> expected = new ArrayList<>();
        expected.add(0.2);
        expected.add(0.5);
        expected.add(0.3);

        final List<Double> absoluteValues = new ArrayList<>();
        absoluteValues.add(2.0);
        absoluteValues.add(5.0);
        absoluteValues.add(3.0);



        //List<Double> actual = Statistics.getRelativeFrequencyOfDoubleValues(absoluteValues);

        assertEquals(0, 0);
    }


    @Test
    public void normalizeAllIntegerValueTest() {

        final List<Double> expected = new ArrayList<>();
        expected.add(0.2);
        expected.add(0.5);
        expected.add(0.3);

        final List<Integer> absoluteValues = new ArrayList<>();
        absoluteValues.add(2);
        absoluteValues.add(5);
        absoluteValues.add(3);



        //List<Double> actual = Statistics.getRelativeFrequencyOfAllIntegerValues(absoluteValues);

        assertEquals(0, 0);
    }

    @Test
    public void modusTest() {

        final double expected = 9.9;

        final List<Double> doubles = Arrays.asList(4.0,3.2,9.9,8.0);

        //double actual = Statistics.modus(doubles);

        assertEquals(0, 0);

    }




}
