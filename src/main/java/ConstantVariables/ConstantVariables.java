package ConstantVariables;

import Vignette.Framework.ReadFramework;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class ConstantVariables {

    //-------Image Paths
    public static final String DEFAULT_RESOURCE_PATH= "/images/plain.png";

    public static final String LOGIN_RESOURCE_PATH= "/images/login.png";
    public static final String PROBLEM_RESOURCE_PATH = "/images/problem.png";
    public static final String QUESTION_RESOURCE_PATH = "/images/question.png";
    public static final String WHATLEARNED_RESOURCE_PATH = "/images/whatlearned.png";
    public static final String RESPONSECORRECT_RESOURCE_PATH ="/images/correct.png";
    public static final String RESPONSEINCORRECT_RESOURCE_PATH ="/images/incorrect.png";
    public static final String CREDITS_RESOURCE_PATH ="/images/credits.png";
    public static final String COMPLETION_RESOURCE_PATH = "/images/completion.png";
    public static final String CUSTOM_RESOURCE_PATH = "/images/custom.png";
    public static final String PROBLEMSTATEMENT_RESOURCE_PATH = "/images/problemstatement.png";



    public static final String Documentation_PATH = "/pdf/Vignette Studio Documentation.pdf";



    public static final String IMAGE_ICON_RESOURCE_PATH= "/images/favicon.png";
    //-------Page Types-----------------
    public static  final String LOGIN_PAGE_TYPE = "login";
    public static  final String QUESTION_PAGE_TYPE = "q";

    public static  final String PROBLEMSTATEMENT_PAGE_TYPE = "problemStatement";
    public static  final String COMPLETION_PAGE_TYPE = "completion";
    public static  final String PROBLEM_PAGE_TYPE = "Problem";
    public static  final String RESPONSE_CORRECT_PAGE_TYPE = "response_correct";
    public static  final String RESPONSE_INCORRECT_PAGE_TYPE = "response_incorrect";
    public static  final String WHAT_LEARNED_PAGE_TYPE = "whatLearned";
    public static final  String CUSTOM_PAGE_TYPE = "Custom";
    public static final  String CREDIT_PAGE_TYPE = "Credit";

    //-------- PAGE HTML SOURCE CODE PATH-----------
    public static  final  String imageResourceFolder = "Images/";
    public static  final  String LOGIN_HTML_SOURCE_PAGE = "/HTMLResources/framework/pages/login.html";
    public static  final  String Q1_HTML_SOURCE_PAGE = "/HTMLResources/framework/pages/q.html";
    public static  final  String PROBLEM_STATEMENT_HTML_SOURCE_PAGE = "/HTMLResources/framework/pages/problemStatement.html";
    public static  final  String PROBLEM_HTML_SOURCE_PAGE = "/HTMLResources/framework/pages/problem.html";
    public static  final  String WHAT_LEARNED_HTML_SOURCE_PAGE = "/HTMLResources/framework/pages/WhatLearned.html";
    public static  final  String COMPLETION_HTML_SOURCE_PAGE = "/HTMLResources/framework/pages/Completion.html";
    public static  final  String RESPONSE_CORRECT_HTML_SOURCE_PAGE = "/HTMLResources/framework/pages/response_correct.html";
    public static  final  String RESPONSE_INCORRECT_SOURCE_PAGE = "/HTMLResources/framework/pages/response_incorrect.html";
    public static  final  String CREDIT_HTML_SOURCE_PAGE = "/HTMLResources/framework/pages/credits.html";
    public static final  String  SCRIPT_FOR_CUSTOM_PAGE = "<script>\n" +
            "\n" +
            "questionType= '';\n" +
            "NextPageName=\"q1a\";\n" +
            "NextPageAnswerNames=[[ 'A','q1a'],[ 'B','q1b'],[ 'C','p1'],[ 'default', 'q1a' ]];\n" +
            "\n" +
            "\n" +
            "$(\"#options\").prop('disabled', false).css('opacity', 1);\n" +
            "$(\"#problemStatement\").prop('disabled', false).css('opacity', 1);\n" +
            "$(\"#PrevPage\").prop('disabled', false).css('opacity', 1);\n" +
            "$(\"#NextPage\").prop('disabled', false).css('opacity', 1);\n" +
            "\n" +
            "reloadAnswers();\n" +
            "$(document).ready(function() {\n" +
            "\tinitCommentsSection();\n" +
            "});  \n" +
            "</script><!-- //////// Video script //////// -->\n" +
            "<script>$(document).ready(function() { var iframe = document.getElementById(\"pageVimeoPlayer\"); pageVimPlayer = new Vimeo.Player(iframe, vimeoOptions);  if( showVideoText == 'text') { pageVimPlayer.pause(); $(\".video\").hide(); $(\".text\").show(); } if( showVideoText == 'video') { pageVimPlayer.play(); $(\".video\").show(); $(\".text\").hide(); }\n" +
            "});  </script>";

    public static final String YOUTUBE_VIDEO_SCRIPT = "<script>\n"+
            "if(playerChoice == 1){\n" +
            "        var tag = document.createElement('script');\n" +
            "        tag.src = \"https://www.youtube.com/iframe_api\";\n" +
            "        var firstScriptTag = document.getElementsByTagName('script')[0];\n" +
            "        firstScriptTag.parentNode.insertBefore(tag, firstScriptTag);\n" +
            "        var id = \"Tn6-PIqc4UM\";\n" +
            "        var player;\n" +
            "        $(\".video\").show();\n" +
            "      $(\".text\").hide();    \n" +
            "      onPlayerReady = function(event){\n" +
            "            // console.log(event);\n" +
            "        }\n" +
            "        onPlayerStateChange = function(event){\n" +
            "          if(event.data === 1){\n" +
            "            timer1 = setInterval(function () {\n" +
            "              !player.getCurrentTime ? localStorage.setItem(\"VideoPosition\", 0.0) : localStorage.setItem(\"VideoPosition\", player.getCurrentTime());\n" +
            "            }, 100)\n" +
            "              if(timeSpent.length != parseInt(localStorage.getItem(\"VideoLength\"))){\n" +
            "                timeSpent = new Array(parseInt(localStorage.getItem(\"VideoLength\")));\n" +
            "              }\n" +
            "              console.log(timeSpent.length);\n" +
            "              // timer = setInterval(record,1000);\n" +
            "            }else{\n" +
            "              clearInterval(timer);\n" +
            "              clearInterval(timer1);\n" +
            "            }\n" +
            "        }\n" +
            "            player = new YT.Player('player', {\n" +
            "              height: '550',\n" +
            "              width: '1000',\n" +
            "              videoId: id,\n" +
            "              allow: \"autoplay\",\n" +
            "              playerVars: {\n" +
            "                'playsinline': 1,\n" +
            "                'autoplay' : 1, \n" +
            "                'rel': 0\n" +
            "              },\n" +
            "              events: {\n" +
            "                'onReady': onPlayerReady,\n" +
            "                'onStateChange': onPlayerStateChange\n" +
            "              }\n" +
            "            });\n" +
            "            function pauseYoutubeVideo(){\n" +
            "              player.pauseVideo();\n" +
            "            }\n" +
            "            \n" +
            "            function playYoutubeVideo(){\n" +
            "              player.playVideo();\n" +
            "            }\n" +
            "            getVideoDetails(id);\n" +
            "          }"+
            "</script>\n";


    public static final String VIMEO_VIDEO_SCRIPT = "<script>\n" +
            "    if(playerChoice == 0){\n" +
            "        $('#player').append(\"<iframe id='pageVimeoPlayer' class='col-12 embed-responsive-item vimPlay1'\" +\n" +
            "        \"title='video' src='https://player.vimeo.com/video/554566606' allow='autoplay; fullscreen' width='1000' height='550'></iframe>\");\n" +
            "        var iframe = document.getElementById(\"pageVimeoPlayer\");\n" +
            "        pageVimPlayer = new Vimeo.Player(iframe, vimeoOptions);\n" +
            "        pageVimPlayer.on(\"play\", function () {\n" +
            "        numberOfVimeoPlays = numberOfVimeoPlays + 1;\n" +
            "    });\n" +
            "    if (showVideoText == \"text\") {\n" +
            "      pageVimPlayer.pause();\n" +
            "      $(\".video\").hide();\n" +
            "      $(\".text\").show();\n" +
            "    }\n" +
            "    if (showVideoText == \"video\") {\n" +
            "      pageVimPlayer.play();\n" +
            "      $(\".video\").show();\n" +
            "      $(\".text\").hide();\n" +
            "    }\n" +
            "    pageVimPlayer.play();\n" +
            "    }\n" +
            "    </script>";


    public static String VIMEO_VIDEO_OPTION = "vimeo";
    public static String YOUTUBE_VIDEO_OPTION = "youtube";

    public static final String[] PAGE_TYPE_SOURCE_ARRAY = {LOGIN_HTML_SOURCE_PAGE, Q1_HTML_SOURCE_PAGE, PROBLEM_STATEMENT_HTML_SOURCE_PAGE,
            COMPLETION_HTML_SOURCE_PAGE,PROBLEM_HTML_SOURCE_PAGE, WHAT_LEARNED_HTML_SOURCE_PAGE,
             RESPONSE_CORRECT_HTML_SOURCE_PAGE, RESPONSE_INCORRECT_SOURCE_PAGE,
            CREDIT_HTML_SOURCE_PAGE};
    public static final String[] PAGE_TYPE_ARRAY = {LOGIN_PAGE_TYPE, QUESTION_PAGE_TYPE, PROBLEMSTATEMENT_PAGE_TYPE
            ,COMPLETION_PAGE_TYPE, PROBLEM_PAGE_TYPE, WHAT_LEARNED_PAGE_TYPE,
            RESPONSE_CORRECT_PAGE_TYPE, RESPONSE_INCORRECT_PAGE_TYPE,
            CREDIT_PAGE_TYPE};

    public static final HashMap<String,String> PAGE_TYPE_LINK_MAP = new HashMap<>();

    //---------Frame work folder -------------
    public static final String FRAMEWORK_RESOURCE_FOLDER = "/HTMLResources/framework.zip";

    // ---------HTML Pages directory name---------
    public static final String PAGE_DIRECTORY = "/pages";
    public static final String DATA_DIRECTORY = "/data";
    public static  final  String CUSTOM_CSS_SOURCE_PAGE = "/HTMLResources/css/custom.css";


    public static final  String CSS_DIRECTORY = "/css/custom.css";
    public static final String VIGNETTE_SETTING ="courseInfo.js";

    //-----LIST OF PAGE TYPES USED IN DROP DOWN-------------
    public static  final  String[] listOfPageTypes = {"Please select page type",COMPLETION_PAGE_TYPE,LOGIN_PAGE_TYPE,
            QUESTION_PAGE_TYPE,PROBLEM_PAGE_TYPE,PROBLEMSTATEMENT_PAGE_TYPE,RESPONSE_CORRECT_PAGE_TYPE,
                                                      RESPONSE_INCORRECT_PAGE_TYPE,WHAT_LEARNED_PAGE_TYPE,CUSTOM_PAGE_TYPE, CREDIT_PAGE_TYPE};


    //------------RECENT FILES---------------------

    public static final String  VIGNETTESTUDIO_PATH = System.getProperty("user.home") + File.separator+ ".vignettestudio-ii";
    public static final String  RECENT_FILE_PATH = VIGNETTESTUDIO_PATH+ File.separator+"recent_files.txt";
    public static final String  FRAMEWORK_VERSION_FILE_PATH = VIGNETTESTUDIO_PATH+ File.separator+"frameworkVersion.txt";
    public static final String  NUM_RECENT_FILE_PATH = VIGNETTESTUDIO_PATH+ File.separator+"num_recent_files.txt";
//    public static final String FRAMEWORK_VERSION_FILE_PATH =  "frameworkVersion.txt";

    //-------------QUESTION TYPE------------------------
    public static final String QUESTIONTYPE_NOBRACNH ="none";
    public static final String QUESTIONTYPE_BRACNHRADIO ="radio";
    public static final String QUESTIONTYPE_BRACNHCHECKBOX ="check";

    public static int INSERT_BRANCHING_AT_INDEX = 54;


    //-------------INPUT TYPE DROPDOWN------------------------
    public static final String RADIO_INPUT_TYPE_DROPDOWN = "radio";
    public static final String CHECKBOX_INPUT_TYPE_DROPDOWN ="checkbox";
    public static final String TEXTFIELD_INPUT_TYPE_DROPDOWN = "textfield";
    public static final String TEXTAREA_INPUT_TYPE_DROPDOWN ="textarea";

    //-------------Page Settings------------------------
    public static final String OPTION_PAGE_SETTING = "options";
    public static final String PROBLEM_PAGE_SETTING ="problemStatement";
    public static final String PREV_PAGE_PAGE_SETTING = "PrevPage";
    public static final String NEXT_PAGE_PAGE_SETTING ="NextPage";

    //-------------Tabs------------------------
    public static final String PAGES_TAB_TEXT = "Pages";

    //-------------Framework------------------------
    public static final String[] PAGES_LIST_TO_BE_PRESENT  = {"question.html","q.html", "problemStatement.html", "response_correct.html", "response_incorrect.html", "problem.html", "login.html", "whatLearned.html", "completion.html"};
    public static final String QUESTION_STYLE_PATH = ReadFramework.getUnzippedFrameWorkDirectory() + "questionStyle/";
    public static final String DEFAULT_FRAMEWORK_PATH = "HTMLResources"+File.separator+"framework.zip";
    public static final String DEFAULT_FRAMEWORK_FOLDER = "HTMLResources"+File.separator+"framework";
    public static final String DEFAULT_RESOURCES = "HTMLResources";
//        /Users/ashnilvazirani/programming/vignette-studio-ii/target/classes/HTMLResources/framework

}
