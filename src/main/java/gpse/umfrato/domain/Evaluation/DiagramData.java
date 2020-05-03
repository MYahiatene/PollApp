package gpse.umfrato.domain.Evaluation;

import java.util.List;

public class DiagramData
{

    public class Color
    {
        int red;
        int green;
        int blue;
        int alpha;
    }
    private class Dataset
    {
        List<Double> values;
        String name;
        Color color;
        public String toJSON() //sobald wir die Anforderungen verstanden haben
        {
            return "";
        }
    }
    List<Dataset> datasets;
    List<String> xAxisLabels;
    List<String> yAxisLabels; //standartmäßig leer und vom chart.js erstellt
    String xAxisName;
    String yAxisName;
}

