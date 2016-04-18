<%@page import="org.json.JSONObject"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	import="com.baidu.ueditor.ActionEnter"
    pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%

    request.setCharacterEncoding( "utf-8" );
	response.setHeader("Content-Type" , "text/html");
	String rootPath = "";
	rootPath = application.getRealPath( "/" );
	
	/* String action=request.getParameter("action");
	if("listimage".equals(action) ){
		rootPath = "D:\\upfiles\\image\\";
	}else{
	    rootPath = application.getRealPath( "/" );
	} */
	System.out.println(rootPath);
	
	out.write( new ActionEnter( request, rootPath ).exec() );
	
	
%>