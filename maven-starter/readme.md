##### maven根据环境打包配置文件+多配置文件

[git项目地址 ](https://github.com/GitHubsteven/maven-in-action/tree/master/maven-starter)

###### 目的一：maven根据环境打包配置文件
阐述：在开发过程中，不同环境下的配置是不同的，如DataSource，cache,mq等等，所以我们需要根据不同的环境读取不同的配置打包发布。
为了实现这个目的，我们需要依赖maven的一个插件maven-resources-plugin，另外我们需要定义不同的环境profile.

参考文件：
1. [Apache Maven Resources Plugin](https://maven.apache.org/plugins/maven-resources-plugin/) 
2. [Maven根据不同环境打包不同配置文件](https://blog.csdn.net/li295214001/article/details/52044800)

###### 步骤
1. 在父pom中导入plugin，代码如下： 
```xml
<build>
        <resources>
            <!-- 先对主资源目录进行过滤。env下的所有内容都不打包 -->
            <resource>
                <directory>src/main/resources/</directory>
                <excludes>
                    <exclude>env/</exclude>
                </excludes>
            </resource>
            <!-- 之后再根据配置选择具体的 env 目录，maven 会根据该配置复制到资源根目录 -->
            <resource>
                <directory>src/main/resources/env/${package.environment}</directory>
            </resource>
        </resources>

        <!--这是在父pom中 -->
        <pluginManagement>
            <plugins>
                <!-- 别的plugin写在下面*位置 -->
                <plugin>
                    <groupId>org.apache.maven.plugins</groupId>
                    <artifactId>maven-resources-plugin</artifactId>
                    <version>2.6</version>
                    <executions>
                        <execution>
                            <id>copy-resources</id>
                            <phase>compile</phase>
                            <goals>
                                <goal>copy-resources</goal>
                            </goals>
                            <configuration>
                                <!-- 覆盖原有文件 -->
                                <overwrite>true</overwrite>
                                <outputDirectory>${project.build.outputDirectory}</outputDirectory>
                                <!-- 也可以用下面这样的方式（指定相对url的方式指定outputDirectory） <outputDirectory>target/classes</outputDirectory> -->
                                <!-- 待处理的资源定义 -->
                                <resources>
                                    <resource>
                                        <!-- 指定resources插件处理哪个目录下的资源文件 -->
                                        <directory>src/main/resources/env/${package.environment}</directory>
                                        <filtering>true</filtering>
                                    </resource>
                                </resources>
                            </configuration>
                        </execution>
                    </executions>
                </plugin>
                <!--over-->
            </plugins>
        </pluginManagement>
    </build>
```
在实际的项目pom中，导入 
```xml
 <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-resources-plugin</artifactId>
            </plugin>
   </plugins> 
  </build>
```
**注意：也可以直接导入父pom中的build依赖，但是把pluginManagement标签去掉**

2. 建立profile

在pom.xml中，你可以在父pom（推荐），也可以在子pom.xml中定义
```xml
<profiles>
        <profile>
            <id>dev</id>
            <properties>
                <package.environment>dev</package.environment>
            </properties>
            <activation>
                <activeByDefault>true</activeByDefault>
            </activation>
        </profile>

        <profile>
            <id>test</id>
            <properties>
                <package.environment>test</package.environment>
            </properties>
        </profile>
        <profile>
            <id>prod</id>
            <properties>
                <package.environment>prod</package.environment>
            </properties>
        </profile>

    </profiles>
```

package.environment用来在plugin中定义的resource标签内容中的directory引用，要和对应的配置目录一致。当前resources目录为：
```
└─resources
    └─env
        ├─dev
        ├─prod
        └─test
```
3. 通过maven打包 

执行maven命令： 
```
mvn clean [package|install|deploy] -Dmaven.test.skip=true -P [dev|test|prod]
```
就可以按环境打包配置了。

###### 目的二：多配置文件

通过spring.profiles.include 我们可以读取多个指定配置文件。

###### 步骤
1. 在resources下配置共同的配置文件如banner.txt（这里跳过），启动文件application.yml(如果这个不同，请放在env/[dev|test|prod]下）
   application.yml配置文件如下： 
   ```yml
   spring:
      profiles:
        include: datasource,web       #包含的文件名（application-name.yml)取name
      application:
        name: Multi-Env
   ```
2. 在env下创建
   dev,test,prod文件夹，每个文件夹包含application-datasource,application-web配置文件，示例在test环境下，
   application-datasource.yml代码如下：
   ```yml
   spring:
      datasource:
        url: test-url
        username: test-username
        password: test-password
   ```
   application-web.yml代码如下： 
   ```yml 
   server:
      port: 9020
   ```
   dev和prod雷同。
 
最后的目录结构如下（resources）
```
│  application.yml
│
└─env
    ├─dev
    │      application-datasource.yml
    │      application-web.yml
    │
    ├─prod
    │      application-datasource.yml
    │      application-web.yml
    │
    └─test
            application-datasource.yml
            application-web.yml
```

##### 验证
执行如下命令 
```
mvn clean package -Dmaven.test.skip=true -P test
```

target下classes代码结构如下： 
```
│  application-datasource.yml
│  application-web.yml
│  application.yml
│
└─com

```
com表示自定义java类生成的class文件，查看datasource和web配置文件，和resources配置文件一样。

**注意：执行maven命令请在profile所在的pom.xml对应的项目下执行，本项目在父项目上执行**