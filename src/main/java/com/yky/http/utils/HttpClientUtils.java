package com.yky.http.utils;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author wuhualu
 */
public class HttpClientUtils {

    private static Logger logger = LoggerFactory.getLogger(HttpClientUtils.class);


    /**
     * 发送GET请求
     * @param requestBase
     */
    private static String execute(HttpRequestBase requestBase) {
        try (
                //创建一个HttpClient对象
                CloseableHttpClient httpClient = HttpClientBuilder.create().build();

                //创建响应模型
                CloseableHttpResponse response = httpClient.execute(requestBase);
                ) {

            //获得响应实体
            HttpEntity entity = response.getEntity();

            //获得实体内容,设置编码，防止响应乱码
            String entityString = EntityUtils.toString(entity, StandardCharsets.UTF_8);

            //获得响应状态码
            int statusCode = response.getStatusLine().getStatusCode();

            logger.debug("URL:" + requestBase.getURI());
            logger.debug("Method:" + requestBase.getMethod());
            logger.debug("code:" + statusCode);
            logger.debug("响应内容为:" + entityString);

            return entityString;

        } catch (IOException e) {
            e.printStackTrace();
            logger.error("遇到异常，消息发送失败");
        }

        return null;
    }

    /**
     * 解析请求参数
     * @param map 请求参数的map集合
     * @return
     */
    private static String getParams(Map<String,Object> map) {

        if(map == null || map.size() == 0) {
            return "";
        }

        StringBuffer params = new StringBuffer();

        try {
            //使用EntrySet遍历map
            for (Map.Entry<String,Object> entry : map.entrySet()) {
                //拿到value,判断是不是字符串
                if(entry.getValue() instanceof String) {
                    //字符串在这里做一下编码,防止汉字乱码
                    params.append(entry.getKey() + "=" + URLEncoder.encode((String) entry.getValue(),"utf-8"));
                } else {
                    params.append(entry.getKey() + "=" + entry.getValue());
                }
                //添加连接符
                params.append("&");
            }
            //删除最后一个&符号
            params.deleteCharAt(params.length() - 1);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return "?" + params.toString();
    }


    /**
     * 封装请求头
     * @param headers
     * @param requestBase
     */
    private static void setHeaders(Map<String,String> headers, HttpRequestBase requestBase) {
        if(headers != null && headers.size() > 0) {
            for (Map.Entry<String,String> entry : headers.entrySet()) {

                //key value都不为空，则设置请求头
                if(!(StringUtils.isEmpty(entry.getKey()) || StringUtils.isEmpty(entry.getValue()))) {
                    requestBase.setHeader(entry.getKey(),entry.getValue());
                }

            }
        }
    }


    private static void setConfig(Integer timeOut, HttpRequestBase requestBase) {
        //配置超时时间
        RequestConfig requestConfig = RequestConfig.custom()
                //设置连接超时,单位ms
                .setConnectTimeout(5000)
                //设置请求超时时间,单位ms
                .setConnectionRequestTimeout(5000)
                //socket读写超时时间,单位ms
                .setSocketTimeout(timeOut)
                //设置是否允许重定向(默认为true)
                .setRedirectsEnabled(true).build();

        requestBase.setConfig(requestConfig);
    }

    /**
     * 发送带参的GET请求
     * @param url 请求地址
     * @param timeOut socket超时时间，ms
     * @param headers 请求头部，可设置Content-Type以外的字段用于特殊应用，Content-Type已经设置了，无须重复设置。如果不需要请传入null
     * @param entityMap 请求参数，key-value形式，最后会拼接到url后面.最后的效果如下：http://localhost?name=peter&age=11
     * @return
     */
    public static String requestGet(String url, Integer timeOut, Map<String,String> headers, Map<String,Object> entityMap) {

        //拼接请求参数到URL后面
        url += getParams(entityMap);

        //创建GET请求
        HttpGet httpGet = new HttpGet(url);

        //设置头部
        setHeaders(headers, httpGet);

        //配置超时时间
        setConfig(timeOut, httpGet);

        return execute(httpGet);
    }

    /**
     * 发送不带参的GET请求
     * @param url 请求地址
     * @param timeOut 超时时间
     * @return
     */
    public static String requestGet(String url, Integer timeOut) {
        return requestGet(url, timeOut, null, null);
    }

    /**
     * 发送带参的post请求，普通参数，可以直接拼接到URL后面
     * @param url 请求地址
     * @param timeOut socket超时时间
     * @param headers 请求头部，可设置Content-Type以外的字段用于特殊应用，Content-Type已经设置了，无须重复设置。如果不需要请传入null
     * @param entityMap 请求参数，key-value形式，最后会拼接到url后面。最后的效果如下：http://localhost?name=peter&age=11
     * @return
     */
    public static String requestPost(String url, Integer timeOut, Map<String,String> headers, Map<String,Object> entityMap) {
        //拼接请求参数到URL后面
        url += getParams(entityMap);

        //创建POST请求
        HttpPost httpPost = new HttpPost(url);

        setHeaders(headers,httpPost);

        //设置ContentType(注:如果只是传普通参数的话,ContentType不一定非要用application/json)
        httpPost.setHeader("Content-Type", "application/json;charset=utf8");

        //配置超时时间
        setConfig(timeOut, httpPost);

        return execute(httpPost);
    }

    /**
     * 发送无参POST请求
     * @param url 请求地址
     * @param timeOut 超时
     * @return
     */
    public static String requestPost(String url, Integer timeOut) {
        return requestPost(url, timeOut, null, null);
    }

    /**
     * 发送普通POST表单请求(不带文件)
     * headers封装：
     *          Content-Type = application/x-www-form-urlencoded : 表单数据将会被编码成键值对
     * @param url 请求地址
     * @param timeOut socket超时时间
     * @param headers 请求头部，可设置Content-Type以外的字段用于特殊应用，Content-Type已经设置了，无须重复设置。如果不需要请传入null
     * @param entityMap 消息体，如果想传数组的话，请用Collection或继承自Collection的类,如List，ArrayList等
     * @return
     */
    public static String requestFormPost(String url, Integer timeOut, Map<String,String> headers, Map<String,Object> entityMap) {
        //创建POST请求
        HttpPost httpPost = new HttpPost(url);

        //设置请求头
        httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf8");

        setHeaders(headers,httpPost);

        List<BasicNameValuePair> params = new LinkedList<>();

        for (Map.Entry<String,Object> entry : entityMap.entrySet()) {
            //键值对都不为空
            if(!(StringUtils.isEmpty(entry.getKey()) || StringUtils.isEmpty(entry.getValue()))) {
                //如果是List，单独处理
                if(entry.getValue() instanceof Collection) {
                    Collection collection = (Collection)entry.getValue();
                    for(Object o : collection) {
                        params.add(new BasicNameValuePair(entry.getKey(),o.toString()));
                    }
                } else {
                    params.add(new BasicNameValuePair(entry.getKey(), entry.getValue().toString()));
                }

            }
        }

        //设置编码，防止中文乱码
        UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(params, StandardCharsets.UTF_8);
        httpPost.setEntity(formEntity);

        //配置超时时间
        setConfig(timeOut, httpPost);

        return execute(httpPost);
    }

    /**
     * 发送带JSON的POST请求
     * @param url 请求地址
     * @param timeOut 超时时间
     * @param headers 请求头部，可设置Content-Type以外的字段用于特殊应用，Content-Type已经设置了，无须重复设置。如果不需要请传入null
     * @param jsonString json字符串
     * @return
     */
    public static String requestJsonPost(String url, Integer timeOut, Map<String,String> headers, String jsonString) {
        //创建post请求
        HttpPost httpPost = new HttpPost(url);

        //设置ContentType
        httpPost.setHeader("Content-Type", "application/json;charset=utf8");

        setHeaders(headers, httpPost);

        //防止中文乱码
        StringEntity stringEntity = new StringEntity(jsonString,"UTF-8");

        httpPost.setEntity(stringEntity);

        setConfig(timeOut, httpPost);

        return execute(httpPost);
    }

    /**
     * 发送带JSON的POST请求
     * @param url 请求地址
     * @param timeOut 超时时间
     * @param headers 请求头部，可设置Content-Type以外的字段用于特殊应用，Content-Type已经设置了，无须重复设置。如果不需要请传入null
     * @param entity 消息体,支持Map、自定义类。
     * @return
     */
    public static <T>  String requestJsonPost(String url, Integer timeOut, Map<String,String> headers, T entity) {
        String jsonString = FastJsonUtils.entityToJsonString(entity);

        return requestJsonPost(url,timeOut,headers,jsonString);
    }





}
