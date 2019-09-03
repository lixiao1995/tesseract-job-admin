package com.kevin.communication.resolve;


import com.kevin.communication.core.context.BeatContext;

/**
 * @author kevin
 */
public class ParamDoubleResolve implements IResolve<Double> {

	@Override
	public Class<Double> getResolveClass() {
		return Double.class;
	}

	@Override
	public Double resolve(BeatContext context, String paramName) {
		return Double.valueOf(Double.parseDouble(context.getUrlParams().get(paramName)));
	}

}
