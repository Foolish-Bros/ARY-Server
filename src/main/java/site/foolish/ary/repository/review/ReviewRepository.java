package site.foolish.ary.repository.review;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import site.foolish.ary.domain.review.Review;

@Repository
public interface ReviewRepository extends MongoRepository<Review, Long> {
}
