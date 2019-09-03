package com.kevin.scepter.client.test.command;


import com.kevin.scepter.client.core.hotkey.Controller;
import com.kevin.scepter.client.core.hotkey.Param;
import com.kevin.scepter.client.core.hotkey.ProxyModule;
import com.kevin.scepter.client.core.hotkey.RequestMapping;

import java.util.List;

@ProxyModule
@Controller
public interface IServiceRemote {
	
	@RequestMapping("/api/test/123")
	public List<String> sayHello(@Param("aa") String x, @Param("b") int b);
	
	@RequestMapping("/api/getTask")
	public String getTask(String json);

}
