ps:gradle插件开发,达到直接在项目中引用自己的插件
```
eg:
 //app/build.gradle中添加
 apply plugin: 'com.lhp.hello.plugin'
 
 //TestGradePlugin/build.gradle中添加
 buildscript {
    repositories {
        google()
        jcenter()

        maven {//自定义插件本地Maven仓库地址
            //本地地址
           //url uri('/repo')
            //maven 远程地址
            url uri('https://dl.bintray.com/orgid1/maven')

        }
    }
    dependencies { 
        //自定义插件
        classpath "com.lhp.gradle:plugin:1.0.0"
    }
}
```



### 1.gradle 插件
在独立的项目里编写插件，然后发布到中央仓库，之后直接引用就可以了，优点就是可复用,Gradle插件是使用Groovy进行开发的，而Groovy其实是可以兼容Java的
### 2.开发步骤
- 首先，新建一个Android项目。
- 之后，新建一个Android Module项目，类型选择Android Library。
- 将新建的Module(plugin)中除了build.gradle文件外的其余文件全都删除，然后删除build.gradle文件中的所有内容。
```
在plugin/build.gradle中添加
apply plugin: 'groovy'
apply plugin: 'maven'

dependencies {
    //gradle sdk
    compile gradleApi()
    //groovy sdk
    compile localGroovy()
}

repositories {
    mavenCentral()
}
```
- 在新建的plugin中新建文件夹src，接着在src文件目录下新建main文件夹，在main目录下新建groovy目录，这时候groovy文件夹会被Android识别为groovy源码目录。除了在main目录下新建groovy目录外，你还要在main目录下新建resources目录，同理resources目录会被自动识别为资源文件夹。在groovy目录下新建项目包名（com.lhp.gradle），就像Java包名那样。resources目录下新建文件夹META-INF，META-INF文件夹下新建gradle-plugins文件夹。META-INF和gradle-plugins必须是父子目录。
 
- 在新建的com.lhp.gradle下创建文件PluginImpl
```
package com.lhp.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
public class PluginImpl implements Plugin<Project> {
    void apply(Project project) {
        project.task('testTask') {
            println "+++++++++++++++++++++++++"
            println "Hello gradle plugin"
            println "+++++++++++++++++++++++++"
        }
    }
}
```
- 然后在resources/META-INF/gradle-plugins目录下新建一个properties文件，注意该文件的命名就是你只有使用插件的名字，这里命名为com.lhp.hello.plugin.properties，在里面输入
```
implementation-class=com.lhp.gradle.PluginImpl
```
- 这样就完成了最简单的一个gradle插件，里面有一个叫testTask的Task，执行该task后会输出
```
成功之后再build 的时候会在 run栏中打印，执行的是testTask任务
+++++++++++++++++++++++++
Hello gradle plugin
+++++++++++++++++++++++++
```

### 3.发布到本地
- 在plugin/build.gradle中添加
```
发布到本地：group和version在后面使用自定义插件的时候会用到
group='com.lhp.gradle'
version='1.0.0'
uploadArchives {
    repositories {
        mavenDeployer {
            //本地的Maven地址设置为../repo
            repository(url: uri('../repo'))
        }
    }
}
```
- Sync Now 之后，找到Gradle projects窗口中的piugin/Tasks/upload/uploadArchives，双击执行，成功之后会在会提示build successful
- 可以使用了，参考开头的步骤
### 4.发布到maven
```
1.注册  https://bintray.com/signup/oss  亲测使用foxmial邮箱可以注册
2.在该网站依次创建（组织，在组织下创建maven 仓库）
3.在TestGradePlugin/build.gradle中添加
buildscript {
    dependencies {
        //jcenter上传到jcenter的工具类
        classpath 'com.novoda:bintray-release:0.9.1'
    }
}
4.在plugin/build.gradle中添加 
//https://github.com/novoda/bintray-release,使用bintray-release插件完成
apply plugin: 'com.novoda.bintray-release'
publish {
    userOrg = 'orgid1' //bintray账户下某个组织id
    groupId = 'com.lhp.gradle' //maven仓库下库的包名，一般为模块包名
    artifactId = 'plugin' //项目名称
    publishVersion = '1.0.0' //版本号
    desc = 'gradle plugin library' //项目介绍，可以不写
    website = 'https://github.com/liuhaipeng/TestGradePlugin' //亲测不写无法成功
}
5.在终端执行 先clean 工程
//执行上传任务到bintray上，成功之后会在网站看到上传上去的项目，就可以使用了，替换自己的PbintrayUser，PbintrayKey（获取方式自行查看）
//gradlew clean build bintrayUpload -PbintrayUser=lhp -PbintrayKey=219e22864958870e718116f7d78cd0216c3be23a -PdryRun=false
成功后会提示build successful ，这时候就能在远程bintray仓库中 【 组织/maven/ 】下看到plugin了 
6. 可以使用了，参考开头的步骤
```
### 5.发布到jcenter
```
1.在上述bintray仓库中找到maven/plugin,找到 add to jcenter ，提交之后等待审核，成功之后会收到邮件

2.只需要添加jcenter 就好了，具体使用参考开头的步骤
```
