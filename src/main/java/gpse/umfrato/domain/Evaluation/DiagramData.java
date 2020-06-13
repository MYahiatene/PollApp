package gpse.umfrato.domain.Evaluation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gpse.umfrato.domain.answer.Answer;
import gpse.umfrato.domain.poll.Poll;
import gpse.umfrato.domain.pollresult.PollResult;
import gpse.umfrato.domain.question.Question;
import gpse.umfrato.domain.question.QuestionService;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class DiagramData
{
	interface QuestionData
	{
		enum QuestionType{ChoiceQuestion, TextQuestion, RangeQuestion, SliderQuestion}
		long getQuestionId();
		QuestionType getQuestionType();
		void statistics();
		String toJSON();
	}

	@Getter
	@Setter
	private class ChoiceData implements QuestionData
	{
		long questionId;
		String questionTitle;
		private List<String> answerPossibilities;
		private List<Integer> data;
		private List<Double> relative;
		private String median;
		private String mode;

		ChoiceData(long questionId, String questionMessage, List<String> answerPossibilities)
		{
			this.questionId = questionId;
			this.questionTitle = questionMessage;
			this.answerPossibilities = answerPossibilities;
			data = new ArrayList<>();
			for(String ignored:answerPossibilities)
			{
				data.add(0);
			}
		}

		public void addAnswer(int answerPossibility)
		{
			data.set(answerPossibility,data.get(answerPossibility) + 1);
		}

		@Override public QuestionType getQuestionType()
		{
			return QuestionType.ChoiceQuestion;
		}

		@Override public void statistics()
		{
			int size = 0;
			List<Integer> maxima = new ArrayList<>();
			int max = 0;
			//Häufigste Antwort und Menge der Antworten raussuchen
			for(int i = 0; i < data.size(); i++)
			{
				size += data.get(i);
				if(data.get(i) > max)
				{
					maxima.clear();
					maxima.add(i);
					max = data.get(i);
				}
				else if(data.get(i) == max)
				{
					maxima.add(i);
				}
			}
			StringBuilder modeText = new StringBuilder();
			for(Integer i: maxima)
			{
				modeText.append(answerPossibilities.get(i)).append(" / ");
			}
			modeText.replace(modeText.lastIndexOf(" / "), modeText.length(), "");
			mode = modeText.toString();
			//Median ist an mittlerer Stelle
			int medianPos = size / 2;
			relative = new ArrayList<>();
			for(int i = 0; i < data.size(); i++)
			{
				//Relative Häufigkeiten nur Summe vor Division
				relative.add(data.get(i).doubleValue() / (double) size);
				medianPos -= data.get(i);
				// [1,1,1,1,1,1,2,2,2,3,3,3,4,6](originalDaten) => [0,6,3,3,1,0,1](data)
				// => 14(data.size) => 7(/2) => 1(-6) => -2(-3) => 2(Position) => Median
				if(medianPos <= 0 && median == null)
				{
					if(medianPos == 0 && i < answerPossibilities.size())
					{
						median = answerPossibilities.get(i) + " / " + answerPossibilities.get(i + 1);
					}
					else
					{
						median = answerPossibilities.get(i);
					}
				}
			}
		}

		@Override public String toJSON()
		{
			StringBuilder json = new StringBuilder();
			json.append("{\"id\": ").append(questionId)
					.append(",\"type\": ").append("\"choice\"")
					.append(",\"title\": \"").append(questionTitle)
					.append("\",\"answerPossibilities\": [");
					for(int i = 0; i < answerPossibilities.size();i++)
					{
						json.append("\"").append(answerPossibilities.get(i)).append("\"");
						if(i < answerPossibilities.size() - 1)
						{
							json.append(",");
						}
					}
					json.append("],\"data\": ").append(data)
					.append(",\"calculated\": {")
					.append("\"relative\": ").append(relative)
					.append(",\"median\": \"").append(median)
					.append("\",\"mode\": \"").append(mode)
					.append("\"}}");
			return json.toString();
		}
	}

	@Getter
	@Setter
	private class TextData implements QuestionData
	{
		long questionId;
		String questionTitle;
		private List<Long> ids = new ArrayList<>();
		private List<String> texts = new ArrayList<>();
		private List<String> editedDates = new ArrayList<>();
		private List<String> creator = new ArrayList<>();

		TextData(long questionId, String questionMessage)
		{
			this.questionId = questionId;
			this.questionTitle = questionMessage;
		}

		@Override public QuestionType getQuestionType()
		{
			return QuestionType.TextQuestion;
		}

		@Override public void statistics()
		{

		}

		@Override
		public String toJSON()
		{
			StringBuilder json = new StringBuilder();
			json.append("{\"id\": ").append(questionId)
					.append(",\"type\": ").append("\"text\"")
					.append(",\"title\": \"").append(questionTitle)
					.append("\",\"answers\": [");
			for(int i = 0; i < ids.size(); i++)
			{
				json.append("{").append("\"id\": ").append(ids.get(i))
						.append(",\"text\": \"").append(texts.get(i))
						.append("\",\"answered\": \"").append(editedDates.get(i))
						.append("\",\"creator\": \"").append(creator.get(i))
						.append("\"}");
				if(i + 1 < ids.size())
				{
					json.append(",");
				}
			}
			json.append("]}");
			return json.toString();
		}
	}

	@Getter
	@Setter
	private static class RangeData implements QuestionData
	{
		long questionId;
		String questionTitle;

		RangeData(long questionId, String questionMessage)
		{
			this.questionId = questionId;
			this.questionTitle = questionMessage;
		}

		@Override public QuestionType getQuestionType()
		{
			return QuestionType.RangeQuestion;
		}

		@Override public void statistics()
		{

		}

		@Override public String toJSON()
		{
			return "{\"id\": " + questionId + ",\"type\": " + "\"text\"" + ",\"title\": " + questionTitle + "}";
		}
	}

	@Getter
	@Setter
	private static class SliderData implements QuestionData
	{
		long questionId;
		String questionTitle;

		SliderData(long questionId, String questionMessage)
		{
			this.questionId = questionId;
			this.questionTitle = questionMessage;
		}

		@Override public QuestionType getQuestionType()
		{
			return QuestionType.SliderQuestion;
		}

		@Override public void statistics()
		{

		}

		@Override public String toJSON()
		{
			return "{\"id\": " + questionId + ",\"type\": " + "\"text\"" + ",\"title\": " + questionTitle + "}";
		}
	}

	private final List<QuestionData> questions = new ArrayList<>();
	@JsonIgnore private final QuestionService questionService;
	@JsonIgnore private final Poll poll;

	public DiagramData(Poll poll, List<PollResult> results, QuestionService questionService)
	{
		this.questionService = questionService;
		this.poll = poll;
		loadData(results);
	}

	private void loadData(List<PollResult> results)
	{
		for(Question q:questionService.getAllQuestions(poll.getPollId()))
		{
			QuestionData qd = null;
			switch(q.getQuestionType())
			{
				case "ChoiceQuestion":
				{
					qd = new ChoiceData(q.getQuestionId(), q.getQuestionMessage(), q.getAnswerPossibilities());
					break;
				}
				case "TextQuestion":
				{
					qd = new TextData(q.getQuestionId(), q.getQuestionMessage());
					break;
				}
				case "RangeQuestion":
				{
					qd = new RangeData(q.getQuestionId(), q.getQuestionMessage());
					break;
				}
				case "SliderQuestion":
				{
					qd = new SliderData(q.getQuestionId(), q.getQuestionMessage());
					break;
				}
				default:
				{

				}
			}
			if(qd != null)
			{
				questions.add(qd);
			}
		}
		for(PollResult pr:results)
		{
			for(Answer a:pr.getAnswerList())
			{
				for(QuestionData qd:questions)
				{
					if(qd.getQuestionId() == a.getQuestionId())
					{
						switch(qd.getQuestionType())
						{
							case ChoiceQuestion:
							{
								ChoiceData cd = (ChoiceData) qd;
								for(String s:a.getGivenAnswerList())
								{
									cd.addAnswer(Integer.parseInt(s));
								}
								break;
							}
							case TextQuestion:
							{
								if(!a.getGivenAnswerList().isEmpty())
								{
									TextData td = (TextData) qd;
									td.getCreator().add(pr.getPollTaker());
									//nur die neuste (letzte) Antwort
									td.getTexts().add(a.getGivenAnswerList().get(a.getGivenAnswerList().size() - 1));
									td.getEditedDates().add(pr.getLastEditAt());
									//vielleicht auch eine neue ID, aber ich wüsste nciht warum
									td.getIds().add(pr.getPollResultId());
								}
								break;
							}
							case RangeQuestion:
							{
								RangeData rd = (RangeData) qd;
								break;
							}
							case SliderQuestion:
							{
								SliderData sd = (SliderData) qd;
								break;
							}
						}
					}
				}
			}
		}
		for(QuestionData qd:questions)
		{
			qd.statistics();
		}
	}

	public String toJSON()
	{
		StringBuilder json = new StringBuilder();
		json.append("[");
		for(int i = 0; i < questions.size(); i++)
		{
			json.append(questions.get(i).toJSON());
			if(i + 1 < questions.size())
			{
				json.append(",");
			}
		}
		json.append("]");
		return json.toString();
	}

}
