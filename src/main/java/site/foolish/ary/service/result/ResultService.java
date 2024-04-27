package site.foolish.ary.service.result;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.foolish.ary.domain.member.Member;
import site.foolish.ary.domain.result.Result;
import site.foolish.ary.repository.result.ResultRepository;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ResultService {

    private final ResultRepository resultRepository;

    public boolean save(Member member, Result result) {
        return false;
    }

    public boolean delete(Member member, Result result) {
        return false;
    }

    public boolean update(Member member, Result result) {
        return false;
    }

    public List<Result> getResultList(Member member) {
        return null;
    }

    public Result getResult(Member member, String id) {
        return null;
    }
}
