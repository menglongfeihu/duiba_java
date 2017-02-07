/**
 * Copyright (c) 2012 Sohu. All Rights Reserved
 */
package com.sohu.tv.duiba.util;

import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.collect.Lists;


public class ResponseUtil {
    private static Logger logger = Logger.getLogger(ResponseUtil.class);

    public static final int STATUS_SUCCESS = 200;

    public static final int STATUS_LACK_PARAM = 100; // 缺少参数

    public static final int STATUS_UID_TOKEN_EMPTY = 101; // uid或token空

    public static final int STATUS_USER_INVALID = 102; // 用户不存在

    public static final int STATUS_TOKEN_INVALID = 103; // token无效

    public static final int STATUS_FAIL = 104; // 系统错误

    public static final int STATUS_NO_PRIVILEGE = 105; // 权限不够

    public static final int STATUS_ERROR_PARAM = 106; // 参数错误

    public static final int STATUS_CODE_SEND_TOO_MANY = 107;

    public static final int STATUS_CODE_INVALID = 108; // 验证码无效

    public static final int STATUS_NO_LOGIN = 109; // 用户没登录

    public static final int STATUS_NOT_BIND_PHONE = 110; // 没有绑定手机号

    public static final int STATUS_HAS_SIGN = 111; // 今天已经签到过

    public static final int STATUS_HAS_GIFT = 112;// 用户已经领取过大礼包

    public static final int STATUS_VIP_ACTIVE_FAILURE = 113; // VIP激活失败

    public static final int STATUS_RAFFLE_NOT_START = 210; // 本次活动未开始

    public static final int STATUS_RAFFLE_HAS_STOPED = 211; // 本次活动已结束

    public static final int STATUS_RAFFLE_NOT_NORMAL = 212; // 本次活动不存在或者状态不正常

    public static final int STATUS_RAFFLE_TICKET_ENOUGH = 213; // 参加票数已达上限

    public static final int STATUS_USER_IN_RAFFLE = 214; // 用户已经参加过抽奖

    public static final int STATUS_FREQUENT_OPERATION = 215; //操作过于频繁

    public static final int STATUS_USER_NO_RAFFLE = 216; //用户没有押宝

    public static final int STATUS_USER_ENOUGH_LIKE = 217; //用户集运已满

    public static final int STATUS_USER_ALREADY_LIKE = 218; //已经为该用户点赞


    /**
     * json response
     *
     * @param response
     * @param json
     */
    public static void printJson(HttpServletResponse response, String json) {
        printJson(response, json, "");
    }

    /**
     * jsonp response
     *
     * @param response
     * @param json
     * @param callback
     */
    public static void printJson(HttpServletResponse response, String json,
            String callback) {
        OutputStream out = null;
        if (StringUtils.isEmpty(json)) {
            logger.warn("response json is empty");
            return;
        }

        if (StringUtils.isNotBlank(callback)) {
            StringBuilder result = new StringBuilder(json.length() + 16);
            result.append(callback);
            result.append('(');
            result.append(json);
            result.append(')');
            json = result.toString();
        }

        try {
            out = response.getOutputStream();
            byte[] bytes = json.getBytes("utf-8");
            response.setStatus(200);
            response.setContentLength(bytes.length);
            response.setCharacterEncoding("utf-8");
            response.setContentType("application/json; charset=UTF-8");
            response.setDateHeader("Expires", 0);
            response.setHeader("Pragma", "no-cache");
            // HTTP 1.1
            response.setHeader("Cache-Control", "No-cache");
            out.write(bytes);
            out.flush();
        } catch (Exception e) {
            logger.error("ResponseUtil--printJson--error ", e);
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (Exception e) {
            }
        }
    }

    public static final String RESPONSE_LACK_PARAM;

    public static final String RESPONSE_ERROR_PARAM;

    public static final String RESPONSE_SYSTEM_ERROR;

    public static final String RESPONSE_NO_PRIVILEGE;

    public static final String RESPONSE_OK;

    public static final String RESPONSE_NOT_BIND_PHONE;

    public static final String RESPONSE_HAS_SIGN;

    public static final String RESPONSE_NO_LOGIN;

    public static final String RESPONSE_HAS_GIFT;

    public static final String RESPONSE_RAFFLE_TICKET_ENOUGH;

    public static final String RESPONSE_USER_IN_RAFFLE;

    public static final String RESPONSE_TOO_FREQUENT_OPERATION;

    public static final String RESPONSE_USER_NO_RAFFLE;
    public static final String RESPONSE_USER_ENOUGH_LIKE;



    static {

        JSONObject obj = new JSONObject();
        obj.put("status", STATUS_LACK_PARAM);
        obj.put("message", "lack required parameter");
        RESPONSE_LACK_PARAM = obj.toString();

        obj = new JSONObject();
        obj.put("status", STATUS_ERROR_PARAM);
        obj.put("message", "param error");
        RESPONSE_ERROR_PARAM = obj.toString();

        obj = new JSONObject();
        obj.put("status", STATUS_FAIL);
        obj.put("message", "system error");
        RESPONSE_SYSTEM_ERROR = obj.toString();

        obj = new JSONObject();
        obj.put("status", STATUS_NO_PRIVILEGE);
        obj.put("message", "this user has no privilege");
        RESPONSE_NO_PRIVILEGE = obj.toString();

        obj = new JSONObject();
        obj.put("status", STATUS_SUCCESS);
        obj.put("message", "operate succ");
        RESPONSE_OK = obj.toString();

        obj = new JSONObject();
        obj.put("status", STATUS_NOT_BIND_PHONE);
        obj.put("message", "this user has not bind phone");
        RESPONSE_NOT_BIND_PHONE = obj.toString();

        obj = new JSONObject();
        obj.put("status", STATUS_HAS_SIGN);
        obj.put("message", "has sign today.");
        RESPONSE_HAS_SIGN = obj.toString();

        obj = new JSONObject();
        obj.put("status", STATUS_NO_LOGIN);
        obj.put("message", "user not login");
        RESPONSE_NO_LOGIN = obj.toString();

        obj = new JSONObject();
        obj.put("status", STATUS_HAS_GIFT);
        obj.put("message", "user has gift");
        RESPONSE_HAS_GIFT = obj.toString();

        obj = new JSONObject();
        obj.put("status", STATUS_RAFFLE_TICKET_ENOUGH);
        obj.put("message", "raffle ticket enough");
        RESPONSE_RAFFLE_TICKET_ENOUGH = obj.toString();

        obj = new JSONObject();
        obj.put("status", STATUS_USER_IN_RAFFLE);
        obj.put("message", "user already in this raffle");
        RESPONSE_USER_IN_RAFFLE= obj.toString();

        obj = new JSONObject();
        obj.put("status", STATUS_FREQUENT_OPERATION);
        obj.put("message", "too frequent operation");
        RESPONSE_TOO_FREQUENT_OPERATION= obj.toString();

        obj = new JSONObject();
        obj.put("status", STATUS_USER_NO_RAFFLE);
        obj.put("message", "user has no raffle");
        RESPONSE_USER_NO_RAFFLE= obj.toString();

        obj = new JSONObject();
        obj.put("status", STATUS_USER_ENOUGH_LIKE);
        obj.put("message", "user had enough like");
        RESPONSE_USER_ENOUGH_LIKE= obj.toString();

    }

    public static void responseLackParam(HttpServletResponse response) {
        printJson(response, RESPONSE_LACK_PARAM);
    }

    public static void responseLackParam(HttpServletResponse response,
            String callback) {
        printJson(response, RESPONSE_LACK_PARAM, callback);
    }

    public static void responseErrorParam(HttpServletResponse response) {
        printJson(response, RESPONSE_ERROR_PARAM);
    }

    public static void responseErrorParam(HttpServletResponse response, String callback) {
        printJson(response, RESPONSE_ERROR_PARAM, callback);
    }

    public static void responseUidTokenEmpty(HttpServletResponse response) {
        JSONObject obj = new JSONObject();
        obj.put("status", STATUS_UID_TOKEN_EMPTY);
        obj.put("message", "userId or token is empty");
        printJson(response, obj.toString());
    }

    public static void responseUidTokenEmpty(HttpServletResponse response,
            String callback) {
        JSONObject obj = new JSONObject();
        obj.put("status", STATUS_UID_TOKEN_EMPTY);
        obj.put("message", "userId or token is empty");
        printJson(response, obj.toString(), callback);
    }

    public static void responseValidUser(HttpServletResponse response) {
        JSONObject obj = new JSONObject();
        obj.put("status", STATUS_USER_INVALID);
        obj.put("message", "user is invalid");
        printJson(response, obj.toString());
    }

    public static void responseValidUser(HttpServletResponse response,
            String callback) {
        JSONObject obj = new JSONObject();
        obj.put("status", STATUS_USER_INVALID);
        obj.put("message", "user is invalid");
        printJson(response, obj.toString(), callback);
    }

    public static void responseValidToken(HttpServletResponse response) {
        JSONObject obj = new JSONObject();
        obj.put("status", STATUS_TOKEN_INVALID);
        obj.put("message", "token is invalid");
        printJson(response, obj.toString());
    }

    public static void responseCodeInvalid(HttpServletResponse response) {
        JSONObject obj = new JSONObject();
        obj.put("status", STATUS_CODE_INVALID);
        obj.put("message", "code is invalid");
        printJson(response, obj.toString());
    }

    public static void responseValidToken(HttpServletResponse response,
            String callback) {
        JSONObject obj = new JSONObject();
        obj.put("status", STATUS_TOKEN_INVALID);
        obj.put("message", "token is invalid");
        printJson(response, obj.toString(), callback);
    }

    public static void responseSystemError(HttpServletResponse response) {
        printJson(response, RESPONSE_SYSTEM_ERROR);
    }

    public static void responseSystemError(HttpServletResponse response,
            String callback) {
        printJson(response, RESPONSE_SYSTEM_ERROR, callback);
    }

    public static void responseNoPrivilege(HttpServletResponse response) {
        printJson(response, RESPONSE_NO_PRIVILEGE);
    }

    public static void responseNoPrivilege(HttpServletResponse response,
            String callback) {
        printJson(response, RESPONSE_NO_PRIVILEGE, callback);
    }

    public static void responseOK(HttpServletResponse response) {
        printJson(response, RESPONSE_OK);
    }

    public static void responseOK(HttpServletResponse response, String callback) {
        printJson(response, RESPONSE_OK, callback);
    }

    public static void responseObject(HttpServletResponse response,
            Object object) {
        printJson(response, getOkResponse(object));
    }

    public static void responseObject(HttpServletResponse response,
            Object object, String callback) {
        printJson(response, getOkResponse(object), callback);
    }

    public static void responseObjectWithContentType(HttpServletResponse response,
            Object object, String callback, String contentType) {
        printJsonWithContentType(response, getOkResponse(object), callback, contentType);
    }

    public static String getOkResponse(Object object) {
        JSONObject obj = new JSONObject();
        obj.put("status", STATUS_SUCCESS);
        obj.put("message", object);
        return JSON.toJSONString(obj, SerializerFeature.DisableCircularReferenceDetect);
    }

    public static void responseObject(HttpServletResponse response, Object object, int status) {
        JSONObject obj = new JSONObject();
        obj.put("status", status);
        obj.put("message", object);
        printJson(response, obj.toJSONString());
    }

    public static void responseObject(HttpServletResponse response, Object object, int status, String callback) {
        JSONObject obj = new JSONObject();
        obj.put("status", status);
        obj.put("message", object);
        printJson(response, obj.toJSONString(), callback);
    }

    public static void responseObject2App(HttpServletResponse response, Object object, int status, String desc,
            String callback) {
        JSONObject obj = new JSONObject();
        obj.put("status", status);
        obj.put("statusText", desc);
        obj.put("data", object);
        printJson(response, obj.toJSONString(), callback);
    }

    public static void responseNotBindPhone(HttpServletResponse response) {
        printJson(response, RESPONSE_NOT_BIND_PHONE);
    }

    public static void responseNotBindPhone(HttpServletResponse response,
            String callback) {
        printJson(response, RESPONSE_NOT_BIND_PHONE, callback);
    }

    public static void responseHasSign(HttpServletResponse response,
            String callback) {
        printJson(response, RESPONSE_HAS_SIGN, callback);
    }

    public static void responseNotLogin(HttpServletResponse response) {
        printJson(response, RESPONSE_NO_LOGIN);
    }

    public static void responseNotLogin(HttpServletResponse response,
            String callback) {
        printJson(response, RESPONSE_NO_LOGIN, callback);
    }

    public static void responseUserHasGift(HttpServletResponse response) {
        printJson(response, RESPONSE_HAS_GIFT);
    }

    public static void responseUserHasGift(HttpServletResponse response,
            String callback) {
        printJson(response, RESPONSE_HAS_GIFT, callback);
    }

    public static void responseRaffleEnoughTicket(HttpServletResponse response) {
        printJson(response, RESPONSE_RAFFLE_TICKET_ENOUGH);
    }

    public static void responseRaffleEnoughTicket(HttpServletResponse response,
            String callback) {
        printJson(response, RESPONSE_RAFFLE_TICKET_ENOUGH, callback);
    }

    public static void responseUserInRaffle(HttpServletResponse response) {
        printJson(response, RESPONSE_USER_IN_RAFFLE);
    }

    public static void responseUserInRaffle(HttpServletResponse response,
            String callback) {
        printJson(response, RESPONSE_USER_IN_RAFFLE, callback);
    }

    public static void responseTooFrequentOP(HttpServletResponse response,
                                             String callback){
        printJson(response, RESPONSE_TOO_FREQUENT_OPERATION, callback);

    }

    public static Map<String, Object> convertToMap(Object bean, String[] columns) throws Exception {
        if ((bean != null) && (columns != null) && (columns.length > 0)) {
            Map<String, Object> map = new HashMap<String, Object>();
            Class beanCla = bean.getClass();
            for (String colName : columns) {
                Field field = beanCla.getDeclaredField(colName);
                if (field != null) {
                    field.setAccessible(true); // 设置些属性是可以访问的
                    Object val = field.get(bean); // 得到此属性的值
                    String name = field.getName();
                    map.put(name, val);
                }
            }
            return map;
        }
        return null;
    }

    /**
     * <p>
     * Description: 复制部分属性，用于返回json结果
     * </p>
     *
     * @param <T>
     * @param list 原对象列表
     * @param columns 想要复制的属性值
     * @return
     */
    public static <T> List<Map<String, Object>> convertToMapList(List<T> list, String[] columns) {
        try {
            List<Map<String, Object>> results = Lists.newArrayList();
            if (CollectionUtils.isNotEmpty(list)) {
                for (T object : list) {
                    Map<String, Object> map = convertToMap(object, columns);
                    if (map != null) {
                        results.add(map);
                    } else {
                        return null;
                    }
                }
            }
            return results;
        } catch (Exception e) {
            logger.error("convertToMapList error", e);
        }
        return null;
    }

    /**
     * json response
     *
     * @param response
     * @param json
     */
    public static void printJsonWithContentType(HttpServletResponse response, String json, String contentType) {
        printJsonWithContentType(response, json, "", contentType);
    }

    /**
     * jsonp response
     *
     * @param response
     * @param json
     * @param callback
     */
    public static void printJsonWithContentType(HttpServletResponse response, String json,
            String callback, String contentType) {
        OutputStream out = null;
        if (StringUtils.isEmpty(json)) {
            logger.warn("response json is empty");
            return;
        }

        if (StringUtils.isNotBlank(callback)) {
            StringBuilder result = new StringBuilder(json.length() + 16);
            result.append(callback);
            result.append('(');
            result.append(json);
            result.append(')');
            json = result.toString();
        }

        try {
            out = response.getOutputStream();
            byte[] bytes = json.getBytes("utf-8");
            response.setStatus(200);
            response.setContentLength(bytes.length);
            response.setCharacterEncoding("utf-8");
            // response.setHeader("Access-Control-Allow-Origin", "*");
            if (contentType == null) {
                response.setContentType("application/json; charset=UTF-8");
            } else {
                response.setContentType(contentType);
            }
            response.setDateHeader("Expires", 0);
            response.setHeader("Pragma", "no-cache");
            // HTTP 1.1
            response.setHeader("Cache-Control", "No-cache");
            out.write(bytes);
            out.flush();
        } catch (Exception e) {
            logger.error("ResponseUtil--printJson--error ", e);
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (Exception e) {
            }
        }
    }

    public static void responseVipActiveFailed(HttpServletResponse response, String reason, String callback) {
        responseObject(response, reason, STATUS_VIP_ACTIVE_FAILURE, callback);
    }

    public static void responseNoRaffle(HttpServletResponse response, String callback) {
        printJson(response, RESPONSE_USER_NO_RAFFLE, callback);
    }

    public static void responseEnoughLike(HttpServletResponse response, String callback) {
        printJson(response, RESPONSE_USER_ENOUGH_LIKE, callback);
    }
}
