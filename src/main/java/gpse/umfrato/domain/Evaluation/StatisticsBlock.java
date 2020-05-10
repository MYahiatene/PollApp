package gpse.umfrato.domain.Evaluation;

public class StatisticsBlock
{

    private enum CalculationType {median,max,min,qunatil,valueOver,valueUnder}
    long targetPollId = 0;
    boolean inverted = false;
    public void work(StatisticsData sd)
    {
        return;
    }

    public class QuestionFilterBlock extends StatisticsBlock
    {
        long targetQuestionId = 0;
        int targetAnswerId = 0;
        public void work(StatisticsData sd)
        {
            sd.removeWithAnswer(targetPollId,targetQuestionId,targetAnswerId,inverted);
        }
    }

    public class UserFilterBlock extends StatisticsBlock
    {
        String attribute = "";
        String expected = "";
        public void work(StatisticsData sd)
        {
            sd.removeWithUser(targetPollId,attribute,expected,inverted);
        }
    }

    public class CalculateBlock extends StatisticsBlock
    {
        CalculationType thisType = CalculationType.median;
        int value;
        public void work(StatisticsData sd)
        {
            switch(thisType)
            {
                case median:
                {
                    break;
                }
                case max:
                {
                    break;
                }
                case min:
                {
                    break;
                }
                case qunatil:
                {
                    break;
                }
                case valueOver:
                {
                    break;
                }
                case valueUnder:
                {
                    break;
                }
            }
        }
    }
}
