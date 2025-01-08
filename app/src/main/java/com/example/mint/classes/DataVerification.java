package com.example.mint.classes;

public class DataVerification {
    public static boolean pinCode(String pinCode){
        if (pinCode.length() != 5)
            return false;

        if (!pinCode.matches("\\d+"))
            return false;

        return true;
    }

    public static boolean sum(String sum) {
        String regex = "^[0-9]+(\\.[0-9]{1,2})?$";
        return sum.matches(regex);
    }
}
