package site.foolish.ary.service.member;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import site.foolish.ary.domain.member.CustomSecurityUserDetails;
import site.foolish.ary.domain.member.Member;
import site.foolish.ary.repository.member.MemberRepository;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Member member = memberRepository.findByEmail(email);
        if(member != null) {
            return new CustomSecurityUserDetails(member);
        }
        return null;
    }
}