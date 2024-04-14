package site.foolish.ary.repository.member;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import site.foolish.ary.domain.member.Member;

@Repository
public interface MemberRepository extends MongoRepository<Member, Long> {

    /**
     * 이메일을 갖는 객체가 존재하는지 확인하는 메소드
     * @param email 존재하는지 확인할 email
     * @return email이 존재 -> true / 존재하지 않음 -> false
     */
    boolean existsByEmail(String email);

    /**
     * 해당하는 email을 갖고 있는 Member 객체를 반환하는 메소드
     * @param email 반환할 객체를 찾기 위한 email
     * @return 해당하는 Member 객체
     */
    Member findByEmail(String email);

//    Member findByUsername(String username);
}
