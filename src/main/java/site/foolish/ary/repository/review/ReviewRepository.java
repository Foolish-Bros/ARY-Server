package site.foolish.ary.repository.review;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import site.foolish.ary.domain.member.Member;
import site.foolish.ary.domain.review.ReviewList;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends MongoRepository<ReviewList, String> {
    Optional<ReviewList> findById(String id);
    List<ReviewList> findAllByMember(Member member);
    void deleteById(String id);
}
