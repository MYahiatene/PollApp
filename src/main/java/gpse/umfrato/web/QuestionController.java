package gpse.umfrato.web;

import gpse.umfrato.domain.cmd.QuestionCategoryChangeCmd;
import gpse.umfrato.domain.cmd.QuestionCmd;
import gpse.umfrato.domain.question.Question;
import gpse.umfrato.domain.question.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

/**
 * The Question controller used to process question specific requests.
 */
@RequestMapping(value = "/api", method = RequestMethod.GET)
@RestController
@CrossOrigin(origins = "http://127.0.0.1:8080")
public class QuestionController {

    private final QuestionService questionService;

    private static String uploadDirectory = System.getProperty("user.dir") + "/uploads/";

    /**
     * This class constructor initializes the services and repository.
     *
     * @param questionService the question service
     */
    @Autowired
    public QuestionController(final QuestionService questionService) {
        this.questionService = questionService;
    }

    /**
     * This method adds a question.
     *
     * @param questionCmd has the question and the poll id
     */
    @PreAuthorize("hasAnyAuthority('Admin','Creator', 'Editor')")
    @PostMapping("/addquestion")
    public Question addQuestion(final @RequestBody QuestionCmd questionCmd) {
        return questionService.addQuestion(questionCmd);
    }

    @PreAuthorize("hasAnyAuthority('Admin','Creator', 'Editor')")
    @PostMapping(value = "/upload")
    public String uploadMultimedia(@RequestParam("file") MultipartFile file) {

        File path = new File(uploadDirectory + file.getOriginalFilename());
        try {
            file.transferTo(path);
            return "Upload successful.";
        } catch (IOException e) {
            e.printStackTrace();
            return "Upload failed.";
        }
    }

    @PreAuthorize("hasAnyAuthority('Admin','Creator', 'Editor')")
    @GetMapping(value = "/download/{filename}")
    public Resource downloadMultimedia(@PathVariable String filename) throws IOException {
        Path filePath = Path.of(uploadDirectory + filename);
        UrlResource urlResource = null;
        try {
            urlResource = new UrlResource(filePath.toUri());
        } catch (MalformedURLException e) {
            throw new IOException(e);
        }
        return urlResource;

    }

    @PreAuthorize("hasAnyAuthority('Admin','Creator', 'Editor')")
    @PostMapping(value = "/removeFile/{filename}")
    public String removeMultimedia(@PathVariable String filename) {
        File path = new File(uploadDirectory + filename);

        if (path.exists()) {
            path.delete();
            return "Remove successul.";
        } else {
            return "Nothing to remove.";
        }
    }

    /**
     * This method deletes a question.
     *
     * @param questionCmd the Cmd of the question
     */
    @PreAuthorize("hasAnyAuthority('Admin','Creator', 'Editor')")
    @PutMapping("/removequestion")
    public void deleteQuestion(final @RequestBody QuestionCmd questionCmd) {
        questionService.removeQuestion(questionCmd.getCategoryId(), questionCmd.getQuestionId());
    }

    /**
     * This method gets a requested question.
     *
     * @param questionCmd has the poll id of the question
     * @return returns the requested question
     */
    @GetMapping("/getOneQuestion")
    public Question getQuestion(final @RequestBody QuestionCmd questionCmd) {

        return questionService.getQuestion(questionCmd.getPollId());
    }

    /**
     * This method returns a list of all questions in a specific category.
     *
     * @param categoryId The categoryId of a specific category.
     * @return returns a list of all questions in a specific category.
     */
    @GetMapping("/getallquestions")
    public List<Question> getAllQuestions(final @RequestParam long categoryId) {

        return questionService.getAllQuestions(categoryId);
    }

    /**
     * This method edits a specific question.
     *
     * @param questionCmd takes the necessary question Cmd object.
     * @return returns the specific question object.
     */
    @PreAuthorize("hasAnyAuthority('Admin','Creator', 'Editor')")
    @PutMapping("/editquestion")
    public Question editQuestion(final @RequestBody QuestionCmd questionCmd) {
        // System.out.println(questionCmd);
        return questionService.editQuestion(questionCmd);
    }

    /**
     * This method changes the category of a question.
     *
     * @param questionCategoryChangeCmd takes the questionCategoryChangeCmd object.
     * @return returns the specific question.
     */
    @PreAuthorize("hasAnyAuthority('Admin','Creator', 'Editor')")
    @PostMapping("/changeQuestionIndex")
    public Question changeQuestionIndex(final @RequestBody QuestionCategoryChangeCmd questionCategoryChangeCmd) {
        return questionService.changeCategory(Long.parseLong(questionCategoryChangeCmd.getQuestionId()),
            Long.parseLong(questionCategoryChangeCmd.getNewCategoryId()),
            Long.parseLong(questionCategoryChangeCmd.getNewIndex()));
    }
}
