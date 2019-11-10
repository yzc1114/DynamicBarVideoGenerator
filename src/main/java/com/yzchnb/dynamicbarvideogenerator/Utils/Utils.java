package com.yzchnb.dynamicbarvideogenerator.Utils;

import org.springframework.util.ResourceUtils;

public class Utils {
    public static String getDataFileDir(){
        try{
            return ResourceUtils.getURL("classpath:").getPath()+"resources/datafiles/";
        }catch (Exception e){
            return "";
        }
    }
    public static String getMoviesDir(){
        try{
            return "target/classes/resources/movies/";
        }catch (Exception e){
            return "";
        }
    }
}
