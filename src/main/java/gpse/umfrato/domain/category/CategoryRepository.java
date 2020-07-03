package gpse.umfrato.domain.category;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * The repository in which categories are stored.
 */
public interface CategoryRepository extends JpaRepository<Category, Long> {
    /**
     * This method returns all the categories belonging to one poll id.
     * @param pollId the poll id
     * @return returns all the categories as a list
     */
    List<Category> findCategoriesByPollId(final Long pollId);

}
