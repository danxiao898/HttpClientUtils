package com.yky.http.utils;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.jws.Oneway;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class HttpClientUtilsTest {

    @Test
    void requestGet() {
        String s = HttpClientUtils.requestGet("http://localhost/test/me", 5000);
        System.out.println(s);
    }

    @Test
    void testRequestGet() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("name", "张三");
        map.put("age", 22);
        String s = HttpClientUtils.requestGet("http://localhost/test/me/1", 5000, null, map);
        System.out.println(s);
    }

    @Test
    void requestPost() {
        String s = HttpClientUtils.requestPost("http://localhost/test/me", 5000);
        System.out.println(s);
    }

    @Test
    void testRequestPost() {
        Map<String,Object> map = new LinkedHashMap<>();
        map.put("name", "深圳市");
        String s = HttpClientUtils.requestPost("http://localhost/test/area", 5000, null,map);
        System.out.println(s);
    }

    @Test
    void requestFormPost() {
        Map<String,Object> entity = new LinkedHashMap<>();

        Integer ids[] = {1,2,3};

        entity.put("id", 1);
        entity.put("name", "深圳市");
        entity.put("ids",Arrays.asList(ids));

        String s = HttpClientUtils.requestFormPost("http://localhost/test/area" ,5000, null, entity);
        System.out.println(s);
    }

    @Test
    void requestJsonPost() {
        Map<String,Object> entity = new LinkedHashMap<>();

        entity.put("id", 1);
        entity.put("name", "深圳市");

        String s = HttpClientUtils.requestJsonPost("http://localhost/test/jsonArea" ,5000,null , entity);
        System.out.println(s);
    }

    @Test
    void testRequestJsonPost() {
    }

    @Test
    void testRequestJsonPost1() {
    }
}