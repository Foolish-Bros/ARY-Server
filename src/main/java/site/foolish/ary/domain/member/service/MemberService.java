package site.foolish.ary.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.foolish.ary.domain.member.dto.JoinRequest;
import site.foolish.ary.domain.member.dto.LoginRequest;
import site.foolish.ary.domain.member.entity.Member;
import site.foolish.ary.domain.member.repository.MemberRepository;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public boolean checkEmailDuplicated(String email) {
        return memberRepository.existsByEmail(email);
    }

    public void join(JoinRequest joinRequest) {
        memberRepository.save(joinRequest.toEntity());
    }

    public Member login(LoginRequest loginRequest) {
        Member findMember = memberRepository.findByEmail(loginRequest.getEmail());

        if(findMember == null) return null;
        if(!findMember.getPassword().equals(loginRequest.getPassword())) return null;

        return findMember;
    }

    public Member getLoginMemberById(Long memberId) {
        if(memberId == null) return null;

        Optional<Member> findMember = memberRepository.findById(memberId);
        return findMember.orElse(null);
    }

}
