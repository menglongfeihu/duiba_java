/**
 * Copyright (c) 2012 Sohu. All Rights Reserved
 */
package com.sohu.tv.duiba.web.controller;

import javax.annotation.Resource;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.github.kristofa.brave.Brave;
import com.github.kristofa.brave.http.DefaultSpanNameProvider;
import com.github.kristofa.brave.okhttp.BraveOkHttpRequestResponseInterceptor;

@Controller
@RequestMapping("/zipkin")
public class ZipkinController {

    @Resource
    FactoryBean<Brave> braveFactoryBean;

    @RequestMapping("/test1")
    public String test1() {
        try {
            Brave brave = braveFactoryBean.getObject();
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

}
