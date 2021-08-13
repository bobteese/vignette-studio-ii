package ConstantVariables;

public class BranchingConstants {


//    public static String NEXT_PAGE_ANSWER_NAME_TARGET  = ".*NextPageAnswerNames.*";
    public static String NEXT_PAGE_ANSWER_NAME_TARGET  = ".*NextPageAnswerNames([\\S\\s]*?);.*";
    public static String PAGE_QUESTION_ARRAY_TARGET  = ".*pageQuestions.*";
    public static String PLAYER_CHOICE_TARGET = ".*playerChoice.*";


    public static String QUESTION_TYPE_TARGET = ".*questionTypeReplace.*";
    public static String NEXT_PAGE_NAME_TARGET = ".*NextPageName.*";

    public static String NEXT_PAGE_ANSWER = "NextPageAnswerNames";
    public static String PAGE_QUESTION_ARRAY = "pageQuestions";
    public static String PROBLEM_STATEMENT = "ProblemStatement";
    public static String PLAYER_CHOICE = "var playerChoice";


    public static String QUESTION_TYPE = "questionTypeReplace";
    public static String NEXT_PAGE_NAME="NextPageName";


    public static String SIMPLE_BRANCH = "none";
    public static String RADIO_QUESTION = "radio";
    public static String CHECKBOX_QUESTION="checkbox";


    public static String CHECKBOX_CORRECT_OPTION = "<correct_options>";
}
