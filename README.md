# 分布式任务调度平台 tesseract-job
## 开发目的：
### 一、简化任务调用
### 二、不依赖于第三方调度框架，如Quartz等
### 三、提供完整的权限系统
### 四、可根据业务部门进行调度线程隔离，避免了由于某部门任务过多造成别的部门的影响
### 五、提供动态修改调度线程数，可根据任务压力调整线程池大小
### 六、调度端和执行端采用异步操作
### 七、提供任务失败，机器下线邮件报警
### 八、提供任务分片机制
### 九、前后端分离开发，前端采用VUE、Element-UI
## 系统架构图：
![tesseract-job架构](https://github.com/tesseract-job/tesseract-job-admin/blob/master/%E6%9E%B6%E6%9E%84/Tesseract%20Job%E6%9E%B6%E6%9E%84%E5%9B%BE.jpg)
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
   一个使用例子，目前采用 spring 耦合
## 高可用说明：
   调度端采用DB的行锁，所以请务必按照sql脚本的引擎创建lock表，对于并发访问的结构已经采用db锁，所以可以部署多台调度端，采用NGINX来做负载均衡。至于client端也可部署多台，调度端支持任务失败重试。
## 运行步骤：
### 一、运行db下的两个sql脚本创建数据库和表
### 二、访问地址:http://localhost:8080/ 访问控制台，调度端启动会自动注册
### 三、可根据需要指定profile和log.path的路径
### 四、系统管理员账号和密码:super_admin admin

