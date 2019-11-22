# 分布式任务调度平台 tesseract-job
## 开发目的：
### 一、简化任务调用
### 二、不依赖于第三方调度框架，如Quartz等
### 三、提供完整的权限系统，功能权限可控制到部门，页面权限可控制到按钮和菜单
### 四、可根据业务部门进行调度线程隔离，避免了由于某部门任务过多造成别的部门的影响
### 五、提供动态修改调度线程数，可根据任务压力调整线程池大小
### 六、调度端和执行端采用异步操作
### 七、提供任务失败，机器下线邮件报警
### 八、提供任务分片机制
### 九、前后端分离开发，前端采用VUE、Element-UI
### 十、超轻量级代码与架构，提供高扩展性，高性能分布式调度器
## 系统架构图：
![tesseract-job架构](https://github.com/tesseract-job/tesseract-job-admin/blob/master/architecture/TesseractJobProfile.jpg)
## 组件描述：
#### 1、Group
   Group为组，可以理解为部门，所有资源按部门隔离（Netty除外）
#### 2、MissfireThread
   一个独立运行线程，用于扫描超过时间的触发器并发送邮件通知
#### 3、ExecutorThread
   一个独立运行线程，用于扫描失去心跳的Executor Detail 并发送邮件通知 这里理解为执行任务机器即可
#### 4、ScheduleThread
   一个独立运行线程，为调度线程，通过提取执行触发器并放入TaskThreadPool线程池执行任务
#### 5、TaskThreadPool
   线程池，用于执行任务，创建执行请求发送给机器执行任务
#### 6、ExecutorThreadPool
   线程池，为客户端使用，Netty接收到执行请求后将任务扔给线程池执行并响应服务端接收到请求
#### 7、Netty Server
   netty的HTTP服务器，序列化采用Hessian，服务端和客户端均有一个
#### 8、Netty Client Pool
   netty client端的池子，用于缓存server端调度客户端的netty client 提高性能
#### 9、Ping Pong Thread
   客户端注册+心跳的组合操作线程，提供心跳与注册功能，server 依据此判断机器是否还活着  
## 项目模块划分：
#### 1、Actuator
   spring boot admin server 端
#### 2、Tesseract Job Admin  
   调度服务端，也包括web端，分为两块：1、tomcat 2、netty tomcat主要用于服务于web管理端，netty 用于调度任务端
#### 3、Tesseract Job Common  
   服务端和客户端公用的结构和逻辑
#### 4、Tesseract Job Client  
   客户端，用于接收调度端的请求，在执行完成后通知调度端
#### 5、Tesseract Job Simple  
   一个使用例子，提供非spring项目和spring项目例子，spring boot和spring相同只是配置不同，参考spring官方手册
## 高可用说明：
   调度端采用DB的行锁，所以请务必按照sql脚本的引擎创建lock表，对于并发访问的结构已经采用db锁，所以可以部署多台调度端，采用NGINX来做负载均衡。至于client端也可部署多台，调度端支持任务失败重试。
## 使用方法：
##### 1、进入web控制台
##### 2、创建组（部门）
##### 3、创建执行器
##### 4、创建触发器
##### 5、编写实现了JobHandler的类，并且用@TesseractJob注解标明所属的触发器名字，与web端触发器名字相同
##### 6、web控制台启动触发器即可
## 框架设计原理（十分轻量）：
### 一、DB表设计
![tesseract-job架构](https://github.com/tesseract-job/tesseract-job-admin/blob/master/architecture/TesseractJobTable.jpg)
##### 1、tesseract_job_detail
任务明细，包含字段及注释参考sql源码
##### 2、tesseract_trigger
触发器明细，包含字段及注释参考sql源码
##### 3、tesseract_fired_job
正在执行的触发器，包含字段及注释参考sql源码
##### 4、tesseract_executor
触发器所属的执行器，包含字段及注释参考sql源码
##### 5、tesseract_executor_detail
执行器下面的机器列表，包含字段及注释参考sql源码
##### 6、tesseract_lock
数据库锁，包含字段及注释参考sql源码
##### 7、tesseract_user
用户信息，包含字段及注释参考sql源码
##### 8、tesseract_log
日志信息，包含字段及注释参考sql源码
##### 9、tesseract_group
用户组（部门），包含字段及注释参考sql源码
##### 10、tesseract_btn_resource
按钮信息，包含字段及注释参考sql源码
##### 11、tesseract_menu_resource
菜单信息，包含字段及注释参考sql源码
##### 12、tesseract_role
角色信息，包含字段及注释参考sql源码
##### 13、tesseract_role_resources
角色、菜单多对多关联表，包含字段及注释参考sql源码
##### 14、tesseract_role_btn
角色、按钮多对多关联表，包含字段及注释参考sql源码
##### 15、tesseract_user_role
角色、用户多对多关联表，包含字段及注释参考sql源码
### 二、内部运行原理
#### 1、Tesseract Job Server端
##### 1)、初始化Netty Http Server
##### 2)、从数据库中读出所有用户组信息
##### 3)、初始化每个组的调度线程ScheduleThread、ExecutorThread、MissfiredThread 具体描述参考上文所述
##### 4)、扫描等待执行触发器并通知触发器所属执行器下的机器执行任务，选取机器策略根据web控制台设置定义
#### 2、Tesseract Job Client端
##### 1)、初始化PingPongThread开始注册
##### 2)、初始化Netty Http Server
##### 3)、接收服务端调度请求，反射构建执行体执行
##### 4)、执行完毕或者遇见异常构建请求通知调度端
## 运行步骤：
### 1、运行db下的两个sql脚本创建数据库和表，先 func.sql（功能） 再 auth.sql（权限）
### 2、访问地址:http://localhost:8080/ 访问控制台，调度端启动会自动注册
### 3、可根据需要指定profile和log.path的路径
### 4、系统管理员账号和密码:super_admin admin

