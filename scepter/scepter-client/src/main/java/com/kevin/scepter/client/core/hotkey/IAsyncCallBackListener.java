package com.kevin.scepter.client.core.hotkey;

/**
 * @author: kevin
 * @description: 异步接口信息返回回调处理接口
 * @updateRemark: 修改内容(每次大改都要写修改内容)
 * @date: 2019-07-30 10:50
 */
public interface IAsyncCallBackListener {

    /**
     * 回调函数将在异步方法成功获取数据后执行
     *
     * @param event - 回调事件
     * @return boolean 如果为false，则不执行后面的IAsyncCallBackListener对象。
     */
    public boolean callback(CallBackEvent event);

}
