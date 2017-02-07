/**
 * Copyright (c) 2012 Sohu. All Rights Reserved
 */
package com.sohu.tv.duiba.bean;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.FactoryBean;

import com.github.kristofa.brave.Brave;
import com.github.kristofa.brave.EmptySpanCollectorMetricsHandler;
import com.github.kristofa.brave.http.HttpSpanCollector;

public class ZipkinBraveFactoryBean implements FactoryBean<Brave> {
    private String serviceName;
    private String zipkinHost;

    private Brave brave;

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public void setZipkinHost(String zipkinHost) {
        this.zipkinHost = zipkinHost;
    }

    private void getInstatnce() {
        if (this.serviceName == null) {
            throw new BeanInitializationException("property serviceName must be set.");
        }
        Brave.Builder builder = new Brave.Builder(this.serviceName);
        if (StringUtils.isNotBlank(this.zipkinHost)) {
            builder.spanCollector(HttpSpanCollector.create(this.zipkinHost, new EmptySpanCollectorMetricsHandler()));
        }
        this.brave = builder.build();
    }

    @Override
    public Brave getObject() throws Exception {
        if (this.brave == null) {
            this.getInstatnce();
        }
        return brave;
    }

    @Override
    public Class<?> getObjectType() {
        return Brave.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}
