package gpse.umfrato.domain.category;

import gpse.umfrato.domain.question.Question;

public interface CategoryService {
    Category createCategory(final String name, final long pollId);
}
