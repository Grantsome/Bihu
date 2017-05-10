package com.grantsome.newbihu.Util;

/**
 * Created by tom on 2017/5/2.
 */

public class RefrechChecker {

    private static boolean sQuestionNeedRefresh = false;

    private static boolean sAnswerNeedRefresh = false;

    public static boolean isQuestionNeedRefresh(){
        return sQuestionNeedRefresh;
    }

    public static boolean isAnswerNeedRefresh() {
        return sAnswerNeedRefresh;
    }

    public static void setAnswerNeedRefresh(boolean answerNeedRefresh) {
        sAnswerNeedRefresh = answerNeedRefresh;
    }

    public static void setQuestionNeedRefresh(boolean questionNeedRefresh) {
        sQuestionNeedRefresh = questionNeedRefresh;
    }

}
