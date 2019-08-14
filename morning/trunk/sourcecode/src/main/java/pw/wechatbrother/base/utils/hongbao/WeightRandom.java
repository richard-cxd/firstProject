package pw.wechatbrother.base.utils.hongbao;

/**
 * Created by zhengjingli on 2017/2/12.
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

//获取带权重的随机数
//面试的时候面试官问道一个这样的问题
//A、B、C三个字符分别出现的概率是30%,40%,30%
//分析：首先1-100随机产生一个数，判断这个数，1-30出现的概率是30%， 31—70出现的概率是40%， 71-100出现的概率是30%
public class WeightRandom {
    public static void main(String[] args) {
        Random ran = new Random();
        String str = getWanfei(ran.nextInt(100));
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("key", "10%");
        map.put("value", "10");
        Map<String, Object> map1 = new HashMap<String, Object>();
        map1.put("key", "20%");
        map1.put("value", "10");
        Map<String, Object> map2 = new HashMap<String, Object>();
        map2.put("key", "30%");
        map2.put("value", "10");
        Map<String, Object> map3 = new HashMap<String, Object>();
        map3.put("key", "40%");
        map3.put("value", "10");
        Map<String, Object> map4 = new HashMap<String, Object>();
        map4.put("key", "50%");
        map4.put("value", "10");
        Map<String, Object> map5 = new HashMap<String, Object>();
        map5.put("key", "60%");
        map5.put("value", "10");
        Map<String, Object> map6 = new HashMap<String, Object>();
        map6.put("key", "70%");
        map6.put("value", "10");
        Map<String, Object> map7 = new HashMap<String, Object>();
        map7.put("key", "80%");
        map7.put("value", "10");
        Map<String, Object> map8 = new HashMap<String, Object>();
        map8.put("key", "90%");
        map8.put("value", "10");
        Map<String, Object> map9 = new HashMap<String, Object>();
        map9.put("key", "100%");
        map9.put("value", "10");
        //List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        List<Map> list = new ArrayList<Map>();
        list.add(map);
        list.add(map1);
        list.add(map2);
        list.add(map3);
        list.add(map4);
        list.add(map5);
        list.add(map6);
        list.add(map7);
        list.add(map8);
        list.add(map9);
        for(int i=0;i<100;i++){
            System.out.println(getWeight1(list, ran.nextInt(100)));
        }

    }

    //知道权重的情况下
    public static String getWanfei(int num) {
        if (num >= 1 && num <= 30) {
            return "A";
        } else if (num >= 31 && num < 70) {
            return "B";
        } else {
            return "C";
        }
    }

    //如果A、B、C的个数不确定 ，权重的总数也也不确定
    public static String getWeight(List<Map<String, Object>> list, int ran) {
        //map里放的是a,b,c 值，和每个a、b、c对应的权重
        int sum = 0;
        int total = list.size();
        for (int i = 0; i < total; i++) {
            sum += Integer.parseInt(list.get(i).get("value").toString());
            if (ran <= sum) {
                return list.get(i).get("key").toString();
            }
        }
        return null;
    }

    public static String getWeight1(List<Map> list, int ran) {
        //map里放的是a,b,c 值，和每个a、b、c对应的权重
        int sum = 0;
        int total = list.size();
        for (int i = 0; i < total; i++) {
            sum += Integer.parseInt(list.get(i).get("value").toString());
            if (ran <= sum) {
                return list.get(i).get("key").toString();
            }
        }
        return null;
    }

    public static String getWeightByYN(List<Map> list, int ran) {
        //map里放的是a,b,c 值，和每个a、b、c对应的权重
        int sum = 0;
        int total = list.size();
        for (int i = 0; i < total; i++) {
            sum += Integer.parseInt(list.get(i).get("weightRandomProbability").toString());
            if (ran <= sum) {
                return list.get(i).get("weightRandomPercentage").toString();
            }
        }
        return null;
    }

}
