package com.yaheen.pdaapp.bean;

import java.io.Serializable;

public class BindBean implements Serializable {

    private static final long serialVersionUID = -6611564855694318197L;
    /**
     * result : true
     * msg : 更新成功
     */

    private boolean result;
    private String msg;

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
