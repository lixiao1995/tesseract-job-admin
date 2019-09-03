package com.kevin.communication.resolve;


import com.kevin.communication.core.context.BeatContext;

/**
 * @author kevin
 */
public class ParamIntegerResolve implements IResolve<Integer> {

	@Override
	public Class<Integer> getResolveClass() {
		return Integer.class;
	}

	@Override
	public Integer resolve(BeatContext context, String paramName) {
		return Integer.valueOf(Integer.parseInt(context.getUrlParams().get(paramName)));
	}

}
