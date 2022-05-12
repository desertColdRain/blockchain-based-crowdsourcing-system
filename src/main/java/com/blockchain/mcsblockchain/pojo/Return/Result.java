package com.blockchain.mcsblockchain.pojo.Return;

import io.swagger.models.auth.In;

public class Result {
    /**
     * 返回信息
     */
    private String msg;



    /**
     * 数据是否正常请求
     */
    private Integer code;

    private Object data;

    public Result() {
    }
    public Result(String msg, Integer code, Object data) {
        this.msg = msg;
        this.code = code;
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}

