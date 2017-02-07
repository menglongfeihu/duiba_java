/**
 * Copyright (c) 2012 Sohu. All Rights Reserved
 */
package com.sohu.tv.duiba.web.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.github.kristofa.brave.Brave;
import com.github.kristofa.brave.http.DefaultSpanNameProvider;
import com.github.kristofa.brave.okhttp.BraveOkHttpRequestResponseInterceptor;

import com.sohu.tv.duiba.util.ResponseUtil;

@Controller
@RequestMapping("/zipkin")
public class ZipkinController {
    protected final Log logger = LogFactory.getLog(getClass());

    @Resource
    FactoryBean<Brave> braveBean;

    @Resource
    FactoryBean<OkHttpClient> okHttpClientBean;

    @RequestMapping("/test1")
    public String test1() {
        try {
            Brave brave = braveBean.getObject();
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new BraveOkHttpRequestResponseInterceptor(brave.clientRequestInterceptor(), brave
                            .clientResponseInterceptor(), new DefaultSpanNameProvider())).build();

            Thread.sleep(100);
            Request request = new Request.Builder().url("http://localhost:8033/zipkin/brave/service2/test2").build();
            Response response = okHttpClient.newCall(request).execute();
            return response.body().string();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "";
    }

    @RequestMapping("/test2")
    public void test2(HttpServletRequest request,HttpServletResponse response,String callback) {
        try {
            Thread.sleep(100);
            Request request1 = new Request.Builder().url("http://localhost:8080/duiba/autologin.json").build();
            Response response1 = okHttpClientBean.getObject().newCall(request1).execute();
            String content = response1.body().string();
            logger.info("body content:"+content);
            ResponseUtil.responseObject(response, content,callback);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return;
    }

}
