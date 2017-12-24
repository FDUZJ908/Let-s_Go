package util;

import android.app.Activity;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 11437 on 2017/12/22.
 */

public class Convert {
    public static String[] gender_ = new String[]{
            "男", "女", "保密"
    };
    // 0-3
    public static String[] age_ = new String[]{
            "70后",
            "80后",
            "90后",
            "00后"
    };
    // 4-15
    public static String[] constellation_ = new String[]{
            "白羊座",
            "金牛座",
            "双子座",
            "巨蟹座",
            "狮子座",
            "处女座",
            "天秤座",
            "天蝎座",
            "射手座",
            "魔蝎座",
            "水瓶座",
            "双鱼座"
    };
    // 16-25
    public static String[] interest_ = new String[]{
            "美食",
            "文化",
            "艺术",
            "旅游",
            "时尚",
            "游戏娱乐",
            "运动",
            "科技",
            "动漫",
            "教育"
    };
    // 26-35
    public static String[] character_ = new String[]{
            "乐观",
            "忧郁",
            "外向",
            "安静",
            "自信",
            "急躁",
            "耐心",
            "机智",
            "坦率",
            "幽默"
    };

    public static String genderIntToString(int gender) {
        if (gender == 1)
            return "男";
        else if (gender == 2)
            return "女";
        else return "保密";
    }

    public static int genderStringToInt(String gender) {
        if (gender.equals("男"))
            return 1;
        if (gender.equals("女"))
            return 2;
        if (gender.equals("保密"))
            return 0;
        return -1;
    }

    public static String ageIntToString(long tags) {
        long a = 1;
        for (int i = 0; i < 4; i++) {
            if ((tags & (a << i)) > 0) {
                return age_[i];
            }
        }
        return "暂无";
    }

    public static String conIntToString(long tags) {
        long a = 1;
        for (int i = 0; i < 12; i++) {
            if ((tags & (a << (i + 4))) > 0) {
                return constellation_[i];
            }
        }
        return "暂无";
    }

    public static ArrayList<String> intIntToString(long tags) {
        long a = 1;
        ArrayList<String> re = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Log.d("the "+String.valueOf(i)+" time",String.valueOf(tags & (a << (i + 16))));
            if ((tags & (a << (i + 16))) > 0)
                re.add(interest_[i]);
        }
        return re;
    }

    public static ArrayList<String> chaIntToString(long tags) {
        long a = 1;
        ArrayList<String> re = new ArrayList<>();
        for (int i = 0; i < 10; i++)
            if ((tags & (a << (i + 26))) > 0)
                re.add(character_[i]);
        return re;
    }

    public static ArrayList<String> intIntListToString(ArrayList<Integer> s) {
        ArrayList<String> re = new ArrayList<>();
        for (int i = 0; s!=null && i < s.size(); i++) {
            re.add(interest_[s.get(i)]);
        }
        return re;
    }

    public static ArrayList<String> chaIntListToString(ArrayList<Integer> s) {
        ArrayList<String> re = new ArrayList<>();
        for (int i = 0; s!=null && i < s.size(); i++) {
            re.add(character_[s.get(i)]);
        }
        return re;
    }

    public static long generateTags(String age, String con, ArrayList<Integer> A1, ArrayList<Integer> A2) {
        long a = 1;
        long re = 0;
        for (int i = 0; i < age_.length; i++)
            if (age_[i].equals(age)) {
                re |= (a << i);
                break;
            }
        for (int i = 0; i < constellation_.length; i++)
            if (constellation_[i].equals(con)) {
                re |= (a << (i + 4));
            }
        for (int i = 0; A1 != null && i < A1.size(); i++) {
            Log.d("转换",String.valueOf(A1.get(i)+16));
            re |= (a << (A1.get(i) + 16));
        }
        for (int i = 0; A2 != null && i < A2.size(); i++) {
            Log.d("转换",String.valueOf(A2.get(i)+26));
            re |= (a << (A2.get(i) + 26));
        }
        Log.d("re***",String.valueOf(re));
        return re;
    }
}
