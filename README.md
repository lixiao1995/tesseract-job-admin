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
## 系统架构图：
![tesseract-job架构](https://github.com/tesseract-job/tesseract-job-admin/blob/master/%E6%9E%B6%E6%9E%84/Tesseract%20Job%E6%9E%B6%E6%9E%84%E5%9B%BE.jpg)
## 运行步骤：
### 一、运行db下的两个sql脚本创建数据库和表
### 二、访问地址:http://localhost:8080/index 访问控制台，调度端启动会自动注册
### 三、可根据需要指定profile和log.path的路径
### 四、系统管理员账号和密码:super_admin admin

