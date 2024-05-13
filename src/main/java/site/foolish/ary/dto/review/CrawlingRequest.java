package site.foolish.ary.dto.review;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
public class CrawlingRequest {
    // 1 : coupang
    // 2 : 11st
    // 3 : auction
    private int type;
    private String url;
}
