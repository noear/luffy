<h1 align="center" style="text-align:center;">
  Luffy
</h1>
<p align="center">
轻量级的嵌入式FaaS引擎（可按需组装），也可用作低代码引擎
</p>
<p align="center">
    <a target="_blank" href="https://search.maven.org/search?q=org.noear%20luffy">
        <img src="https://img.shields.io/maven-central/v/org.noear/luffy.svg?label=Maven%20Central" alt="Maven" />
    </a>
    <a target="_blank" href="https://www.apache.org/licenses/LICENSE-2.0.txt">
		<img src="https://img.shields.io/:license-Apache2-blue.svg" alt="Apache 2" />
	</a>
    <a target="_blank" href="https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html">
		<img src="https://img.shields.io/badge/JDK-8-green.svg" alt="jdk-8" />
	</a>
    <a target="_blank" href="https://www.oracle.com/java/technologies/javase/jdk11-archive-downloads.html">
		<img src="https://img.shields.io/badge/JDK-11-green.svg" alt="jdk-11" />
	</a>
    <a target="_blank" href="https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html">
		<img src="https://img.shields.io/badge/JDK-17-green.svg" alt="jdk-17" />
	</a>
    <br />
    <a target="_blank" href='https://gitee.com/noear/luffy/stargazers'>
		<img src='https://gitee.com/noear/luffy/badge/star.svg' alt='gitee star'/>
	</a>
    <a target="_blank" href='https://github.com/noear/luffy/stargazers'>
		<img src="https://img.shields.io/github/stars/noear/luffy.svg?logo=github" alt="github star"/>
	</a>
</p>
<br/>
<p align="center">
	<a href="https://jq.qq.com/?_wv=1027&k=kjB5JNiC">
	<img src="https://img.shields.io/badge/QQ交流群-22200020-orange"/></a>
</p>



# 是啥？
* 我是一个嵌入式 FaaS 引擎 + 扩展中心 + 发布系统。（与云服务的FaaS有区别）
* 理念：运行实例 + 安装几个扩展插件，等于一个性化系统。
* 兼容：jdk8, jdk9, jdk10, jdk11, jdk12, jdk13, jdk14
* 并发：单实例本机测试qps = 4w


# 想干嘛？
* 通过统一的接口嵌入，促成各种FaaS语言的统一编程模型
  
>orm接口，http client接口，lock接口，queue接口，mvc接口，job接口，消息总线接口，函数总线接口
  
* 集成嵌入式服务治理组件（可通过配置，切换为分布式组件）
  
>配置服务，日志服务，消息服务，缓存服务，对象存储，定时任务，集群管理
  
* 可互为扩展中心的机制，形成一个自由的插件网

* 快速构建热编辑、热更新、热执行的轻量级生产环境

* 同时提供类似Electron一样的跨平台桌面运行环境


# 有什么能力？
* 一切都是：热编辑；热更新；热执行
* 运行动态文件执行的能力（由执行器决定）
* 运行静态文件的能力（可304缓存）
* 扩展执行器的能力（及jar包扩展）
* 提供定时任务能力
* 提供消息总线能力
* 提供拦截器的机制
* 提供勾子的机制
* 提供版本管理的机制
* 提供集群支持的机制（集群支持很友好哦）
* 模板即可直接开发轻量接口
* 函数总线,让不同语言可直接相互调用
* 等等...


# 演示

#### 框架演示
* http://jtt.noear.org/.admin/?_L0n5=1CE24B1CF36B0C5B94AACE6263DBD947FFA53531

#### 应用演示（基于luffy开发的应用）


| 应用   | 演示地址                                                                   | 
|-------|------------------------------------------------------------------------| 
| NavX，轻量级内部导航工具 | [https://navx.noear.org/](https://navx.noear.org/)                     |
| TeamX，中小团队协用工具  | [https://teamx.noear.org/ucenter/](https://teamx.noear.org/ucenter/)   |
| Solon，更现代感的应用开发框架 | [https://solon.noear.org/](https://solon.noear.org/)                   |


# 部署

#### 极速体验

* 运行命令：`docker run -it --rm -p 18080:8080 noearorg/luffy-jtl:1.9.2`
* 然后打开：`http://localhost:18080/.admin/?_L0n5=1CE24B1CF36B0C5B94AACE6263DBD947FFA53531`

#### 常规体验

* 使用 docker-compose 部署，配置参考 docker-compose.yml


