package com.kzk.kblog.controller;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class IndexController {

  // 日志工具 使用slf4j API，实际调用log4j
  private static final Logger logger = LoggerFactory.getLogger(IndexController.class);

  @RequestMapping(value = { "/", "/index.html" })
  private String index() {
    logger.debug("public/index");
    final Subject user = SecurityUtils.getSubject();
    if (user.isAuthenticated()) {
      logger.debug(user.getPrincipal().toString());
    }
    return "public/index";
  }

  @RequestMapping(value = "/login.html")
  private String showLogin() {
    final Subject user = SecurityUtils.getSubject();
    logger.debug("User is authenticated:  " + user.isAuthenticated());
    return "public/login";
  }

  @RequestMapping(value = "/login.do")
  @ResponseBody
  private Map<String, Object> doLogin(@RequestParam(required = false) final String userName,
      @RequestParam(required = false) final String password) {
    Map<String, Object> resultMap = new HashMap<String, Object>();
    logger.debug("/login.do?userName=" + userName + "password=" + password);
    final Subject user = SecurityUtils.getSubject();
    logger.debug("before login User is authenticated:  " + user.isAuthenticated());

    final UsernamePasswordToken token = new UsernamePasswordToken(userName, password, false);

    try {
      // 一旦用户尝试登录，先做登出处理
      user.logout();
      user.login(token);
      resultMap.put("userName", user.getPrincipal());
      if (user.hasRole("admin")) {
        resultMap.put("role", "admin");
      } else if (user.hasRole("member")) {
        resultMap.put("role", "member");
      } else {
        resultMap.put("role", "guest");
      }
    } catch (final AuthenticationException e) {
      logger.debug(e.toString());
    }
    logger.debug("after login User is authenticated:  " + user.isAuthenticated());
    resultMap.put("state", user.isAuthenticated());

    return resultMap;
  }

  @RequestMapping(value = "/loginState.do")
  @ResponseBody
  private Map<String, Object> getLoginState() {
    Map<String, Object> resultMap = new HashMap<String, Object>();

    final Subject user = SecurityUtils.getSubject();
    logger.debug("User is authenticated:  " + user.isAuthenticated());

    if (user.isAuthenticated()) {
      resultMap.put("userName", user.getPrincipal());
      if (user.hasRole("admin")) {
        resultMap.put("role", "admin");
      } else if (user.hasRole("member")) {
        resultMap.put("role", "member");
      } else {
        resultMap.put("role", "guest");
      }
    }
    resultMap.put("state", user.isAuthenticated());

    return resultMap;
  }

  @RequestMapping(value = "/logout.html")
  private String showLogout() {
    final Subject user = SecurityUtils.getSubject();
    logger.debug("User is authenticated:  " + user.isAuthenticated());
    return "public/login";
  }

  @RequestMapping(value = "/logout.do")
  @ResponseBody
  private Map<String, Object> doLogout() {
    Map<String, Object> resultMap = new HashMap<String, Object>();
    final Subject user = SecurityUtils.getSubject();
    logger.debug("before logout User is authenticated:  " + user.isAuthenticated());
    user.logout();
    resultMap.put("state", user.isAuthenticated());
    logger.debug("after logout User is authenticated:  " + user.isAuthenticated());

    return resultMap;
  }

}
