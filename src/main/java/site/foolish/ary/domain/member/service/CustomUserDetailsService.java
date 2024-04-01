package site.foolish.ary.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import site.foolish.ary.domain.member.entity.CustomUserDetails;
import site.foolish.ary.domain.member.entity.Member;
import site.foolish.ary.domain.member.repository.MemberRepository;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Member member = memberRepository.findByUsername(username);
        if(member != null) {
            return new CustomUserDetails(member);
        }
        return null;
    }
}