package gpse.umfrato.domain.Evaluation;

import java.util.ArrayList;
import java.util.List;

public class Statistics {

    /**
     * This method takes a absolute value and converts it to a relative value
     * @param value absolute value
     * @param totalNumber total number of values
     * @return relative value
     */
    public static double normalizeOneValue (double value, double totalNumber) throws Exception {
        if(totalNumber<value){
            throw new Exception("totalNumber must be larger than value!");
        }
        return value / totalNumber;
    }

    /**
     * This method takes a List of double values that represent an absolute number
     * and converts each of them into the corresponding relative value
     * @param values all absolute values that were given in a poll
     * @return a list of relative values
     */

    public static List<Double> normalizeAllDoubleValues(List< Double> values){
        double totalNumber = 0;
        for (int i = 0; i < values.size(); i++) {
            totalNumber += values.get(i);
        }

        List<Double> listOfNormalizedValues = new ArrayList<>();

        for (int i = 0; i < values.size() ; i++) {
            try {
                listOfNormalizedValues.add(normalizeOneValue(values.get(i), totalNumber));
            }
            catch (Exception e){
                return null;
            }

        }

        return listOfNormalizedValues;
    }

    /**
     * This method uses normalizeAllDoubleValues to convert absolute numbers into relative numbers.
     * It casts the Integer values into double values first in order to use the method.
     * @param values a list of absolute integer values
     * @return a list of corresponding relative double values.
     */

    public static List<Double> normalizeAllIntegerValues(List<Integer> values){

        List<Double> listOfDoubleValues = new ArrayList<>();

        for (int i = 0; i < values.size() ; i++) {
            listOfDoubleValues.add((double) values.get(i));
        }

        return normalizeAllDoubleValues(listOfDoubleValues);
    }


}
