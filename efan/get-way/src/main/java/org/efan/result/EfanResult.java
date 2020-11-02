package org.efan.result;


import lombok.Data;
import org.efan.common.common.EfanConstants;

import java.io.Serializable;

/**
 * 返回包装类
 * @author liuf
 * @date 2019/12/11 15:01
 */
@Data
public class EfanResult<T> implements Serializable {
    private static final long serialVersionUID = 5778573516446596671L;
    private int code;
    private String message;
    private T data;

    public EfanResult() {
    }

    private EfanResult(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static EfanResult<?> returnVaule(EfanConstants.Code code){
        return new EfanResult<>(code.getCode(), code.getMsg(), "");
    }
}
