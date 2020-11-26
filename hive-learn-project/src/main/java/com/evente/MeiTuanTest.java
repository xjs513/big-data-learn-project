package com.evente;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author : Kasa
 * @date : 2020/11/23 17:02
 * @descripthon :
 */
public class MeiTuanTest {


    public static void main(String[] args) {
//        String str = "AmeituanBv5.0CbaiduDandroid";
//         String str = "Bv5.0CbaiduDandroid";
//        String str = "AmeituanCbaiduDandroid";
//        String str = "AmeituanBv5.0Dandroid";
        String str = "AmeituanBv5.0Cbaidu";


        int maxIndex = str.length();

        int aIndex = !str.contains("A") ? maxIndex:str.indexOf("A");
        int bIndex = !str.contains("B") ? maxIndex:str.indexOf("B");
        int cIndex = !str.contains("C") ? maxIndex:str.indexOf("C");
        int dIndex = !str.contains("D") ? maxIndex:str.indexOf("D");

        System.out.println("aIndex = " + aIndex);
        System.out.println("bIndex = " + bIndex);
        System.out.println("cIndex = " + cIndex);
        System.out.println("dIndex = " + dIndex);

        String app = null;
        String version = null;
        String channel = null;
        String os = null;

        if (aIndex != maxIndex){
            int endIndex = Collections.min(new ArrayList<Integer>(){
                {
                    add(bIndex);
                    add(cIndex);
                    add(dIndex);
                }
            });
            app = str.substring(aIndex + 1, endIndex);
        }

        if (bIndex != maxIndex){
            int endIndex = Collections.min(new ArrayList<Integer>(){
                {
                    add(cIndex);
                    add(dIndex);
                }
            });
            version = str.substring(bIndex + 1, endIndex);
        }

        if (cIndex != maxIndex){
            channel = str.substring(cIndex + 1, dIndex);
        }

        if (dIndex != maxIndex){
            os = str.substring(dIndex + 1);
        }

        System.out.println("-----------------------");
        System.out.println("app = " + app);
        System.out.println("version = " + version);
        System.out.println("channel = " + channel);
        System.out.println("os = " + os);

    }
}
