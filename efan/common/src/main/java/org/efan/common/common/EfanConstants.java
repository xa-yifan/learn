package org.efan.common.common;

/**
 * 常量类
 */
public class EfanConstants {
    private EfanConstants() {
    }

    public static final String REQUEST_VO = "requestVO";
    public static final String APP_ID="appId";
    public static final String TIME_TAMP="timestamp";
    public static final String SIGN="sign";
    public static final String DUBBO_PARAMS="dubbo_params";
    /**
     * 负载均衡
     */
    public interface Loadbalance {
        /**
         * 默认的随机
         */
        String RANDOM="random";
        /**
         * 一致性 Hash
         */
        String HASH="hash";
        String CONSISTENT_HASH="consistenthash";
        /**
         * 轮询
         */
        String ROUND_ROBIN="roundRobin";
    }
    public interface Time{
        /**
         * 请求过期时间1分钟
         */
        int DELAY = 1;
    }
    /**
     * 请求结果响应码
     */
    public enum Code{
        /**
         * 请求成功
         */
        SUCCESS(0,""),
        /**
         * 操作异常
         */
        FAIL(-1,"操作失败"),
        PARAM_ERROR(-2,"您的参数错误,请检查相关文档!"),
        TIME_ERROR(-3,"您的时间参数错误或者已经过期!"),
        BODY_ERROR(-5,"body不能为空"),
        TYPE_ERROR(400,"ContentType类型错误"),
        SIGN_IS_NOT_PASS(401, "签名未通过!"),
        ;


        private int code;
        private String msg;
        Code(int code, String msg){
            this.code=code;
            this.msg=msg;
        }
        public int getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }
    }
}
