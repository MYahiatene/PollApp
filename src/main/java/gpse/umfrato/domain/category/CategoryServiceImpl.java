package gpse.umfrato.domain.category;

import gpse.umfrato.domain.poll.PollRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final PollRepository pollRepository;

    @Autowired
    public CategoryServiceImpl(final CategoryRepository categoryRepository, final PollRepository pollRepository) {
        this.categoryRepository = categoryRepository;
        this.pollRepository = pollRepository;
    }

    @Override
    public Category addCategory(final String name, final long pollId) {
        final Category category = new Category(name, pollId);
        categoryRepository.save(category);
        return category;
    }


}
