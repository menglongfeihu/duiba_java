/**
 * Copyright (c) 2012 Sohu. All Rights Reserved
 */
package com.sohu.tv.duiba.web.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.com.duiba.credits.sdk.CreditTool;

import com.netflix.config.DynamicPropertyFactory;
import com.netflix.config.DynamicStringProperty;

import com.sohu.blog.web.util.RequestUtil;
import com.sohu.spaces.user.model.Account;
import com.sohu.spaces.user.service.AccountService;
import com.sohu.starfans.util.GsonHelper;
import com.sohu.starfans.util.PassportLoginChecker;
import com.sohu.tv.duiba.util.ConstantConfig;

@Controller
@RequestMapping("/duiba")
public class DuibaController {

    private final static Log log = LogFactory.getLog(DuibaController.class);

    @Resource
    private AccountService accountService;

    @RequestMapping("/autologin")
    public String autoLoginUrl(HttpServletRequest request, HttpServletResponse response) {

        Map<String, String[]> params = request.getParameterMap();
        log.info("request params : " + GsonHelper.toGson(params));

        // 获取参数
        String appKey = RequestUtil.getString(request, "dbappkey", "");
        DynamicStringProperty appSecretProperty = DynamicPropertyFactory.getInstance().getStringProperty(appKey, "");
        String appSecret = appSecretProperty.get();
        log.info("appKey =" + appKey + ",appSecret =" + appSecret);

        String autoLoginUrl = generateAutoLoginUrl(appKey, appSecret, request);
        log.info("autoLoginUrl =" + autoLoginUrl);
        return "redirect:" + autoLoginUrl;
    }
    private String generateAutoLoginUrl(String appkey, String appSecret, HttpServletRequest request) {
        Map<String, String> params = new HashMap<String, String>();
        String redirect = RequestUtil.getString(request, "dbredirect", "");
        if (StringUtils.isNotBlank(redirect)) {
            params.put("redirect", redirect);
        }

        Account account = this.getAccount(request);
        // Long uid = this.getAccountUid(request);
        if (account != null) {
            params.put("uid", String.valueOf(account.getId()));
            // params.put("uid", String.valueOf(uid));
        } else {
            params.put("uid", "not_login");
        }
        params.put("credits", "0"); // credits参数默认写死为0
        String timestamp = String.valueOf(new Date().getTime());
        params.put("timestamp", timestamp);

        CreditTool tool = new CreditTool(appkey, appSecret);
        String autoLoginUrl = tool.buildUrlWithSign(ConstantConfig.DUIBA_AUTO_LOGIN_URL, params);

        return autoLoginUrl;
    }

    private Account getAccount(HttpServletRequest request) {
        Account account = null;
        try {
            String passport = RequestUtil.getString(request, "passport", "");
            String token = RequestUtil.getString(request, "token", "");
            boolean isLogin = false;
            if (StringUtils.isNotBlank(passport) && StringUtils.isNotBlank(token)) {
                // 判断用户是否已经登录
                isLogin = PassportLoginChecker.check(passport, token);
            }
            if (isLogin) {
                // 用户登录，获取Account信息
                account = accountService.getAccountByPassport(passport);
            }
        } catch (Exception e) {
            log.error("error : get account info failed :", e);
        }
        return account;

    }

    @SuppressWarnings("unused")
    private Long getAccountUid(HttpServletRequest request) {
        Long uid = null;
        try {
            String passport = RequestUtil.getString(request, "passport", "");
            String token = RequestUtil.getString(request, "token", "");
            boolean isLogin = false;
            if (StringUtils.isNotBlank(passport) && StringUtils.isNotBlank(token)) {
                // 判断用户是否已经登录
                isLogin = PassportLoginChecker.check(passport, token);
            }
            if (isLogin) {
                uid = accountService.getUidByPassport(passport);
            }
        } catch (Exception e) {
            log.error("error : get account info failed :", e);
        }
        return uid;
    }
}
