package com.kevin.communication.resolve;


import com.kevin.communication.core.context.BeatContext;

/**
 * @author kevin
 */
public class ParamLongResolve implements IResolve<Long> {

	@Override
	public Class<Long> getResolveClass() {
		return Long.class;
	}

	@Override
	public Long resolve(BeatContext context, String paramName) {
		return Long.valueOf(Long.parseLong(context.getUrlParams().get(paramName)));
	}

}
