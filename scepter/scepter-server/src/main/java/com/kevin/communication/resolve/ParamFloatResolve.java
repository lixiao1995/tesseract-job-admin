package com.kevin.communication.resolve;


import com.kevin.communication.core.context.BeatContext;

/**
 * @author kevin
 */
public class ParamFloatResolve implements IResolve<Float> {

	@Override
	public Class<Float> getResolveClass() {
		return Float.class;
	}

	@Override
	public Float resolve(BeatContext context, String paramName) {
		return Float.valueOf(Float.parseFloat(context.getUrlParams().get(paramName)));
	}

}
