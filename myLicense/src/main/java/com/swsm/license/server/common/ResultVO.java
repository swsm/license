package com.swsm.license.server.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;


/**
 * 统一请求返回结果
 */
@ApiModel("统一返回VO")
@AllArgsConstructor
@NoArgsConstructor
public class ResultVO<T> {

    /**
     * 请求状态码
     */
    @ApiModelProperty(value = "请求状态码，200-正确，其它-错误")
    private Integer status;

    /**
     * 请求状态描述
     */
    @ApiModelProperty(value = "请求状态描述")
    private String message;

    /**
     * 响应数据，可以为空
     */
    @ApiModelProperty("响应数据：成功时返回需要的数据，失败时返回详细原因或为null")
    private T data;

    // 函数

    /**
     * 请求成功，返回ResultVO，但data为空
     *
     * @param <T> 预期类型
     * @return 成功的空
     */
    public static <T> ResultVO<T> getSuccess() {
        return new ResultVO<>(200, "请求成功", null);
    }

    /**
     * 请求成功，返回ResultVO，有data字段
     *
     * @param <T> 数据类型
     * @param t   数据
     * @return 成功的数据
     */
    public static <T> ResultVO<T> getSuccess(T t) {
        return new ResultVO<>(200, "请求成功", t);
    }

    /**
     * 根据业务方需要，追加的请求成功，返回message，有data字段
     *
     * @param message 成功消息
     * @param <T>     预期类型
     * @return 成功的空
     */
    public static <T> ResultVO<T> getSuccess(String message, T data) {
        return new ResultVO<>(200, message, data);
    }

    /**
     * 请求参数错误
     *
     * @param <T>    预期类型
     * @param detail 错误详情
     * @return 错误的详情
     */
    public static <T> ResultVO<T> getParamsError(String detail) {
        return new ResultVO<>(400, detail, null);
    }

    /**
     * 自定义错误信息
     *
     * @param msg  错误详情
     * @return 错误的详情
     */
    public static <T> ResultVO<T> getError(String msg) {
        return new ResultVO<>(500, msg, null);
    }

    /**
     * 自定义错误
     *
     * @param <T>  预期类型
     * @param code 错误代码
     * @param msg  错误详情
     * @return 错误的详情
     */
    public static <T> ResultVO<T> getError(int code, String msg) {
        return new ResultVO<>(code, msg, null);
    }


    /**
     * 服务器错误
     *
     * @param <T>    预期类型
     * @param detail 错误详情
     * @return 错误的详情
     */
    public static <T> ResultVO<T> getServerError(String detail) {
        ResultVO<T> vo = new ResultVO<>(500, detail, null);
        vo.setStatus(500);
        vo.setMessage(detail);
        return vo;
    }

    /**
     * 第三方服务器返回错误
     *
     * @param <T> 数据类型
     * @param t   第三方服务器返回的数据
     * @return 错误的详情和数据
     */
    public static <T> ResultVO<T> getThirdServerError(T t) {
        return new ResultVO<>(500, "服务器出错", t);
    }

    public Integer getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ResultVO{" +
                "status=" + status +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
