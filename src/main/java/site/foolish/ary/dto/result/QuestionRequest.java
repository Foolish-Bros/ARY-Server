package site.foolish.ary.dto.result;


import lombok.Data;

@Data
public class QuestionRequest {
    private String reviewId;
    private String question;
    private String answer;
}
