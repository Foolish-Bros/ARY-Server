package site.foolish.ary.repository.result;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import site.foolish.ary.domain.member.Member;
import site.foolish.ary.domain.result.Result;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResultRepository  extends MongoRepository<Result, String> {
    Optional<Result> findById(String resultId);
    List<Result> findAllByMember(Member member);
    void deleteById(String resultId);

    @Query(sort = "{_id:  -1}")
    List<Result> findBy(Pageable pageable);
}
