package gpse.umfrato.domain.category;

import gpse.umfrato.domain.poll.PollRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.persistence.EntityNotFoundException;

@Service
public class CategoryServiceImpl implements CategoryService {
    /**
     * Declare a poll repository.
     */
    private final PollRepository pollRepository;
    /**
     * Declare a category repository.
     */
    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryServiceImpl(final CategoryRepository categoryRepository, final PollRepository pollRepository) {
        this.categoryRepository = categoryRepository;
        this.pollRepository = pollRepository;
    }
    @Override
    public Category createCategory(final String name, final long pollId) {
        final Category category = new Category(name, pollId);
        category.setPollId(pollId);
        pollRepository.findById(pollId).orElseThrow(EntityNotFoundException::new).getCategoryList().add(category);
        categoryRepository.save(category);

        return category;
    }

}
