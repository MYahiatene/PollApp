package gpse.umfrato.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
public class Poll {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    @Getter
    private Long id;

    @Column
    @Getter
    @Setter
    private String pollname;

    protected Poll() {

    }

    public Poll(final String pollname) {
        this.pollname = pollname;
    }
}
