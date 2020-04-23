
package gpse.umfrato.domain;

import java.time.LocalDateTime;

class Survey{

    User creator;
    LocalDateTime creationDate;
    User lastEditor;
    LocalDateTime lastChanges;
    LocalDateTime deactivationTime;
    LocalDateTime activationTime;
}
