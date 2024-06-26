package site.foolish.ary.service.member.oauth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import site.foolish.ary.domain.member.Member;
import site.foolish.ary.domain.member.Role;
import site.foolish.ary.domain.member.oauth.CustomOauth2UserDetails;
import site.foolish.ary.domain.member.oauth.OAuth2UserInfo;
import site.foolish.ary.domain.member.oauth.google.GoogleUserDetails;
import site.foolish.ary.domain.member.oauth.kakao.KakaoUserDetails;
import site.foolish.ary.domain.member.oauth.naver.NaverUserDetails;
import site.foolish.ary.repository.member.MemberRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOauth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.info("getAttributes : ", oAuth2User.getAttributes());

        String provider = userRequest.getClientRegistration().getRegistrationId();

        OAuth2UserInfo oAuth2UserInfo = null;

        // 다른 소셜 서비스 로그인을 위한 구분 -> Google
        if(provider.equals("google")) {
            log.info("google login");
            oAuth2UserInfo = new GoogleUserDetails(oAuth2User.getAttributes());
        } else if (provider.equals("kakao")) {
            log.info("kakao login");
            oAuth2UserInfo = new KakaoUserDetails(oAuth2User.getAttributes());
        } else if (provider.equals("naver")) {
            log.info("naver login");
            oAuth2UserInfo = new NaverUserDetails(oAuth2User.getAttributes());
        }

        String providerId = oAuth2UserInfo.getProviderId();
        String email = oAuth2UserInfo.getEmail();
        String name = oAuth2UserInfo.getName();

        Member findMember = memberRepository.findByEmail(email);
        Member member;

        if(findMember == null) {
            member = Member.builder()
                    .email(email)
                    .name(name)
                    .provider(provider)
                    .providerId(providerId)
                    .role(Role.USER)
                    .build();
            memberRepository.save(member);
        } else {
            member = findMember;
        }

        return new CustomOauth2UserDetails(member, oAuth2User.getAttributes());

    }

}
