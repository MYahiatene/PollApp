package gpse.umfrato.domain.evaluation.Session;

import gpse.umfrato.domain.evaluation.filter.FilterData;
import gpse.umfrato.domain.user.User;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long sessionId;

    private Long pollId;

    private String sessionTitle;

    @OneToMany(orphanRemoval = true)
    private List<FilterData> filterList;

    @ElementCollection
    private List<String> diagramFormat;

    @ElementCollection
    private List<String> diagramOptions;

    private Date lastEdited;

    private String lastUsername;
}
