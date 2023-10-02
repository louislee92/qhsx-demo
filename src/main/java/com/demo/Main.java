package com.demo;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 入口
 */
public class Main {

    public static String httpGet(String url) {
        HttpRequest request = HttpRequest.get(url);
        HttpResponse response = request.execute();
        String body = response.body();
        return body;
    }

    public static void main(String[] args) {
        // 1. HTTP请求目录页，使用正则表达式，获取所有详情页链接地址
        String INDEX_URL = "https://mp.weixin.qq.com/s/idfpQeh5P5VxXJBlnvVJWQ";
        String body = httpGet(INDEX_URL);
        // 正则匹配
        String reg = "\\<a href\\=\"(https\\://mp\\.weixin\\.qq\\.com/s\\?__biz=MzI5MzYzNDU5Mg==\\&.*?)\"";
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(body);
        List<String> urlList = new ArrayList<>();
        while(matcher.find()) {
            String url = matcher.group(1);
            url = url.replaceAll("&amp;", "&");
//            System.out.println(url);
            urlList.add(url);
        }
        // 2. HTTP请求详情页，使用正则表达式，获取所有视频地址
        // 正则匹配如下字符串
        reg = "(?m)var videoPageInfos = (\\[[\\s\\S]*?\\]);[\\s\\S]*?window.__videoPageInfos";
        pattern = Pattern.compile(reg);
        for(int i = 0; i < urlList.size(); i++) {
            String url = urlList.get(i);
            String detailBody = httpGet(url);
            matcher = pattern.matcher(detailBody);
            while (matcher.find()) {
                String jsObj = matcher.group(1);
//                System.out.println(jsObj);
                // 得到的字符串为js对象，需要转换为json字符串
                String jsonStr = jsObj
                        .replaceAll("(^ [0-9a-z_]+?):", "'$1':")
                        .replaceAll("(\\|\\|.*?),", ",")
                        .replaceAll("(\\*[\\s\\S]*?)([,\\}])", "$2");

                JSONArray jsonArray = JSONArray.parseArray(jsonStr);
                System.out.println(jsonArray);
                for(int j = 0; j < jsonArray.size(); j++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(j);
                    String source_nickname = jsonObject.getString("source_nickname");
                    String content_noencode = jsonObject.getString("content_noencode");
                    System.out.println(StrUtil.format("{}:{}【{}】",
                            source_nickname, content_noencode, j + 1));
                    JSONArray videoArr = jsonObject.getJSONArray("mp_video_trans_info");
                    for(int k = 0; k < videoArr.size(); k++) {
                        JSONObject videoObj = videoArr.getJSONObject(k);
                        String quality = videoObj.getString("video_quality_wording");
                        String videoUrl = videoObj.getString("url");
                        videoUrl = videoUrl.replaceAll("&amp;", "&");
                        System.out.println(StrUtil.format("【{}】{}", quality, videoUrl));
                    }
                }
            }
//            break;
        }
    }
}