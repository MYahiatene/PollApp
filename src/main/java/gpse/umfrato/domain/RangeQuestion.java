package gpse.umfrato.domain;

public class RangeQuestion {

    double highestValue;
    double lowestValue;
    double stepSize;
    boolean roundValues;
    boolean addOutliers;
    String optOut;

    ChoiceAnswer answer;

    // if there's time we can add more options: outlier String, only one outlier etc.
}
