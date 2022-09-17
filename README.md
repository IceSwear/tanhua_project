## 鸡朋平台-项目概述（Tanhua）

### 背景

本人自学Java的第二个项目，通过网络获取到某培训班的课程资料，自己根据资料写完的项目

### 项目视频展示地址

bilibili.com----https://www.bilibili.com/video/BV1ca411g7TF

### 技术栈

Spring Cloud Alibaba生态（Gateway,Nacos,Dubbo)

Redis,RabitMq,MongoDB,Mysql,Nginx(后期搭前端Vue时用到)

![img](https://cdn.nlark.com/yuque/0/2022/png/29409711/1662141283767-46c57c48-5255-467a-a7ce-be08832871f4.png)

# 接口统计

 App前端：66个，Admin后台端：16个。

### 第三方组件/SDK

#### 1.用第三方API/SDK:(避免重复造轮子)

- 阿里云手机验证码（要收费，测试方便自己封装了邮箱验证码功能）
- 百度人脸识别（首次注册登录上传头像时候使用，有免费次数）
- 环信SDK（IM通讯，聊天，感觉没多大用，100个用户以内免费)
- 阿里云内容审核（个人替换成了百度的内容审核，因为有免费试用次数）[**百度内容审核传送门**](https://ai.baidu.com/ai-doc/ANTIPORN/dkk6wyt3z)

#### 2.存储方案

- 阿里云OSS(缺点：收费，大文件存储不太推荐，流量消耗巨大)
- fastFDS（单个500Mb以内文件推荐，需要配置tracker和storage，会比较麻烦，测试可用docker，下附部署方法）
- MinIO(拓展可选，有UI界面，听说可拓展性很高，docker部署简单，下附部署方法)

### 环境依赖

- JDK1.8
- Maven-3.8.5
- Git
- IntelliJ IDEA



### 学习内容总结-What I learn from this project?

#### 知识点

- **定时任务Spring Task**

同步异步@EnableAsync（加在引导类上）   +@Async(加在方法上)

定时应用：

1. 定时自动生成随机推荐用户，定时获取百度AccessToken,
2. 推荐用户，（定时推荐,存在redis缓存里应该更好，其实是伪代码,，理想状态其实应该仅为登录的用户推荐登录的用户）
3. 定时清空注销用户（如2年未登录的用户等）

cron时间参考：https://tool.lu/crontab/



- **JWT加密**

JWT用户鉴权（A.B.C）

JWT根据非对称加密生成，大家都可以解密，但是只有有秘钥的一方才可以生成token，不可伪造，在token中保存的信息都是可信任的。

最安全还是用redis或者其他缓存服务存储登陆信息，在安全要求没有那么高或者懒得搞缓存的时候才会考虑用jwt，比如业余时间搞同人社团网站/小程序之类。



- **DTO/VO/MODEL/MONGO-ENTITY 封装对象思想**

该项目大量使用，实体类-对应表，VO,DTO,enums枚举类的大量使用，封装初始化想要的VO对象;





- **YAPI 文档阅读，规范了接口书写**

YAPI的代码规范（还是有些许不足，实际开发应该要多方交流）



![img](https://cdn.nlark.com/yuque/0/2022/png/29409711/1662209959996-e603771f-710c-4a23-8f5d-bed39a8f5834.png)

- **自动装配的流程**

resources - META-INF - spring.factories 



- **公共字段自动填充（两种方法，复习，外卖项目的时候也有）**

1. 用Mybatis-Plus拦截器，进行字段处理;
2. 直接使用MySql表内的 **CURRENT_TIMESTAMP**   **和**  **on update CURRENT_TIMESTAMP**



- **自定义业务异常处理**

1. 自定义异常结果类，一般要包含String类型的error_code 和error_message属性;
2. 自定义异常类，继承RuntimeException；
3. 统一异常处理ExceptionAdvice捕获异常，@ControllerAdvice+@ExceptionHandler



- **MongoDB用法大量CRUD**



- **自定义枚举Emus和自定义常量Constants**



- **Redis存储结果**

普通key-value : 验证码

set(key, set)  用户喜欢

![img](https://cdn.nlark.com/yuque/0/2022/png/29409711/1662820938517-595b3b44-2174-4d43-94a3-cebfec11163c.png)

HashKey（key，hashkey,value）：视频/动态，喜欢点赞相关 ；访客记录（visitor,用户,Long型来访时间）

![img](https://cdn.nlark.com/yuque/0/2022/png/29409711/1662820979595-65e54d12-912e-48fd-98ce-ecf83774404b.png)

#### 频繁用的方法汇总

Hutool工具类

```java
//generate random number ,length =6 长度为6 V
String code = RandomUtil.randomNumbers(6);   

//生成50.00-00.00之间保留2位数的随机数,缘分值
Double random=RandomUtil.randomDouble(50.00, 99.99, 2, RoundingMode.HALF_UP);

//将一个list以里面一个元素映射--Long- 对象
//将list以一个字段field做map映射
Map<Long, UserInfo> map = CollUtil.fieldValueMap(lists, "id"); 

//生成条形干扰验证码
LineCaptcha captcha = CaptchaUtil.createLineCaptcha(299, 97);
String code = captcha.getCode();
//读出
captcha.write(response.getOutputStream());

//list里某个字段提出
List<Type> list=CollUtil.getFieldValues(list,"field",Type.class)

//copy同名，同类型的属性
BeanUtils.copyProperties(源, 目标);  

//以,将值分开形成值
String[] values = redisValue.split(",");
//or 自己做
String[] strs = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11" };
// 数组的流，分页，然后用map映射每一个元素，转换成Long类型，并将结果集list
//分页
List<Long> pids = Arrays.stream(split).skip((page - 1) * pagesize).limit(pagesize).map(s -> Long.valueOf(s)).collect(Collectors.toList());



List<String> list = Arrays.asList(strs);
//ListUtil.sort(list, (a, b) -> a.compareTo(b));
ListUtil.sort(list, String::compareTo);
//返回第0页，每页大小为5条数据
int pageNo = 0;
int pageSize = 5;
//Hutool分页
List<String> results = ListUtil.page(pageNo, pageSize, list);
```

md5加密

```java
//md5加密
user.setPassword(DigestUtils.md5Hex(Constants.INIT_PASSWORD.getBytes()));
user.setPassword(org.springframework.util.DigestUtils.md5DigestAsHex(DEFAULT_PASSWORD.getBytes()));
```

几种时间处理

```java
//水印，用的是hutool的 或jod Time
String dateMark = DateTime.now().toString("yyyyMMdd/");
//jdk自带时间格式化处理
String dateMark = new SimpleDateFormat("yyyyMMdd/").format(new Date());


"HH:mm:ss:SSS"
```

几种分页方式比较

```java
//MyBatisPlus 分页，构造条件
IPage<Entity> entityPage = new Page<>(page, pagesize);
QueryWrapper qw = new QueryWrapper<>();
qw.orderByDesc("created");
entityPage = entityMapper.selectPage(userPage, null/qw);
long total = entityPage.getTotal();
List<Entity> list = entityPage.getRecords();


//PageHelper 分页助手
PageHelper.startPage(page, pagesize);
//手写sql，需要startPage后马上做，因为PageHelper的原理是拦截sql，mapper自己写分页条件即可
List<Entity> list = announcementMapper.getAllNotDeleted();
PageInfo<Entity> pageInfo = new PageInfo<>(list);
 long total = pageInfo.getTotal();
//分页集合
 List<Entity> = pageinfo.getList();



//Hutool Page 分页 和下面差不多，是对已有的集合进行排序
String[] values = redisValue.split(",");
List<String> list = Arrays.asList(values);
//ListUtil.sort(list, (a, b) -> a.compareTo(b));  排序
ListUtil.sort(list, String::compareTo);
int pageNo = 0;
int pageSize = 5;
//Hutool分页
List<String> results = ListUtil.page(pageNo, pageSize, list);



//Array直接分页
String[] values = redisValue.split(",");
List<Long> pids = Arrays.stream(split).skip((page - 1) * pagesize).limit(pagesize).map(s -> Long.valueOf(s)).collect(Collectors.toList());
```



#### @Cache 不能用热启动

当对象序列化到缓存中时，应用程序类装入器是C1。然后在更改一些代码/配置之后，devtools会自动重启上下文并创建一个新的类加载器(C2)。当您访问该缓存方法时，缓存抽象将在缓存中找到一个条目，并从存储中对其进行反序列化。如果缓存库没有考虑到上下文类加载器，该对象将有错误的类加载器附加到它(这解释了为什么奇怪的异常A不能转换为A)。

El表达式 @Cacheable(value = "announcement", key = "T(com.tanhua.server.interceptor.UserHolder).userId+'_'+#page+'_'+#pagesize")









Improvement 改进

1. 将MyBatisPlus改成Mybats手写Mapper，强化自己的sql能力；
2. 增加了Swagger集成SpringCloud；[《SpringCloud下JWT+GATEWAY鉴权下，SWAGGER2的部署》](https://www.yuque.com/docs/share/fc9a6e42-7d6c-4be5-a673-8a82ebce1a04?#) 
3. 定时任务自动推荐；
4. 增加邮箱验证EmailTemplate自动装配；
5. 优化了一些DB字段，添加逻辑删除等;
6. 将一些晦涩的魔法值改为常亮。

xx



TODO- List

1. 优化字段名表示；
2. 优化重复方法，整合方法；
3. 其他方法思路？





## 探花鸡友-环境建立

## （Nacos,YPI,RabitMQ,Nginx等搭建）搭建（仅针对mac用户，win自己看视频即可，部分是在Linux系统内，可以参考下）

### 步骤一：Mac的VMware配置NAT模式

**用VMware打开tanhua-linux资料包（Parallel destop用不了****，别问我怎么知道，哭了****）**

![img](https://cdn.nlark.com/yuque/0/2022/png/29409711/1661926465731-b2ce47a4-9920-4778-b426-12604e9232df.png)

在网络创建一个NAT，**subnet IP改为：192.168.136.0**

![img](https://cdn.nlark.com/yuque/0/2022/png/29409711/1661926481025-d1089b7f-bb2c-48b3-9a9e-52361f639913.png)

**然后设置Liunx-tanhua的网络适配器配置vmnet2**

![img](https://cdn.nlark.com/yuque/0/2022/png/29409711/1661926507683-99bdf68d-ced9-4e3c-94dc-1df6d9adc0a2.png)



### 步骤二：用Finalshell连接Linux（当然也可以用虚拟机自带的shell，看个人喜好）

Linux系统账号密码：

ip：192.168.136.160

端口：22

用户名：root

密码：root123



```bash
#进入页面
cd /root/docker-file/base

#运行
docker-compose up -d


cd /root/docker-file/rmq

#运行
docker-compose up -d
```

启动如图：

![img](https://cdn.nlark.com/yuque/0/2022/png/29409711/1662128742405-688ec58b-ca6a-4d98-ab61-389faf960262.png)



### 步骤三（可选）：配置Nginx（建立后台管理系统VUE配置，针对MacOS，win直接用资料的即可）

使用brew安装Nginx

```bash
#安装，等读条
brew install nginx 


#启动
brew services restart nginx

#查看nginx的版本
nginx -v

#查看nginx的信息
brew info nginx

#配置nginx.conf 
#打开nginx.conf .然后把下面配掉
open /usr/local/etc/nginx/



#其实这个才是nginx被安装到的目录，看看即可
open /usr/local/Cellar/nginx      


#把vue的项目放在这里，注意要将www内的文件清空！
open /usr/local/var/www
```



nginx.conf替换内容

```bash
   listen       8088;
        server_name  localhost;
        #charset koi8-r;
        #access_log  logs/host.access.log  main;
		location / {
            root   html;
            index  index.html index.htm;
      
        }
		
		location  /management {
			proxy_pass http://127.0.0.1:8888/admin;
		}
```



如图

![img](https://cdn.nlark.com/yuque/0/2022/png/29409711/1661864467042-74534945-26a4-4ceb-a9f9-effd69a65f21.png)

**tanhua的Vue静态资源，放好后重启nginx即可在**[**http://localhost:8088/**](http://localhost:8088/)**中打开**

![img](https://cdn.nlark.com/yuque/0/2022/png/29409711/1662131259440-9a147aca-586e-4aba-9034-4a6e684b721a.png)



### 步骤四(可选)：使用Docker快速搭建fastFDS或MinIO

#### fastDS（可选）

```bash
#拉镜像
docker pull delron/fastdfs

#创建tracker
docker run -itd \
--network=host \
--restart=always \
--name tracker \
-v /var/fdfs/tracker:/var/fdfs \
-v /etc/localtime:/etc/localtime \
delron/fastdfs tracker 



#创建存储
docker run -itd \
--network=host \
--restart=always \
--name storage \
-e TRACKER_SERVER=ip:22122 \       #这里 ip是当前主机的ip
-v /var/fdfs/storage:/var/fdfs \
-v /etc/localtime:/etc/localtime \
delron/fastdfs storage 

#可以到storage看下nginx.cnf的配置，默认是8088
docker exec -it storage bash

cd /usr/local/nginx/conf/

#默认是8888，需要再自己改
cat nginx.conf


#开启docker自动启动
docker update --restart=always tracker
docker update --restart=always storage
```

成功后就是yml的配置了

![img](https://cdn.nlark.com/yuque/0/2022/png/29409711/1662130570871-af712285-3604-4a27-b957-8ab71a950bf1.png)



#### MinIO(可选)

```bash
docker run -di -p 9000:9000  -p 9001:9001 \
--name=minio \
--restart=always \
-e "MINIO_ROOT_USER=admin" \
-e "MINIO_ROOT_PASSWORD=admin123" \
-v /usr/kk/data/minio/data:/data \
-v /usr/kk/data/minio/config:/root/.minio \
-v /etc/localtime:/etc/localtime \
minio/minio server /data \
--console-address ":9001" \
--address ":9000";

docker logs mino
```

**注意，因为多了一个 Console ，所有要在防火墙里开放9001端口，同时在服务器的安全组中添加放开9001端口，因为后续访问9000端口的时候会跳转至9001端口，从9001端口进入到Console**

### 步骤四：Nacos安装（可选）

#### 方式一：官方压缩包安装（对mac系统，其他应该也一样，毕竟是java）

地址：

[Nacos官方github下载地址](https://github.com/alibaba/nacos/releases)

解压后，在conf文件中，application.properties配置Mysql数据库还有数据库表（本人使用的是1.4.1）

![img](https://cdn.nlark.com/yuque/0/2022/png/29409711/1662103550907-71102ef8-965e-470b-b8ff-770bc45224c7.png)

启动/关闭指令：（开机要重新启动一次）

```git
#bin包下,单机模式
sh startup.sh -m standalone

#关闭
sh shutdown.sh
```

#### 方式二：Docker安装+Mysql（可选，针对Linux系统）

也可以用docker，不过连数据库比较麻烦，docker我用有点占内存。

**Attention:做mysql持久化的时候，一定要对应docker下载的版本来运行sql文件（去github对应版本来找），新版本sql文件可能多了一些字段等原因，老版的用了对不上，导致有些功能配置失败！这是经验之谈，找bug的时候的血泪史。**

docker-compose 挂载

```yaml
version: '3.0'
services:
  mysql:
    image: mysql:8.0.23
    restart: always
    container_name: mysql      # 容器名
    environment:
      MYSQL_ROOT_PASSWORD: 012345678
    volumes:
      - "/home/mysql/data:/var/lib/mysql"
      - "/home/mysql/conf/hmy.cnf:/etc/mysql/conf.d/hmy.cnf"
    ports:
      - "3333:3306"
  nacos:
    image: nacos/nacos-server:2.0.3      # 镜像`nacos/nacos-server:latest`
    container_name: nacos_server                                 # 容器名为'nacos_server'
    restart: always                                              # 指定容器退出后的重启策略为始终重启
   # volumes:                                                     # 数据卷挂载路径设置,将本机目录映射到容器目录
    #  - "./nacos_mysql/logs:/home/nacos/logs"
    #  - "./nacos_mysql/init.d/custom.properties:/home/nacos/init.d/custom.properties"
    environment:                        # 设置环境变量,相当于docker run命令中的-e
      - PREFER_HOST_MODE=ip  #or hostname
      - MODE=standalone                           # 单机模式启动
      - SPRING_DATASOURCE_PLATFORM=mysql
      - MYSQL_SERVICE_HOST=172.17.0.1    # 注：在宿主机输入：ifconfig 找到 docker0的网卡地址，将127.0.0.1换成对应地址即可，通常是172.17.0.1或者172.18.0.1
      - MYSQL_SERVICE_DB_NAME=nacos        # 所需sql脚本位于 `nacos-mysql/nacos-mysql.sql`
      - MYSQL_SERVICE_PORT=3333   #对应mysql外部端口
      - MYSQL_SERVICE_USER=root
      - MYSQL_SERVICE_PASSWORD=012345678
   #   - JVM_XMS=64m   #-Xms default :1g
   #   - JVM_XMX=64m   #-Xmx default :1g
   #   - JVM_XMN=16m   #-Xmn default :512g
    #  - JVM_MS=8m     #-XX:MetaspaceSize default :128m
     # - JVM_MMS=8m    #-XX:MaxMetaspaceSize default :320
    ports:                              # 映射端口
      - "8848:8848"
```

hmy.cnf 的配置，提前创建好文件夹复制创建

```bash
[mysqld]
skip-name-resolve
character_set_server=utf8
datadir=/var/lib/mysql
server-id=1000
```

docker-compose启动命令:

```bash
docker-compose up -d
```

以上是两种安装方式，有时候连不上有问题就重启下nacos，具体看日志分析，推荐方式一。Docker的nacos挺站内存。



### 步骤五（可选）：MongoDB快速部署（Docker一键部署）

```shell
#创建MongoDB
docker run -d \
--name mongo-service \
--restart=always   \
-v /etc/localtime:/etc/localtime:ro \
-v /home/mongo/db:/data/db  \
--privileged=true \
-p 27017:27017  \
mongo \
--auth 

#进入容器
docker exec -it mongo-service mongo

#创建tanhua数据库管理员
db.createUser({
     user:'username', 
     pwd:'password', 
     roles:[{role:'readWrite',db:'tanhua'}]
})
```

java  yml配置

格式：   #mongodb://username:password@ip:port/db

```yaml
spring:
  data:
    mongodb:
      uri: mongodb://username:password@127.0.0.1:27017/tanhua
     #mongodb://username:password@ip:port/db
```



### 步骤六（可选）：RabbitMQ快速部署（Docker一键部署）

```bash
#docker 一键部署
docker run -d \
--hostname myrabbitmq \
-p 15672:15672 -p 5672:5672 \
--name rabbitmq \
--restart=always   \
-v /Users/kk/Documents/MyCode/rmq:/var/lib/rabbitmq \
-e RABBITMQ_DEFAULT_USER=admin \
-e RABBITMQ_DEFAULT_PASS=admin \
rabbitmq:3.8.9-management
```

yml配置如下

```yaml
spring:
  rabbitmq:
    host: 127.0.0.1
    port: 5672
    username: admin
    password: admin
```



**以上即是环境安装的全部。**