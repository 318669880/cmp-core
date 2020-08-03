# FIT2CLOUD 2.0 管理中心

- [要求](#要求)
- [代码规范](#代码规范)
- [首页模式](#首页模式)
- [注意事项](#注意事项)
- [全局处理](#全局处理)
- [权限](#权限)


## 要求

- 掌握 [Angular JS 1.6.9](https://angular.io/) 的内容(此框架的不同版本差异较大，本文基于用 1.6.9)。
- 掌握 [Angular JS Material 1.1.9](https://material.angularjs.org/) 的内容。
- 开发时如需自动提示angular相关内容，可以npm install angular@1.6.9和npm install angular-material, 安装后务必在.gitignore文件中添加package-lock.json和node_modules(Demo工程已经添加)
- 本项目使用 [Spring Boot 2.0](https://spring.io) 作为基础框架并集成 `thymeleaf` 、 `shiro` 、 `quartz` 和 `mail` 等功能。

## 代码规范

- css, html, js等前端文件命名, 使用 `-` 做单词分隔，例如：fit2cloud-style.css, module-menu.html, angular-material.js
- angular: Controller，Service均使用大写首字母的驼峰命名，例如MenuController, MenuService
- angular: Directive，Component均使用小写首字母的驼峰命名，例如moduleMenu
- js: 变量使用小写首字母的驼峰命名，例如userName
- js: 常量使用全大写，下划线分隔单词的命名，例如var MAX_HEIGHT=1000


## 首页模式

每个工程的首页默认不显示公共菜单，需要引入web-public的jar包，启动后输入<hostname:port>/web-public可以看到公共菜单的首页。

## 注意事项

- Form表单，当表单内容多于8项，或者需要二次交互（弹出窗口或内容有表格，并需要操作表格等）时不使用弹出窗口的方式显示表单，直接在主页面显示。

## 全局处理

- 后台代码对@RestController结果集进行了统一封装成ResultHolder,如果自己返回ResultHolder,则不会封装。如果需要包装的数据,method返回类型不要是Object的(new Object()和 null 不会包装，1.返回的type 不是application/json 2.没有对应的Object.class的HttpMessageConverter)
- 后台代码做了全局异常处理
- 前台HttpUtils的post,get都做了错误处理（只有ResultHolder的success 为false时，当做错误处理）,如果需要重新定义错误可以用error的function接受,没有error function 或自己弹出后台的错误信息

## 权限
 
#### 前端页面

  在前端页面已经写的有angular的指令
  
  - has-permission：有这个权限的时候显示 单个权限  
  - has-permissions：有其中一个权限的时候显示，主要是控制多个权限和去掉table的单选框、操作的列
  - lack-permission：没有这个权限的时候显示
  - lack-permissions：没有这里的所有权限的时候显示
