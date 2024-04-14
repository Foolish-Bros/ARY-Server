package site.foolish.ary.response.dto;

import lombok.Data;
import site.foolish.ary.response.StatusEnum;

@Data
public class Message {

    private StatusEnum status;
    private String message;
    private Object data;

    public Message() {
        this.status = StatusEnum.BAD_REQUEST;
        this.data = null;
        this.message = null;
    }
}

