# 延伸的 野火IM解决方案

有興趣可以到 野火團隊 去 下載 代碼  https://github.com/wildfirechat

這邊是 用於學習方面 野火沒有給的 admin 管理介面和 ui 對於 程序員 教學 或是 學習 使用

此版本已經驗證 


| [GitHub仓库地址](https://github.com/LiveChatAndApp)       | 说明                                                                                      
| ------------------------------------------------------------  | --------------------------------------------------------------------------
| [im-server](https://github.com/LiveChatAndApp/im-server)       |        | 野火社区版IM服务，野火IM的核心服务，处理所有IM相关业务。  |
| [app_server](https://github.com/LiveChatAndApp/app_server)       | Demo应用服务，模拟客户的应用服登陆处理逻辑及部分二次开发示例。 |
| [admin-ui](https://github.com/LiveChatAndApp/admin-ui)       | Demo应用服务，基於vue admin element 的 admin 管理介面。 |
| [admin-api](https://github.com/LiveChatAndApp/im-admin)       | Demo应用服务，admin 後台 api 開發。 |
| [android-chat](https://github.com/LiveChatAndApp/android-chat) | 野火IM Android SDK源码和App源码。 |
| [ios-chat](https://github.com/LiveChatAndApp/ios-chat)             | 野火IM iOS SDK源码和App源码。|


#### 编译
```
mvn package
```

#### 短信资源
应用使用的是腾讯云短信功能，需要申请到```appid/appkey/templateId```这三个参数，并配置到```tencent_sms.properties```中去。用户也可以自行更换为自己喜欢的短信提供商。在没有短信供应商的情况下，为了测试可以使用```superCode```，设置好后，客户端可以直接使用```superCode```进行登陆。上线时一定要注意删掉```superCode```。

#### 修改配置
本演示服务有4个配置文件在工程的```config```目录下，分别是```application.properties```, ```im.properties```, ```aliyun_sms.properties```和```tencent_sms.properties```。请正确配置放到jar包所在的目录下的```config```目录下。
> ```application.properties```配置中的```sms.verdor```决定是使用那个短信服务商，1为腾讯短信，2为阿里云短信

#### 运行
在```target```目录找到```app-XXXX.jar```，把jar包和放置配置文件的```config```目录放到一起，然后执行下面命令：
```
java -jar app-XXXXX.jar
```

#### 注意事项
服务中对同一个IP的请求会有限频，默认是一个ip一小时可以请求200次，可以根据您的实际情况调整（搜索rateLimiter字符串就能找到）。如果使用了nginx做反向代理需要注意把用户真实ip传递过去（使用X-Real-IP或X-Forwarded-For)，避免获取不到真实ip从而影响正常使用。

#### 使用到的开源代码
1. [TypeBuilder](https://github.com/ikidou/TypeBuilder) 一个用于生成泛型的简易Builder

#### LICENSE
UNDER MIT LICENSE. 详情见LICENSE文件

