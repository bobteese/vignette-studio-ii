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


    public static String NO_QUESTION= "No Question";
    public static String RADIO_QUESTION = "Multiple-Choice (Radio button)";
    public static String CHECKBOX_QUESTION="Multiple-Select (Checkbox)";


}
