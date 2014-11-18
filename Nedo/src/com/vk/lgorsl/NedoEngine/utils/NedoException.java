package com.vk.lgorsl.NedoEngine.utils;

/**
 * исключение, бросаемое моим движком
 *
 * Created by lgor on 18.11.2014.
 */
public class NedoException extends RuntimeException{

    public NedoException(String message){
        super(message);
    }
}
