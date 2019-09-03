package com.kevin.scepter.client.core.hotkey;

/**
 * @author: kevin
 * @description: 异步回调事件对象
 * @updateRemark: 修改内容(每次大改都要写修改内容)
 * @date: 2019-07-30 10:48
 */
public class CallBackEvent {

    /**
     * 是否成功回调
     */
    private boolean success;

    /**
     * 返回值，如果success为false，则为异常信息；否则则是接口返回值，注意判断一下。
     */
    private transient Object returnObject;

    public CallBackEvent(Exception e) {
        this.success = false;
        this.returnObject = e;
    }

    public CallBackEvent(Object returnObject) {
        if (returnObject != null && returnObject instanceof Exception) {
            this.success = false;
        } else {
            this.success = true;
        }
        this.returnObject = returnObject;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Object getReturnObject() {
        return returnObject;
    }

    public void setReturnObject(Object returnObject) {
        this.returnObject = returnObject;
    }

}
