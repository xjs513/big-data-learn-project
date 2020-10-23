package com.evente;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 生成数据倾斜的数据
 */
public class GenerateSkewData {

    public static void main(String[] args) {
        List<String> list =  new ArrayList<>(100);
        for (int i = 0; i < 90; i++) {
            list.add("北京");
        }
        list.add("河北");
        list.add("山东");
        list.add("河南");
        list.add("广西");
        list.add("广东");
        list.add("四川");
        list.add("云南");
        list.add("贵州");
        list.add("青海");

//        for (String s : list) {
//            System.out.println("s = " + s);
//        }


        Random random = new Random();

        String path = "skew_data.txt";

        File file = new File(path);

        String content;

        try (FileOutputStream fos = new FileOutputStream(file)) {
            for (int i = 0; i < 30000000; i++) {
                int a = random.nextInt(99);
                content = list.get(a) + "\t" + random.nextInt(10000) + "\n";
                System.out.println(i + ":" + content);
                // System.out.println(content);
                fos.write(content.getBytes());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }



    }

}
