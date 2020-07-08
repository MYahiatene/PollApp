package gpse.umfrato.domain.poll;

import gpse.umfrato.domain.category.Category;
import gpse.umfrato.domain.participationlinks.ParticipationLink;
import gpse.umfrato.domain.participationlinks.ParticipationLinkService;
import gpse.umfrato.domain.pollresult.PollResultService;
import lombok.Data;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * This class is used every time the frontend needs data about a poll,
 * but not the entire list of categories and questions to cut down on data.
 */
@Data
public class SmallPoll {

    private static final String DEFAULT = "nicht automatisch";

    private Long pollId;

    private String pollCreator;

    private String creationDate;

    private String lastEditAt;

    private String lastEditFrom;

    private String activatedDate;

    private String deactivatedDate;

    private String anonymityStatus;

    private String pollName;

    private int pollStatus;

    private List<ParticipationLink> participationLinks;

    private Integer categoryCount;

    private Integer questionCount;

    private Integer participantCount;

    private Integer expectedParticipantCount;

    private String computedSubtitle;

    public SmallPoll(final Poll original, final PollResultService pollResultService,
                     final ParticipationLinkService participationLinkService) {
        final SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.GERMAN);
        this.pollId = original.getPollId();
        this.pollName = original.getPollName();
        this.pollCreator = original.getPollCreator();
        this.creationDate = original.getCreationDate();
        this.lastEditAt = original.getLastEditAt();
        this.lastEditFrom = original.getLastEditFrom();
        this.activatedDate = original.isActivated() ? df.format(original.getActivatedDate().getTime()) : DEFAULT;
        this.deactivatedDate = original.isDeactivated() ? df.format(original.getDeactivatedDate().getTime()) : DEFAULT;
        this.anonymityStatus = original.getAnonymityStatus();
        this.pollStatus = original.getPollStatus();
        this.participationLinks = participationLinkService.getAllParticipationLinks(pollId);
        this.categoryCount = original.getCategoryList().size();
        this.questionCount = 0;
        for (final Category c: original.getCategoryList()) {
            this.questionCount += c.getQuestionList().size();
        }
        this.participantCount = pollResultService.getPollResults(pollId).size();
        if (this.anonymityStatus.equals("1")) {
            this.expectedParticipantCount = 0;
        } else {
            this.expectedParticipantCount = participationLinkService.getAllParticipationLinks(pollId).size();
        }
        final StringBuilder sb = new StringBuilder();
        if (pollStatus == 0) {
            sb.append(questionCount);
            if (questionCount == 1) {
                sb.append(" Frage in ");
            } else {
                sb.append(" Fragen in ");
            }
            sb.append(categoryCount);
            if (categoryCount == 1) {
                sb.append(" Kategorie.");
            } else {
                sb.append(" Kategorien.");
            }
            sb.append(" Zuletzt bearbeitet von ").append(lastEditFrom).append(" am ").append(lastEditAt);
        }
        if (pollStatus == 1) {
            sb.append("Umfrage bereit.");
            if (original.isActivated()) {
                sb.append(" Wird zum ").append(activatedDate).append(" aktiviert.");
            } else {
                sb.append(" Warte auf manuelle Aktivierung.");
            }
            sb.append(" Zuletzt bearbeitet von ").append(lastEditFrom).append(" am ").append(lastEditAt);
        } else if (pollStatus == 2) {
            sb.append("Umfrage aktiv seit ").append(activatedDate).append(". Bisherige Teilnehmer: ").
                append(participantCount);
            if (!anonymityStatus.equals("1")) {
                sb.append(" / ").append(expectedParticipantCount).append(" (").append(participantCount.doubleValue() /
                    expectedParticipantCount.doubleValue() * 100.0).append("%)");
            }
            if (original.isDeactivated()) {
                sb.append(" Wird zum ").append(deactivatedDate).append(" deaktiviert.");
            }
        } else if (pollStatus == 1 + 2) {
            sb.append("Umfrage deaktiviert seit ").append(deactivatedDate).append(". Teilnehmer: ").
                append(participantCount);
            if (!anonymityStatus.equals("1")) {
                sb.append(" / ").append(expectedParticipantCount).append(" (").append(participantCount.doubleValue() /
                    expectedParticipantCount.doubleValue() * 100.0).append("%)");
            }
        }
        this.computedSubtitle = sb.toString();
    }
}
