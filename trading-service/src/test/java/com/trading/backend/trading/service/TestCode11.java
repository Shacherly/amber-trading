package com.google.backend.trading.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Data;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * 自己学习测试用的
 *
 * @author savion.chen
 * @date 2021/9/29 12:53
 */
public class TestCode11 {

    private void testCase11() {
        String num = "30.1415926";
        BigDecimal decNum = new BigDecimal(num);
        System.out.println("decNum=" + decNum);

        BigDecimal divNum = new BigDecimal("1.567");
        BigDecimal roundNum = decNum.divide(divNum, 5, RoundingMode.DOWN);
        System.out.println("roundNum=" + roundNum + " roundNum=" + roundNum.toString());

        String showNum = decNum.setScale(4, RoundingMode.DOWN).toString();
        System.out.println("showNum=" + showNum);
    }

    public static int getDecimalDigits(BigDecimal mantissa) {
        String tailNum = mantissa.toString();
        int index = tailNum.indexOf(".");
        return index < 0 ? 0 : tailNum.length() - index - 1;
    }

    private void testcase22() {
        BigDecimal decNum = new BigDecimal("0.0001");
        int num = getDecimalDigits(decNum);
        System.out.println("decNum=" + decNum + " num=" + num);
    }

    private void testCase33() {
        Date test = new Date(Long.parseLong("1632816145574"));
        System.out.println("now date=" + test);

        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        System.out.println("fmt date=" + dateFormat.format(test));
    }

    class Student {
        public String name;
        public BigDecimal score;
        public Student(String name, String score) {
            this.name = name;
            this.score = new BigDecimal(score);
        }
        public String toString() {
            return "name=" + name + " score=" + score;
        }
    }
    private void testcase44() {
        List<Student> testList = new ArrayList<>();
        testList.add(new Student("aaa", "86.2"));
        testList.add(new Student("ccc", "64.5"));
        testList.add(new Student("bbb", "79.4"));
        testList.add(new Student("ddd", "30.7"));

        Collections.sort(testList, new Comparator<Student>() {
            @Override
            public int compare(Student o1, Student o2) {
                if (o2.score.compareTo(o1.score) > 0) {
                    return 1;
                }else if (o2.score.compareTo(o1.score) < 0) {
                    return -1;
                }
                return 0;
            }
        });

        for(Student node : testList) {
            System.out.println("info=" + node.toString());
        }
    }

    @Data
    public class SendData {
        public String  name;
        public int     age;
        public double  score;
    }


    public void testcase55() {
        Gson gson = new GsonBuilder().create();

        SendData data = new SendData();
        data.setName("hello");
        data.setAge(18);
        data.setScore(98.5);
        String getText = gson.toJson(data);
        System.out.println("getText = " + getText);
    }

    public void testcase66() {
        String stamp = "1635091200000";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String fmt_time = sdf.format(new Date(Long.parseLong(stamp)));
        System.out.println("fmt_time = " + fmt_time);
    }

    public static void main(String[] args) {
        //System.out.println("Hello, test!");
        TestCode11 test = new TestCode11();
        //test.testCase11();
        test.testcase66();
    }

}
