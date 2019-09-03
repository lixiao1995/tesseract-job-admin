package com.kevin.communication.resolve;


import com.kevin.communication.core.context.BeatContext;

/**
 * @author kevin
 */
public class ParamShortResolve implements IResolve<Short> {

	@Override
	public Class<Short> getResolveClass() {
		return Short.class;
	}

	@Override
	public Short resolve(BeatContext context, String paramName) {
		return Short.valueOf(Short.parseShort(context.getUrlParams().get(paramName)));
	}

}
