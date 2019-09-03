package com.kevin.communication.resolve;


import com.kevin.communication.core.context.BeatContext;

/**
 * @author kevin
 */
public class ParamByteResolve implements IResolve<Byte> {

	@Override
	public Class<Byte> getResolveClass() {
		return Byte.class;
	}

	@Override
	public Byte resolve(BeatContext context, String paramName) {
		return Byte.valueOf(Byte.parseByte(context.getUrlParams().get(paramName)));
	}

}
