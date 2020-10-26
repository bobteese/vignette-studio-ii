package ConstantVariables;

public class ConstantVariables {

    public static final String IMAGE_RESOURCE_PATH= "/images/plain.png";
    //-------Page Types-----------------
    public static  final String LOGIN_PAGE_TYPE = "Login";
    public static  final String QUESTION_PAGE_TYPE = "Question";
    public static  final String PROBLEM_STATEMENT_PAGE = "Problem Statement";
    public static  final String COMPLETION_PAGE_TYPE = "Completion";
    public static  final String PROBLEM_PAGE_TYPE = "Problem";
    public static  final String RESPONSE_CORRECT_PAGE = "Response Correct";
    public static  final String RESPONSE_INCORRECT_PAGE = "Response Incorrect";
    public static  final String WHAT_LEARNED_PAGE = "What Learned";
    public static final  String CUSTOM_PAGE_TYPE = "Custom";

    //-------- PAGE HTML SOURCE CODE PATH-----------
    public static  final  String imageResourceFolder = "/Images/";
    public static  final  String LOGIN_HTML_SOURCE_PAGE = "/HTMLResources/pages/login.html";
    public static  final  String Q1_HTML_SOURCE_PAGE = "/HTMLResources/pages/q1.html";
    public static  final  String PROBLEM_STATEMENT_HTML_SOURCE_PAGE = "/HTMLResources/pages/problemStatement.html";
    public static  final  String PROBLEM_HTML_SOURCE_PAGE = "/HTMLResources/pages/problem.html";
    public static  final  String WHAT_LEARNED_HTML_SOURCE_PAGE = "/HTMLResources/pages/WhatLearned.html";
    public static  final  String COMPLETION_HTML_SOURCE_PAGE = "/HTMLResources/pages/Completion.html";
    public static  final  String RESPONSE_CORRECT_HTML_SOURCE_PAGE = "/HTMLResources/pages/response_correct.html";
    public static  final  String RESPONSE_INCORRECT_SOURCE_PAGE = "/HTMLResources/pages/response_incorrect.html";

    //---------Frame work folder -------------
    public static final String FRAMEWORK_RESOURCE_FOLDER = "/HTMLResources/framework.zip";


    //-----LIST OF PAGE TYPES USED IN DROP DOWN-------------
    public static  final  String[] listOfPageTypes = {"Please select page type",COMPLETION_PAGE_TYPE,LOGIN_PAGE_TYPE,
            QUESTION_PAGE_TYPE,PROBLEM_PAGE_TYPE,
                                                      PROBLEM_STATEMENT_PAGE,RESPONSE_CORRECT_PAGE,
                                                      RESPONSE_INCORRECT_PAGE,WHAT_LEARNED_PAGE,CUSTOM_PAGE_TYPE};

}
