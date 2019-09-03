# scepter
洛基权杖，netty-plus更名
# netty-plus
#### 服务通信框架
> scepter-client 客户端通信服务框架

> scepter-server 服务端通信服务框架

> scepter-message-protocol 通信消息协议框架

> spring-boot-starter-scepter-server 支持spring boot

> spring-boot-starter-scepter-client 支持spring boot

> scepter-sample 测试项目

#### 如何使用

#### 1.如果直接开启服务端，则使用如下代码：
```java
//初始化配置信息
SocketServerConfig serviceConfig = new SocketServerConfig();
serviceConfig.setBasePackages("com.kevin.scepter.server.test.server.command");
serviceConfig.setPort(9527);
serviceConfig.setProxyFactory(new DefaultProxyFactory());

NettyBootstrap boostrap = new NettyBootstrap(serviceConfig);
boostrap.start();
```

#### 2.使用spring boot：
maven引入jar包

```maven
<dependency>
    <artifactId>spring-boot-starter-scepter-server</artifactId>
    <groupId>com.kevin</groupId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

application.yml配置如下：

```yml
socket: 
  server:
    enable: true
    port: 9527
    basePackages: com.kevin.springboot.scepter.server.test.command
    readerIdleTime : 20 #设定读空闲时间，如果N秒内没有获取到Client的读取时间，则将Client踢出(可选，默认20)
    slowMethod : true #是否打印慢接口(可选，默认false)
    slowMethodMillis : 1000 #慢接口的阈值(可选，默认1000)
```

#### 3.client端开启
maven引入jar包

```maven
<dependency>
	<groupId>com.kevin</groupId>
	<artifactId>scepter-client</artifactId>
	<version>0.0.1-SNAPSHOT</version>
</dependency>
```

Client端开启服务代码：

```java
 SocketClientConfig config = new SocketClientConfig();
        config.setDeviceId("100001");
        config.setBasePackages("com.kevin.scepter.client.test.command");
        config.setServerHost("127.0.0.1");
        config.setServerPort(9527);
        config.setRequestTimeout(300);
        config.setWriterIdleTime(5);
        config.setClientPort(1100);

        System.out.println("连接服务端开启");
        ClientBootstrap client = new ClientBootstrap(config);
        client.start();


        Thread.sleep(5000);

        while (true) {
            try {
                String json = "{'deviceCode':'asdfsdfsd','userUnique':'asdfsdfsd'}";
                String result = Global.getInstance().resolve(ITestCommand.class).getTask(json);
                System.out.println("getTask接口返回的数据为:" + result);
                Global.getInstance().resolve(ITestCommand.class).sayHello();
//				Global.getInstance().resolve(ITestCommand.class).sayHello();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Thread.sleep(2000L);
        }
```

客户端如何调用PC Server远程接口

```java
@ProxyModule //添加动态代理标注
@Controller //标注为远程接口
public interface IServiceRemote {
	
	//远程接口URL
	@RequestMapping("/api/test/123")
	public List<String> sayHello(@Param("aa") String x , @Param("b") int b);//@Param请求的参数
	
	//异步接口，注意，List<String>将返回的永远是空，仅作为返回对象的解析使用。回调将在IAsyncCallBackListener中传递。
	//如果IAsyncCallBackListener中处理方法return false，则不会传递给第二个IAsyncCallBackListener。IAsyncCallBackListener可以为任意参数位置。
	//CallBackEvent内部有是否成功的字段。。
	@RequestMapping(value="/api/test/911" , method = RequestMethod.ASYNC)
	public List<String> sayAsyncHello(IAsyncCallBackListener callback , @Param("aa") String x , @Param("b") int b , IAsyncCallBackListener callback2);
	
	//支持Future模式
	@RequestMapping(value="/api/test/888")
	public Future sayFuture(@Param("aa") String x , @Param("b") int b);

}
```
类和方法说明：<br/>
@ProxyModule 必须加，标注为自动代理<br/>
@Controller 标注为远程接口<br/>
@RequestMapping	请求的API接口映射<br/>
<br/>
调用参数说明：<br/>
1.如果调用的是基本对象，或者多参数方法，则必须要加@Param("value") value代表请求的参数名称<br/>
2.如果调用没有参数，则无须添加任何注解<br/>
3.如果调用的是复杂对象类型，并且有且仅有一个参数，则无须添加@Param注解，自动请求的是此对象的JSON序列化后的参数体<br/>
<br/>

```java
//客户端Future模式的调用方式

IServiceRemote serviceRemote = Global.getInstance().resolve(IServiceRemote.class);
		
Future f = serviceRemote.sayFuture("Hello", 520);
long s = System.currentTimeMillis();
try {
	JSONArray jsonArray = f.get(JSONArray.class);
	for(Object o : jsonArray) {
		System.out.println(o);
	}
} catch (RemoteException e1) {
	e1.printStackTrace();
}
long e = System.currentTimeMillis();
System.out.println("总计花费：" + (e - s) + "毫秒!");

```

如何使用动态代理：<br/>

```java
//接口类
public interface ITestCommand {
	
	public void sayHello();

}
```

```java
//实现类，注意远程API类和此动态代理类可以不放在一个包下。
@ProxyModule(from=ITestCommand.class)//from必须填写接口名称
public class TestCommandImpl implements ITestCommand {
	
	@Inject//注入远程代理对象
	private IServiceRemote serviceRemote;

	@Override
	public void sayHello() {
		//调用远程代理方法
		System.out.println(serviceRemote.sayHello("abc" , 101));
	}

}

```

如果不想写 Command接口和实现类等，可以通过全局对象拿到远程调用实体

```java
Global.getInstance().resolve(ITestCommand.class).sayHello();
```


2019-08-21
实现自定义心跳类接口
服务端配置添加
```java
SocketServerConfig serviceConfig = new SocketServerConfig();
serviceConfig.setBasePackages("com.kevin.scepter.server.test.server.command");
serviceConfig.setPort(9527);
serviceConfig.setProxyFactory(new DefaultProxyFactory());
//自定义心跳类处理类
serviceConfig.setMessageProcessor(new SocketMessageProcessor());

NettyBootstrap boostrap = new NettyBootstrap(serviceConfig);
boostrap.start();
```
服务端配置
```java
SocketClientConfig config = new SocketClientConfig();
config.setDeviceId("100001");
config.setBasePackages("com.kevin.scepter.client.test.command");
config.setServerHost("127.0.0.1");
config.setServerPort(9527);
config.setRequestTimeout(300);
config.setWriterIdleTime(5);
config.setClientPort(1100);
config.setClientHeartBeatHandler(new ClientHeartBeatHandler());//添加自定义处理类
System.out.println("连接服务端开启");
ClientBootstrap client = new ClientBootstrap(config);
client.start();


Thread.sleep(5000);

while (true) {
    try {
        String json = "{'deviceCode':'asdfsdfsd','userUnique':'asdfsdfsd'}";
        String result = Global.getInstance().resolve(ITestCommand.class).getTask(json);
        System.out.println("getTask接口返回的数据为:" + result);
        Global.getInstance().resolve(ITestCommand.class).sayHello();
//				Global.getInstance().resolve(ITestCommand.class).sayHello();
    } catch (Exception e) {
        e.printStackTrace();
    }
    Thread.sleep(2000L);
}

```

2019-08-22
添加hessian等协议支持，代码示例

```java
//客户端代码
public static void main(String[] args) throws IOException, InterruptedException {
        SocketClientConfig config = new SocketClientConfig();
        config.setDeviceId("100001");
        config.setBasePackages("com.kevin.scepter.client.test.command");
        config.setServerHost("127.0.0.1");
        config.setServerPort(9527);
        config.setRequestTimeout(300);
        config.setWriterIdleTime(5);
        config.setSerializeType(SerializeType.HESSIAN2); //配置运行协议
//        config.setClientPort(1100);
        config.setClientHeartBeatHandler(new ClientHeartBeatHandler());

        System.out.println("连接服务端开启");
        ClientBootstrap client = new ClientBootstrap(config);
        client.start();


        Thread.sleep(5000);

        while (true) {
            try {
                String json = "{'deviceCode':'asdfsdfsd','userUnique':'asdfsdfsd'}";
                String result = Global.getInstance().resolve(ITestCommand.class).getTask(json);
                System.out.println("getTask接口返回的数据为:" + result);
                Global.getInstance().resolve(ITestCommand.class).sayHello();
//				Global.getInstance().resolve(ITestCommand.class).sayHello();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Thread.sleep(2000L);
        }
    }
```
```java
//服务端代码
public static void main(String[] args) throws InterruptedException {
		//初始化配置信息
		SocketServerConfig serviceConfig = new SocketServerConfig();
		serviceConfig.setBasePackages("com.kevin.scepter.server.test.server.command");
		serviceConfig.setPort(9527);
		serviceConfig.setProxyFactory(new DefaultProxyFactory());
		serviceConfig.setMessageProcessor(new SocketMessageProcessor());
		serviceConfig.setSerializeType(SerializeType.HESSIAN2);
		NettyBootstrap boostrap = new NettyBootstrap(serviceConfig);
		boostrap.start();
		Map<String, Session> sessionMap = SessionManager.getInstance().getSessionMap();
		sessionMap.entrySet().stream().forEach(stringSessionEntry -> {
			Session session = stringSessionEntry.getValue();
			session.getCtx().channel().writeAndFlush("");
		});
	}
```

## 其他

如有问题，随时联系。
