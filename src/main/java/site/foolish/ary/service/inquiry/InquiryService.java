package site.foolish.ary.service.inquiry;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.foolish.ary.domain.inquiry.Inquiry;
import site.foolish.ary.domain.member.Member;
import site.foolish.ary.repository.inquiry.InquiryRepository;

import java.util.Date;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class InquiryService {
    private final InquiryRepository inquiryRepository;

    public Inquiry inquirySave(Member member, String content) {
        Inquiry inquiry = Inquiry.builder()
                .member(member)
                .content(content)
                .createdAt(new Date())
                .build();
        return inquiryRepository.save(inquiry);
    }

}
