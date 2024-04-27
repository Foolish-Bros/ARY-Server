package site.foolish.ary.repository.result;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import site.foolish.ary.domain.member.Member;
import site.foolish.ary.domain.review.Review;

import java.util.List;

@Repository
public interface ResultRepository extends MongoRepository<Review, String> {

    public List<Review> findByMember(Member member);
}
