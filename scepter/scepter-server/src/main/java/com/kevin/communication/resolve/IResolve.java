package com.kevin.communication.resolve;

import com.kevin.communication.core.context.BeatContext;

/**
 * @author: kevin
 * @description: 基础的解析接口
 * @updateRemark: 修改内容(每次大改都要写修改内容)
 * @date: 2019-07-29 19:06
 */
public interface IResolve<T> {
	
	/**
	 * 获得需要解析的Class
	 * @return Class<T>
	 */
	Class<T> getResolveClass();
	
	/**
	 * 解析，返回真正的实体
	 * @param context - BeatContext
	 * @param paramName - 参数名称
	 * @return T
	 */
	 T resolve(BeatContext context, String paramName);

}
