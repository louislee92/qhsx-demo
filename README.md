## 使用JAVA模拟HTTP请求并通过正则解析所有针灸视频地址

1. 通过HTTP请求针灸目录（零基础中医公益学习班：倪海厦老师人纪系列01——针灸(经络)课程全集）https://mp.weixin.qq.com/s/idfpQeh5P5VxXJBlnvVJWQ，对返回结果进行解析，获取所有详情页跳转地址。

用到的正则：        
String reg = "\\<a href\\=\"(https\\://mp\\.weixin\\.qq\\.com/s\\?__biz=MzI5MzYzNDU5Mg==\\&.*?)\"";

2. 通过HTTP请求详情页，获取视频页信息的Javascript对象，将对象转换为json格式，使用fastJson解析获取所有清晰度视频地址。

用到的正则：
String reg = "(?m)var videoPageInfos = (\\[[\\s\\S]*?\\]);[\\s\\S]*?window.__videoPageInfos";

3. 目录地址应该是固定的，所以可以通过目录地址经过两次HTTP请求与解析得到指定视频的地址。因每次获取都是最新授权地址，所以可以解决视频链接地址超时失效的问题。但此方法也存在如下几个问题
（1）需要至少一次HTTP请求获取到视频地址，所以会有一定网络延迟问题。
（2）详情页解析正则是固定的，如果该网页后期变更结构或者变更链接地址的话，需要重新编写正则表达式

项目地址：https://github.com/louislee92/qhsx-demo


2023-10-18：腾讯前端已修改视频获取逻辑，访问地址返回403未授权。