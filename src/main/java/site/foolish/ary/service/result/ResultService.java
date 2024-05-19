package site.foolish.ary.service.result;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.foolish.ary.domain.member.Member;
import site.foolish.ary.domain.result.Question;
import site.foolish.ary.domain.result.Result;
import site.foolish.ary.domain.review.Review;
import site.foolish.ary.domain.review.ReviewList;
import site.foolish.ary.repository.result.ResultRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ResultService {
    private final ResultRepository resultRepository;

    public Result loadResult(String resultId) {

        Optional<Result> result = resultRepository.findById(resultId);

        return result.orElseGet(() -> Result.builder().build());
    }

    public Result createResult(Member member, String reviewId) {
        List<Question> questionList = new ArrayList<>();

        Result result = Result.builder()
                .member(member)
                .reviewId(reviewId)
                .questionList(questionList)
                .createdAt(new Date())
                .updatedAt(new Date())
                .build();

        resultRepository.save(result);

        return result;
    }

    public Result updateResult(String resultId, Question question) {

        Optional<Result> result = resultRepository.findById(resultId);

        if(result.isPresent()) {
            List<Question> questionList = result.get().getQuestionList();
            questionList.add(question);

            Result updatedResult = result.get();
            updatedResult.setQuestionList(questionList);
            updatedResult.setUpdatedAt(new Date());

            resultRepository.save(updatedResult);

            return result.get();
        } else {
            return null;
        }
    }

    public boolean deleteResult(String resultId) {
        resultRepository.deleteById(resultId);

        return resultRepository.findById(resultId).isEmpty();
    }

    public List<Result> findResultsByMember(Member member) {
        return resultRepository.findAllByMember(member);
    }

    public List<Result> findRecentResults() {
        return resultRepository.findBy(PageRequest.of(0, 5));
    }

}
