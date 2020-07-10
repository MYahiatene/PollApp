package gpse.umfrato.domain.evaluation.session;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import gpse.umfrato.domain.evaluation.filter.FilterData;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long sessionId;

    private Long pollId;

    private String sessionTitle;

    @OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<FilterData> filterList;

    @ElementCollection
    private List<String> diagramFormat;

    @ElementCollection
    private List<String> diagramOptions;

    private ZonedDateTime lastEdited;

    private String lastUsername;
}
