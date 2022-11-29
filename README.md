# 一款将 SpringBoot 项目做成 Windows Service Wrapper 的 Maven 插件

> Windows Service Wrapper v2.11.0

> 包括但不限于 SpringBoot ,任何打成 java jar 包运行的 Maven 项目都可以使用

## 编写初衷
- 公司有个项目
- Java 部分的全部使用的是SpringBoot
- 该项目的部署环境是 Windows
- 公司想把 各个 SpringBoot 的模块托管一下
- 托管的使用方式要简单，易用，测试在打包部署的时候要很容易上手
- 期间尝试过 Spring Boot Admin 和 Jenkins,都说不好用...
- 于是就想着 将Spring Boot 的服务制作成 Windows 服务，这样基本上会操作电脑的人都会使用了，够简单易用的了吧
- 花了一上午时间将其中一个 Spring Boot 模块制作成了 Windows Service
- 发现再做其他的模块的时候，很多工作都是重复的，心想着能够将这个功能提取出来就好了
- 于是就写了这个 Maven 插件

## 怎么使用？
- 使用方法很简单，和普通的 Maven 插件一样使用就可以了，如下
``` xml
    <plugins>
        <plugin>
            <groupId>com.github.alotuser</groupId>
            <artifactId>winsw-maven-plugin</artifactId>
            <version>2.11.0</version>
            <executions>
                <execution>
                    <id>winsw</id>
                    <phase>package</phase>
                    <goals>
                        <goal>winsw</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
```

注意：
 1. 这里的 phase 写的是 package,意思是该插件在 mvn package 的时候调用,你也可以根据不同的需求来更改，比如 install, test等等
 2. goal 写 winsw 就可以了，不需要改动
 3. 一般情况下我们的SpringBoot项目会有其他父项目，这时打包会使用 winsw-maven-plugin 插件的 repackage,这样的情况的话，请将该插件放置最后面,否则服务运行的话将提示没有主属性
 
- 在你的项目中按照以上的方式引入插件后，现在可以 打包了
``` 
    mvn package
```

打包便制作成功,此时，在你项目的target目录下会生成一个 jar 包名字 一样的压缩包进入文件夹，解压这个压缩包
注意：
 1. 5个 bat 文件，双击运行无反应时,请右键以管理员的身份运行
 2. 各文件的文件名无特殊情况，不需要修改
 3. 一旦安装成了 Windows 服务，目录下的文件就不要移动了
 4. 命令运行时，可能会提示安装.NET,安装完成就可运行命令了，不过现在大部分的 Windows 服务器或者个人电脑都会默认安装了.NET,没有的话启用一下就好了
 5. 运行各个命令是注意提示信息，例如卸载完服务都的状态为NonExistent，刚安装完服务后的状态为Stopped，服务成功启动的状态为Started...等等


## 扩展参数
想要在服务启动时添加自定义参数,如 SpringBoot 的配置参数或者 JMV 参数？
像如下配置即可：
``` xml
   <plugin>
        <groupId>com.github.alotuser</groupId>
        <artifactId>winsw-maven-plugin</artifactId>
        <version>2.11.0</version>
        <executions>
            <execution>
                <phase>package</phase>
                <goals>
                    <goal>winsw</goal>
                </goals>
            </execution>
        </executions>
        <configuration>
            <isVersion>true</isVersion>
            <winVersion>x64</winVersion>
            <vmOptions>-Xms1024m -Xmx2048m</vmOptions>
            <programArguments>--spring.profiles.active=pro --server.port=7777</programArguments>
        </configuration>
    </plugin>
```

上面配置了JVM 参数 和 Spring Boot 应用的启动端口。

其中winVersion是运行环境版本，例如:x64 ， x86 ， net2 ， net4 和 net461 。

## 使用注意
- 服务的id为artifactId，服务的名称为artifactId+version，服务的描述为description

