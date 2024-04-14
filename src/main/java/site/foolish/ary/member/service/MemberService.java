package site.foolish.ary.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.foolish.ary.member.dto.JoinRequest;
import site.foolish.ary.member.dto.LoginRequest;
import site.foolish.ary.member.domain.Member;
import site.foolish.ary.member.repository.MemberRepository;

import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public boolean checkEmailDuplicated(String email) {
        return memberRepository.existsByEmail(email);
    }

    public void join(JoinRequest joinRequest) {
        memberRepository.save(joinRequest.toEntity());
    }

    public Member login(LoginRequest loginRequest) {
        Member findMember = memberRepository.findByEmail(loginRequest.getEmail());

        if(findMember == null) return null;
        if(!bCryptPasswordEncoder.matches(loginRequest.getPassword(), findMember.getPassword())) return null;
//        if(!findMember.getPassword().equals(loginRequest.getPassword())) return null;

        return findMember;
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

}
