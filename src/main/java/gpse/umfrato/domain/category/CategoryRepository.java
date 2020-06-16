package gpse.umfrato.domain.category;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category findCategoryByPollId(final Long pollId);
    List<Category> findCategoriesByPollId(final Long pollId);
    void deleteCategoriesByPollId(final Long pollId);
}
