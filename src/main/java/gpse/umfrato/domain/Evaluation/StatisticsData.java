package gpse.umfrato.domain.Evaluation;

import gpse.umfrato.domain.answer.Answer;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public class StatisticsData
{
    List<PollPackage> pollPacks;

    public void removePoll(int poll)
    {
        pollPacks.remove(poll);
    }

    public void removeUser(int poll, int result)
    {
        pollPacks.get(poll).getPollResults().remove(result);
    }

    public void removeAnswer(int poll, int result, int answer)
    {
        pollPacks.get(poll).getPollResults().get(result).getAnswers().remove(answer);
    }

    public void removeAnswers(int answer, int poll)
    {
        for(PollResult pollResult:pollPacks.get(poll).getPollResults())
        {
            pollResult.getAnswers().remove(answer);
        }
    }

    public void removeWithAnswer(long pollId, int question, int checkedAnswer)
    {
        removeWithAnswer(getPollIndexWithId(pollId),resutl,checkedAnswer,false);
    }

    public void removeWithAnswer(long pollId, int question, int checkedAnswer, boolean removeIfChecked)
    {
        PollPackage pollPackage = pollPacks.get(getPollIndexWithId(pollId));
        for(int i = 0;i < pollPackage.getPollResults().size();i++)
        {
            PollResult pollResult = pollPackage.getPollResults();
            if(pollResult.getAnswers().get(question) instanceof SelectionQuestion)
            {
                if(pollResult.getAnswers().get(question).getGivenAnswers().contains(checkedAnswer) ^ removeIfChecked)
                {
                    removeUser(getPollIndexWithId(pollId),i);
                }
            }
        }
    }

    public void removeWithUser(long pollId, String attribute, String expectedValue)
    {
        removeWithUser(getPollIndexWithId(pollId),attribute,expectedValue,false);
    }

    public void removeWithUser(long pollId, String attribute, String expectedValue, boolean removeIfFalse)
    {
        PollPackage pollPackage = getPollWithId(poll);
        for(int i = 0;i < pollPackage.getPollResults().size();i++)
        {
            PollResult pollResult = pollPackage.getPollResults();
            if((pollResult.getUser().getAttribute() == expectedValue) ^ removeIfFalse)
            {
                removeUser(getPollIndexWithId(pollId),i);
            }
        }
    }

    private PollPackage getPollWithId(long pollId)
    {
        for(PollPackage pollPackage:pollPacks)
        {
            if(pollPackage.getPoll().getId() == pollId)
            {
                return pollPackage;
            }
        }
        return null;
    }

    private int getPollIndexWithId(long pollId)
    {
        int i = -1;
        for(PollPackage pollPackage:pollPacks)
        {
            if(pollPackage.getPoll().getId() == pollId)
            {
                return i;
            }
        }
        return i;
    }

    public StatisticsData intersection(StatisticsData sd)
    {
        for(PollPackage pollPackage:pollPacks)
        {
            int packageIndex = sd.pollPacks.indexOf(pollPackage);
            if(packageIndex != -1)
            {
                PollPackage sdPollPackage = sd.pollPacks.get(packageIndex);
                List<PollResult> sdResults = sdPollPackage.getPollResults();
                for(PollResult pollResult:pollPackage.getPollResults())
                {
                    int resultIndex = sdResults.indexOf(pollResult);
                    if(resultIndex != -1)
                    {
                        PollResult sdPollResult = sdResults.get(resultIndex);
                        List<Answer> sdAnswers = sdPollResult.getAnswerList();
                        for(Answer answer:pollResult.getAnswerList())
                        {
                            if(!sdAnswers.contains(answer))
                            {
                                pollResult.remove(answer);
                            }
                        }
                    }
                    else
                    {
                        pollPackage.remove(pollResult);
                    }
                }
            }
            else
            {
                pollPacks.remove(pollPackage);
            }
        }
        return this;
    }

    public StatisticsData union(StatisticsData sd)
    {
        for(PollPackage pollPackage:sd.pollPacks)
        {
            int packageIndex = pollPacks.indexOf(pollPackage);
            if(packageIndex != -1)
            {
                PollPackage myPollPackage = pollPacks.get(packageIndex);
                List<PollResult> myResults = myPollPackage.getPollResults();
                for(PollResult pollResult:pollPackage.getPollResults())
                {
                    int resultIndex = myResults.indexOf(pollResult);
                    if(resultIndex != -1)
                    {
                        PollResult myPollResult = myResults.get(resultIndex);
                        List<Answer> myAnswers = myPollResult.getAnswerList();
                        for(Answer answer:pollResult.getAnswerList())
                        {
                            if(!myAnswers.contains(answer))
                            {
                                myPollResult.add(answer);
                            }
                        }
                    }
                    else
                    {
                        myPollPackage.add(pollResult);
                    }
                }
            }
            else
            {
                pollPacks.add(pollPackage);
            }
        }
        return this;
    }

    public DiagramData toDiagramData()
    {
        DiagramData dd = new DiagramData();
        //TODO: fill in DiagramData
        //sprich: aus Listen von Usern Listen von absoluten Häufigkeiten machen
        return dd;
    }
}
