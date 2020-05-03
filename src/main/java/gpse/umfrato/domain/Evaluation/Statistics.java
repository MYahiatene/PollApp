package gpse.umfrato.domain.Evaluation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
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

    /**
     * This function returns the modus of a list of absolute values
     * @param allValues absolute values
     * @return the highest value
     */

    public static double modus(List<Double> allValues){

        double currentHighest = 0.0;

        for (int i = 0; i < allValues.size(); i++) {
            if(allValues.get(i)>currentHighest){
                currentHighest = allValues.get(i);
            }
        }

        return currentHighest;

    }

    /**
     * This function casts the provided value to an integer and limits it to a range of zero to max minus one
     * to avoid out of range exceptions while accessing arrays or lists.
     * @param val the value to limit or the possibly out of bounds index to access.
     * @param max one above the biggest value val can be or the size of the array to access.
     * @return the constricted integer.
     */
    private static <T extends Number> int constrict(T val, int max)
    {
        max -= 1;
        int casted = val.intValue();
        if(casted < 0)
        {
            return 0;
        }
        if(casted > max)
        {
            return max;
        }
        return casted;
    }

    /**
     * This function returns the p-Quantile of the provided values.
     * If the list is empty the function returns null.
     * If p is below zero or above one it will be set to zero or one respectively.
     * @param values list of values to pick the quantile from. The values will be sorted inside the function.
     * @param p the parameter to calculate the quantile for example p=0.5 equals the median and p=1 equals the maximum.
     * @return the value corresponding to the p.
     **/
    public static Double pQuantile(List<Double> values, double p)
    {
        if(values.isEmpty())
        {
            return null;
        }
        if(p < 0.0)
        {
            p = 0.0;
        }
        else if(p > 1.0)
        {
            p = 1.0;
        }
        Collections.sort(values);
        int n = values.size();
        double Xnp = 0.0;
        double Xnp1 = values.get(constrict(n * p, values.size()));
        if(n * p % 1.0 == 0.0)
        {
            Xnp = values.get(constrict((n * p) - 1,values.size()));
            return (Xnp + Xnp1) / 2;
        }
        else
        {
            return Xnp1;
        }
    }

    /**
    * This function returns the median of a given list of values, if the list is not empty.
    * @param values list of values to calculate the median from
    * @return will return null for empty lists, otherwise will return the median of given list of values.
    */
    public static Double median(List<Double> values)
    {
        return pQuantile(values, 0.5);
    }

    public <T extends Number> double cumulate(List<T> values, T threshold){ //Kumulierte Häufigkeit
        double cumulated = 0;
        Iterator<T> listIterator = values.listIterator();
        while(listIterator.hasNext()){
            if(listIterator.next().doubleValue() < threshold.doubleValue()) { cumulated++; }
        }
        return cumulated;
    }

    public <T extends Number> List<Double> normalize(List<T> values){
        Iterator<T> listIterator = values.listIterator();
        double minimum = Double.POSITIVE_INFINITY;
        double maximum = Double.NEGATIVE_INFINITY; //So that there's always a max and a min to be found
        ArrayList<Double> normalized = new ArrayList<Double>();
        while(listIterator.hasNext()){
            T next = listIterator.next();
            if(minimum < next.doubleValue()) { minimum = next.doubleValue(); }
            if(maximum > next.doubleValue()) { maximum = next.doubleValue(); }
        }
        listIterator = values.listIterator();
        while(listIterator.hasNext()){
            T next = listIterator.next();
            normalized.add(next.doubleValue()/maximum - minimum/maximum);
        }
        return normalized;
    }


}
