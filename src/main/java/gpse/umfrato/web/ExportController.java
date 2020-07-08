package gpse.umfrato.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import gpse.umfrato.domain.category.CategoryService;
import gpse.umfrato.domain.cmd.FilterCmd;
import gpse.umfrato.domain.cmd.PollCmd;
import gpse.umfrato.domain.consistencyquestion.ConsistencyQuestionService;
import gpse.umfrato.domain.evaluation.session.SessionService;
import gpse.umfrato.domain.evaluation.Statistics;
import gpse.umfrato.domain.export.ExportService;
import gpse.umfrato.domain.participationlinks.ParticipationLinkService;
import gpse.umfrato.domain.poll.Poll;
import gpse.umfrato.domain.poll.PollRepository;
import gpse.umfrato.domain.poll.PollService;
import gpse.umfrato.domain.pollresult.PollResult;
import gpse.umfrato.domain.pollresult.PollResultService;
import gpse.umfrato.domain.question.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.web.bind.annotation.*;

import javax.activation.UnsupportedDataTypeException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@RequestMapping("/api/export/")
@RestController
@CrossOrigin
public class ExportController {
    private static final String FILE_PATH = "src/main/java/gpse/umfrato/domain/export/files/";
    private static final Integer MAX_FILE_NUMBER = 50;
    private final PollService pollService;
    private final PollResultService pollResultService;
    private final ExportService exportService;
    private final QuestionService questionService;
    private final CategoryService categoryService;
    private final SessionService sessionService;
    private final ConsistencyQuestionService consistencyQuestionService;
    private final ParticipationLinkService participationLinkService;
    private final PollRepository pollRepository;
    private static final String ONE = "1";
    private static final String ALL_USERS = "allUsers";
    private Integer fileNumber = 0;

    @Autowired
    public ExportController(final PollService pollService, final PollResultService pollResultService, final ExportService exportService, final QuestionService questionService, final CategoryService categoryService, final ConsistencyQuestionService consistencyQuestionService, final SessionService sessionService, final ParticipationLinkService participationLinkService, final PollRepository pollRepository) {
        this.pollService = pollService;
        this.pollResultService = pollResultService;
        this.exportService = exportService;
        this.questionService = questionService;
        this.categoryService = categoryService;
        this.consistencyQuestionService = consistencyQuestionService;
        this.sessionService = sessionService;
        this.participationLinkService = participationLinkService;
        this.pollRepository = pollRepository;
    }

    @RequestMapping(value = "/getFile/{fileNumber:\\d+}", method = RequestMethod.GET)
    @ResponseBody
    public FileSystemResource getFile(final @PathVariable Integer fileNumber) {
        return new FileSystemResource(new File(FILE_PATH + fileNumber.toString() + ".txt"));
    }

    public Integer writeToFile(final String string) throws FileNotFoundException {
        final int usedFileNumber = fileNumber;
        final File file = new File(FILE_PATH + fileNumber + ".txt");
        fileNumber = ++fileNumber % MAX_FILE_NUMBER;
        final PrintWriter out = new PrintWriter(file);
        out.println(string);
        out.flush();
        out.close();
        return usedFileNumber;
    }

    public Integer writeToFileCSV(final String string) throws FileNotFoundException {
        final int usedFileNumber = fileNumber;
        final File file = new File(FILE_PATH + fileNumber.toString() + ".txt");
        fileNumber = ++fileNumber % MAX_FILE_NUMBER;
        final PrintWriter out = new PrintWriter(file);
        for (final String index : string.split("\n")) {
            out.append(index);
            out.append('\n');
        }
        out.flush();
        out.close();
        return usedFileNumber;
    }

    @PostMapping("/importPoll")
    public Long fromJSONToPoll(final @RequestBody String file) throws MalformedURLException {
        final PollCmd pollCmd = exportService.fromJSONToPoll(file);
        Instant actDate = Instant.parse(pollCmd.getActivatedDate());
        ZonedDateTime actTime = ZonedDateTime.ofInstant(actDate, ZoneId.ofOffset("UTC", ZoneOffset.ofHoursMinutes(pollCmd.getTimeZoneOffset() / 60,pollCmd.getTimeZoneOffset() % 60)));
        pollCmd.setActivatedDate(actTime.toString());
        Instant deactDate = Instant.parse(pollCmd.getDeactivatedDate());
        ZonedDateTime deactTime = ZonedDateTime.ofInstant(deactDate, ZoneId.ofOffset("UTC", ZoneOffset.ofHoursMinutes(pollCmd.getTimeZoneOffset() / 60,pollCmd.getTimeZoneOffset() % 60)));
        pollCmd.setDeactivatedDate(deactTime.toString());

        Instant creationDate = Instant.parse(pollCmd.getCreationDate());
        ZonedDateTime creationTime = ZonedDateTime.ofInstant(creationDate, ZoneId.ofOffset("UTC", ZoneOffset.ofHoursMinutes(pollCmd.getTimeZoneOffset() / 60,pollCmd.getTimeZoneOffset() % 60)));

        final Poll poll = new Poll(pollCmd.getPollCreator(), pollCmd.getAnonymityStatus(), pollCmd.getPollName(), creationTime,
        actTime, deactTime, pollCmd.getPollStatus(),
            pollCmd.getBackgroundColor(), pollCmd.getFontColor(), pollCmd.getLogo(), pollCmd.isVisibility(),
            pollCmd.isCategoryChange(), pollCmd.isActivated(), pollCmd.isDeactivated(), pollCmd.getRepeat(),
        pollCmd.getRepeatUntil(), pollCmd.getDay(), pollCmd.getWeek(), pollCmd.getMonth(),
        pollCmd.getStoppingReason(), pollCmd.getLevel(), 0L);


    /*if (poll.getAnonymityStatus().equals(ONE)) {
        final String link = participationLinkService.createParticipationLink().toString();
        participationLinkService.saveParticipationLink(poll.getPollId(), ALL_USERS, link);
    }*/
        pollRepository.save(poll);
        return poll.getPollId();
    }
    //ZoneId timeZone = ZoneId.ofOffset("UTC", ZoneOffset.ofHoursMinutes(timeZoneOffset / 60,timeZoneOffset % 60));

    /*@PostMapping("/importPoll")
    public Long fromJSONToPoll(final @RequestBody String file) throws MalformedURLException {
        final PollCmd pollCmd = exportService.fromJSONToPoll(file);
        Calendar actCalendar = Calendar.getInstance();
        System.out.println("ActDate: "+pollCmd.getActivatedDate());
        Date actDate = new Date(Long.parseLong(pollCmd.getActivatedDate()));
        actCalendar.setTime(actDate);
        pollCmd.setActivatedDate(actCalendar.toString());
        Calendar deactCalendar = Calendar.getInstance();
        System.out.println("DeactDate: "+pollCmd.getDeactivatedDate());
        Date deactDate = new Date(Long.parseLong(pollCmd.getDeactivatedDate()));
        actCalendar.setTime(deactDate);
        pollCmd.setDeactivatedDate(deactCalendar.toString());
        final Poll poll = new Poll(pollCmd.getPollCreator(), pollCmd.getAnonymityStatus(), pollCmd.getPollName(), pollCmd.getCreationDate(), actCalendar,
            deactCalendar, pollCmd.getPollStatus(), pollCmd.getBackgroundColor(), pollCmd.getFontColor(), pollCmd.getLogo(), pollCmd.isVisibility(), pollCmd.isCategoryChange(), pollCmd.isActivated(),
            pollCmd.isDeactivated());
        //if (poll.getAnonymityStatus().equals(ONE)) {
        //    final String link = participationLinkService.createParticipationLink().toString();
        //    participationLinkService.saveParticipationLink(poll.getPollId(), ALL_USERS, link);
        //}
        pollRepository.save(poll);
        return poll.getPollId();
    }*/

    @PostMapping("/Poll/{pollId:\\d+}")
    public Integer toCSVManual(final @PathVariable Long pollId, final @RequestParam String format, final @RequestParam String separator) throws UnsupportedDataTypeException, FileNotFoundException, JsonProcessingException {
        final Poll poll = pollService.getPoll(pollId);
        if (format.equals("csv")) {
            final String csv = exportService.toCSVManual(poll, separator);
            return writeToFileCSV(csv);
        } else if (format.equals("json")) {
            final String json = exportService.toJSON(poll);
            return writeToFile(json);
        } else {
            throw new UnsupportedDataTypeException(format + "is not a supported export format");
        }
    }

    @PostMapping("/PollResult/{pollId:\\d+}")
    public Integer toJSONResult(final @PathVariable Long pollId, final @RequestParam Long sessionId, final @RequestParam String format, final @RequestParam String separator, final @RequestBody List<FilterCmd> filterList) throws JsonProcessingException, FileNotFoundException, UnsupportedDataTypeException {
        final FilterCmd cmd = new FilterCmd();
        cmd.setBasePollId(pollId);
        cmd.setBaseQuestionIds(Collections.singletonList(-1L));
        final Statistics statistics = new Statistics(questionService, pollService, pollResultService, categoryService, consistencyQuestionService, sessionService, cmd);
        if (sessionId < 0) {
            if (sessionId > -2) {
                statistics.loadFilter(filterList);
            }
        } else {
            statistics.loadSessionFilters(sessionId);
        }
        final List<PollResult> results = statistics.filteredResults();
        if (format.equals("csv")) {
            final String csv = exportService.toCSVManual(results, pollService.getPoll(pollId), separator);
            return writeToFileCSV(csv);
        } else if (format.equals("json")) {
            final String json = exportService.toJSON(results);
            return writeToFile(json);
        } else {
            throw new UnsupportedDataTypeException(format + "is not a supported export format");
        }
    }

    /*@PostMapping("/toJSONWithPollResult")
    public String createExportJSON(Poll poll, List<PollResult> result){
        return exportService.createExportJSON(poll, result);
    }*/

}

