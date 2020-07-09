package gpse.umfrato.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import gpse.umfrato.domain.category.Category;
import gpse.umfrato.domain.category.CategoryService;
import gpse.umfrato.domain.cmd.CategoryCmd;
import gpse.umfrato.domain.cmd.FilterCmd;
import gpse.umfrato.domain.cmd.PollCmd;
import gpse.umfrato.domain.cmd.QuestionCmd;
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
import gpse.umfrato.domain.question.Question;
import gpse.umfrato.domain.question.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.web.bind.annotation.*;

import javax.activation.UnsupportedDataTypeException;
import javax.persistence.EntityNotFoundException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Logger;

@RequestMapping("/api/export/")
@RestController
@CrossOrigin
public class ExportController {
    private static final String FILE_PATH = "src/main/java/gpse/umfrato/domain/export/files/";
    private static final Integer MAX_FILE_NUMBER = 50;
    private static final String ONE = "1";
    private static final String ALL_USERS = "allUsers";
    private static final Logger LOGGER = Logger.getLogger("ExportController");
    private final PollService pollService;
    private final PollResultService pollResultService;
    private final ExportService exportService;
    private final QuestionService questionService;
    private final CategoryService categoryService;
    private final SessionService sessionService;
    private final ConsistencyQuestionService consistencyQuestionService;
    private final ParticipationLinkService participationLinkService;
    private final PollRepository pollRepository;
    private Integer fileNumber = 0;

    private static final double ZERO = 0.0;


    private static final String TEXT_QUESTION = "TextQuestion";
    private static final String RANGE_QUESTION = "RangeQuestion";
    private static final String SLIDER_QUESTION = "SliderQuestion";
    private static final String CHOICE_QUESTION = "ChoiceQuestion";
    private static final String SORT_QUESTION = "SortQuestion";


    @Autowired
    public ExportController(final PollService pollService, final PollResultService pollResultService,
                            final ExportService exportService, final QuestionService questionService,
                            final CategoryService categoryService,
                            final ConsistencyQuestionService consistencyQuestionService,
                            final SessionService sessionService,
                            final ParticipationLinkService participationLinkService,
                            final PollRepository pollRepository) {
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

    /**
     * @param string Der String welcher am Ende in die Datei geschrieben wird
     * Die Funktion schreibt einen Text in eine Datei und gibt die Dateinummer zurück
     * */
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

    /**
     * @param string Der String welcher am Ende in die Datei geschrieben wird
     * Die Funktion schreibt eine CSV-Datei und gibt die Dateinummer zurück
     * */
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

    /**
     * @param file Die JSON-Datei welche importiert werden soll
     * @param timeZoneOffset Der Zeitzonenoffset für die benutzte Zeitzone
     * Die Funktion parst zuerst die aktivierungs-, deaktivierungs- und creation-Zeiten (werden als Unix epoch-Timestamps übergeben)
     * und erstellt dann eine neue Poll mit neuem ParticipationLink und speichert diese ab.
     * */
    @PostMapping("/importPoll")
    public Long fromJSONToPoll(final @RequestBody String file, final @RequestHeader int timeZoneOffset) throws MalformedURLException {
        final PollCmd pollCmd = exportService.fromJSONToPoll(file);
        Instant actDate = Instant.ofEpochSecond(Long.parseLong(pollCmd.getActivatedDate().substring(0, 10))); //Instant.parse(pollCmd.getActivatedDate());
        ZonedDateTime actTime = ZonedDateTime.ofInstant(actDate, ZoneId.ofOffset("UTC", ZoneOffset.ofHoursMinutes(timeZoneOffset / 60,timeZoneOffset % 60)));
        pollCmd.setActivatedDate(actTime.toString());
        Instant deactDate = Instant.ofEpochSecond(Long.parseLong(pollCmd.getDeactivatedDate().substring(0, 10)));
        ZonedDateTime deactTime = ZonedDateTime.ofInstant(deactDate, ZoneId.ofOffset("UTC", ZoneOffset.ofHoursMinutes(timeZoneOffset / 60,timeZoneOffset % 60)));
        pollCmd.setDeactivatedDate(deactTime.toString());

        Instant creationDate = Instant.ofEpochSecond(Long.parseLong(pollCmd.getCreationDate().substring(0, 10)));
        ZonedDateTime creationTime = ZonedDateTime.ofInstant(creationDate, ZoneId.ofOffset("UTC", ZoneOffset.ofHoursMinutes(timeZoneOffset / 60,timeZoneOffset % 60)));

        final Poll poll = new Poll(pollCmd.getPollCreator(), pollCmd.getAnonymityStatus(), pollCmd.getPollName(), creationTime, actTime, deactTime, 0,
            pollCmd.getBackgroundColor(), pollCmd.getFontColor(), pollCmd.getLogo(), pollCmd.isVisibility(),
            pollCmd.isCategoryChange(), pollCmd.isActivated(), pollCmd.isDeactivated(), pollCmd.getOwnDesign(), pollCmd.getRepeat(),
        pollCmd.getRepeatUntil(), pollCmd.getDay(), pollCmd.getWeek(), pollCmd.getMonth(),
        pollCmd.getStoppingReason(), pollCmd.getLevel(), 0L, pollCmd.getCheckLeapYear());
        poll.setPollId(null);
        Long pollId = pollRepository.save(poll).getPollId();
        poll.setLastEditAt(ZonedDateTime.now());
        poll.setCheckLeapYear(pollCmd.getCheckLeapYear());
        poll.setSeriesPollName(pollCmd.getSeriesPollName());
        if (poll.getAnonymityStatus().equals(ONE)) {
            final String link = participationLinkService.createParticipationLink().toString();
            participationLinkService.saveParticipationLink(poll.getPollId(), ALL_USERS, link);
            poll.setParticipationLink(link);
        }
        poll.setCategoryList(cmdToCategory(pollCmd.getCategoryList(), pollId));
        poll.setSeriesCounter(pollCmd.getSeriesCounter());

    /*if (poll.getAnonymityStatus().equals(ONE)) {
        final String link = participationLinkService.createParticipationLink().toString();
        participationLinkService.saveParticipationLink(poll.getPollId(), ALL_USERS, link);
    }*/
        return pollId;
    }

    /**
     * @param input Kategorienliste aus dem zu importierenden Poll
     * @param pollId pollId des neuen Polls
     * Die Funktion macht eine Liste an CategoryCMDs zu einer Liste an Kategorien da es dafür keine Funktion gab
     * */
    private List<Category> cmdToCategory(List<CategoryCmd> input, Long pollId){
        List<Category> outputList = new ArrayList<>();
        for(CategoryCmd cmd : input) {
            Category cat = categoryService.createCategory(cmd.getCategoryName(), pollId);
            /*System.out.println(cat.getCategoryId());*/
            Long indexCounter = 0L;
            for(QuestionCmd q : cmd.getQuestionList()) {
                q.setCategoryId(cat.getCategoryId());
                q.setPollId(pollId);
                questionService.changeCategory(questionService.addQuestion(q).getQuestionId(), cat.getCategoryId() ,indexCounter);
                indexCounter++;
            }
            outputList.add(cat);

        }
        return outputList;
    }

    /**
     * @param pollId Die PollId
     * @param format Das gewünschte Format (JSON/CSV)
     * @param separator Der gewünschte Separator
     * Die Funktion macht aus einer Poll eine JSON bzw. CSV-Datei
     * */
    @PostMapping("/Poll/{pollId:\\d+}")
    public Integer toCSVManual(final @PathVariable Long pollId, final @RequestParam String format,
                               final @RequestParam String separator) throws UnsupportedDataTypeException,
        FileNotFoundException, JsonProcessingException {
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

    /**
     * @param pollId Die PollId
     * @param sessionId Die SessionID des Nutzers
     * @param format Das gewünschte Format (JSON/CSV)
     * @param separator Der gewünschte Separator
     * @param filterList Eine Liste an gewünschten Filtern
     * @param timeZoneOffset Der Zeitzonenoffset
     * Die Funktion exportiert eine möglicherweise gefilterte Liste an PollResults als JSON bzw. CSV
     * */
    @PostMapping("/PollResult/{pollId:\\d+}")
    public Integer toJSONResult(final @PathVariable Long pollId, final @RequestParam Long sessionId,
                                final @RequestParam String format, final @RequestParam String separator, final @RequestParam Boolean dereferenceAnswerPossibilities,
                                final @RequestBody List<FilterCmd> filterList , final @RequestHeader int timeZoneOffset)
            throws JsonProcessingException, FileNotFoundException, UnsupportedDataTypeException {
        ZoneId timeZone = ZoneId.ofOffset("UTC", ZoneOffset.ofHoursMinutes(timeZoneOffset / 60 , timeZoneOffset % 60));
        final FilterCmd cmd = new FilterCmd();
        cmd.setBasePollId(pollId);
        cmd.setBaseQuestionIds(Collections.singletonList(-1L));
        final Statistics statistics = new Statistics(questionService, pollService, pollResultService, categoryService,
                consistencyQuestionService, sessionService, timeZone, cmd);
        if (sessionId < 0) {
            if (sessionId > -2) {
                statistics.loadFilter(filterList);
            }
        } else {
            statistics.loadSessionFilters(sessionId);
        }
        final List<PollResult> results = statistics.filteredResults();
        if (format.equals("csv")) {
            final String csv = exportService.toCSVManual(results, pollService.getPoll(pollId), separator, dereferenceAnswerPossibilities);
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

