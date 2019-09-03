package com.kevin.communication.resolve;


import com.alibaba.fastjson.JSONObject;
import com.kevin.communication.core.context.BeatContext;
import com.kevin.message.protocol.utility.FastJsonHelper;

/**
 * @author kevin
 */
public class JSONObjectResolve implements IResolve<JSONObject> {
	
	private static final JSONObject EMPTY_OBJECT = new JSONObject();

	@Override
	public Class<JSONObject> getResolveClass() {
		return JSONObject.class;
	}

	@Override
	public JSONObject resolve(BeatContext context , String paramName) {
		String d = context.getRequest().getBody();
		if(d == null) {
			return EMPTY_OBJECT;
		}
		
		return FastJsonHelper.toJsonObject(d);
	}

}
