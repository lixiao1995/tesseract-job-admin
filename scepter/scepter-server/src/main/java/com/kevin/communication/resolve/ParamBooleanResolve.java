package com.kevin.communication.resolve;


import com.kevin.communication.core.context.BeatContext;

/**
 * @author kevin
 */
public class ParamBooleanResolve implements IResolve<Boolean> {

	@Override
	public Class<Boolean> getResolveClass() {
		return Boolean.class;
	}

	@Override
	public Boolean resolve(BeatContext context, String paramName) {
		return Boolean.valueOf(Boolean.parseBoolean(context.getUrlParams().get(paramName)));
	}

}
