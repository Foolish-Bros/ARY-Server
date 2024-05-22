package site.foolish.ary.service.member;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.foolish.ary.domain.result.Result;
import site.foolish.ary.domain.review.ReviewList;
import site.foolish.ary.dto.member.JoinRequest;
import site.foolish.ary.dto.member.LoginRequest;
import site.foolish.ary.domain.member.Member;
import site.foolish.ary.repository.member.MemberRepository;
import site.foolish.ary.repository.result.ResultRepository;
import site.foolish.ary.repository.review.ReviewRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ReviewRepository reviewRepository;
    private final ResultRepository resultRepository;

    public boolean checkEmailDuplicated(String email) {
        return memberRepository.existsByEmail(email);
    }

    public Member login(LoginRequest loginRequest) {
        Member findMember = memberRepository.findByEmail(loginRequest.getEmail());

        if(findMember == null) return null;
        if(!bCryptPasswordEncoder.matches(loginRequest.getPassword(), findMember.getPassword())) return null;

        return findMember;
    }

    public boolean checkPassword(Member member, String password) {
        return bCryptPasswordEncoder.matches(password, member.getPassword());
    }

    public boolean changePassword(Member member, String oldPassword, String newPassword) {
        if(checkPassword(member, oldPassword)) {
            Member findMember = memberRepository.findById(member.getId()).orElseThrow(() -> new EntityNotFoundException(member.getId()));
            findMember.setPassword(bCryptPasswordEncoder.encode(newPassword));

            memberRepository.save(findMember);
            return true;
        } else {
            return false;
        }
    }

    public Member getLoginMemberByEmail(String email) {
        if(email == null) return null;

        Optional<Member> findMember = Optional.ofNullable(memberRepository.findByEmail(email));
        return findMember.orElse(null);
    }

    // BCryptPasswordEncoder 를 통해서 비밀번호 암호화 작업 추가한 회원가입 로직
    public void securityJoin(JoinRequest joinRequest){
        if(memberRepository.existsByEmail(joinRequest.getEmail())){
            return;
        }

        log.info(joinRequest.getPassword());

        joinRequest.setPassword(bCryptPasswordEncoder.encode(joinRequest.getPassword()));

        memberRepository.save(joinRequest.toEntity());
    }

    public boolean withdrawMember(Member member) {
        if(memberRepository.existsById(member.getId())){
            // 회원 탈퇴
            memberRepository.delete(member);
            member.setPassword("hidden");

            // 조회했던 리뷰 삭제
            List<ReviewList> reviews = reviewRepository.findAllByMember(member);
            reviewRepository.deleteAll(reviews);

            // 문답결과 삭제
            List<Result> results = resultRepository.findAllByMember(member);
            resultRepository.deleteAll(results);


            return true;
        } else {
            return false;
        }
    }

}
