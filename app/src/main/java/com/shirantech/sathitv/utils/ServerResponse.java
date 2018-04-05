package com.shirantech.sathitv.utils;

/**
 * Created by prativa on 11/6/15.
 */
public class ServerResponse {

    public static final String RASHIFAL="Rashifal";
    public static final String CONSULTANT="Consultant";

    public static boolean isTokenInvalid(String message){
        if(message.equals("Invalid token"))
            return true;
        else
            return false;
    }

}
