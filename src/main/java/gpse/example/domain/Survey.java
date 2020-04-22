
package gpse.example.domain;

import java.time.LocalDateTime;

class Survey{

    User creator;
    LocalDateTime creationDate;
    User lastEditor;
    LocalDateTime lastChanges;
    LocalDateTime deactivationTime;
    LocalDateTime activationTime;

    public Survey(User creator, LocalDateTime creationDate, User lastEditor, LocalDateTime lastChanges, LocalDateTime deactivationTime, LocalDateTime activationTime) {
        this.creator = creator;
        this.creationDate = creationDate;
        this.lastEditor = lastEditor;
        this.lastChanges = lastChanges;
        this.deactivationTime = deactivationTime;
        this.activationTime = activationTime;
    }
}
