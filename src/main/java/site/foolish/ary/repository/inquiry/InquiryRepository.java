package site.foolish.ary.repository.inquiry;

import org.springframework.data.mongodb.repository.MongoRepository;
import site.foolish.ary.domain.inquiry.Inquiry;

public interface InquiryRepository extends MongoRepository<Inquiry, String> {
}
