/**
 * Copyright (c) 2012 Sohu. All Rights Reserved
 */
package com.sohu.tv.duiba.bean;

import okhttp3.OkHttpClient;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.stereotype.Component;

import com.github.kristofa.brave.Brave;
import com.github.kristofa.brave.EmptySpanCollectorMetricsHandler;
import com.github.kristofa.brave.Sampler;
import com.github.kristofa.brave.SpanCollector;
import com.github.kristofa.brave.http.DefaultSpanNameProvider;
import com.github.kristofa.brave.http.HttpSpanCollector;
import com.github.kristofa.brave.okhttp.BraveOkHttpRequestResponseInterceptor;

public class ZipkinOKHttpClientFactoryBean implements FactoryBean<OkHttpClient> {

    protected final Log logger = LogFactory.getLog(getClass());

    private String serviceName;

    private String zipkinHost;

    private int connectTimeout;

    private int readTimeout;

    private int flushInterval;

    private int traceSample;

    private boolean compressionEnabled;

    private Brave brave;

    private SpanCollector spanCollector;

    private OkHttpClient okHttpClient;

    private void init() {
        try {
            if (okHttpClient == null) {
                synchronized (ZipkinOKHttpClientFactoryBean.class) {
                    if (okHttpClient == null) {
                        // 初始化spanCollector
                        HttpSpanCollector.Config spaConfig = HttpSpanCollector.Config.builder()
                                .compressionEnabled(this.isCompressionEnabled())
                                .connectTimeout(this.getConnectTimeout())
                                .flushInterval(this.getFlushInterval())
                                .readTimeout(this.getReadTimeout())
                                .build();
                        spanCollector = HttpSpanCollector.create(this.getZipkinHost(), spaConfig,
                                new EmptySpanCollectorMetricsHandler());

                        // 初始化brave
                        Brave.Builder builder = new Brave.Builder(this.getServiceName());
                        builder.spanCollector(spanCollector);
                        builder.traceSampler(Sampler.create(this.getTraceSample()));
                        brave = builder.build();

                        // 初始化 okhttpclient
                        okHttpClient = new OkHttpClient.Builder()
                                .addInterceptor(
                                        new BraveOkHttpRequestResponseInterceptor(brave.clientRequestInterceptor(),
                                                brave
                                                        .clientResponseInterceptor(), new DefaultSpanNameProvider()))
                                .build();
                    }
                }
            }
        } catch (Exception e) {
            logger.error("init okhttpclient failed,reason:", e);
        }

    }

    @Override
    public OkHttpClient getObject() throws Exception {
        if (okHttpClient == null) {
            this.init();
        }
        return okHttpClient;
    }

    @Override
    public Class<?> getObjectType() {
        return OkHttpClient.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getZipkinHost() {
        return zipkinHost;
    }

    public void setZipkinHost(String zipkinHost) {
        this.zipkinHost = zipkinHost;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    public int getFlushInterval() {
        return flushInterval;
    }

    public void setFlushInterval(int flushInterval) {
        this.flushInterval = flushInterval;
    }

    public int getTraceSample() {
        return traceSample;
    }

    public void setTraceSample(int traceSample) {
        this.traceSample = traceSample;
    }

    public boolean isCompressionEnabled() {
        return compressionEnabled;
    }

    public void setCompressionEnabled(boolean compressionEnabled) {
        this.compressionEnabled = compressionEnabled;
    }

}
