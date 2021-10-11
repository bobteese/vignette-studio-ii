package ConstantVariables;

public class BranchingConstants {


//    public static String NEXT_PAGE_ANSWER_NAME_TARGET  = ".*NextPageAnswerNames.*";
    public static final  String NEXT_PAGE_ANSWER_NAME_TARGET  = ".*NextPageAnswerNames([\\S\\s]*?);.*";
    public static final  String QUESTION_TYPE_TARGET  = ".*questionType([\\S\\s]*?);.*";
    public static final  String PAGE_QUESTION_ARRAY_TARGET  = ".*pageQuestions.*";
    public static final  String PLAYER_CHOICE_TARGET = ".*playerChoice.*";


//    public static String QUESTION_TYPE_TARGET = ".*questionTypeReplace.*";
    public static final  String NEXT_PAGE_NAME_TARGET = ".*NextPageName.*";

    public static final String NEXT_PAGE_ANSWER = "NextPageAnswerNames";
    public static final String PAGE_QUESTION_ARRAY = "pageQuestions";
    public static final  String PROBLEM_STATEMENT = "ProblemStatement";
    public static final  String PLAYER_CHOICE = "var playerChoice";
    public static final String QUESTION_TYPE = "questionType";
    public static final String NEXT_PAGE_NAME="NextPageName";


    public static final String SIMPLE_BRANCH = "none";
    public static final String RADIO_QUESTION = "radio";
    public static final String CHECKBOX_QUESTION="checkbox";


}
