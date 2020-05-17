package gpse.umfrato.domain.category;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    public Category findCategoryByPollId(final Long pollId);
    public List<Category> findCategoriesByPollId(final Long pollId);
}
