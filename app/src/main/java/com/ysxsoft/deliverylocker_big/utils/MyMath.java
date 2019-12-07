package com.ysxsoft.deliverylocker_big.utils;

import java.math.BigDecimal;

public class MyMath {


    /**
     *   提供精确的加法运算。
     *   @param   v1   被加数
     *   @param   v2   加数
     *   @return   两个参数的和
     */

    public   static   double   add(double   v1,double   v2){
        BigDecimal b1   =   new   BigDecimal(Double.toString(v1));
        BigDecimal b2   =   new   BigDecimal(Double.toString(v2));
        return   b1.add(b2).doubleValue();
    }

    /**
     *   提供精确的减法运算。
     *   @param   v1   被减数
     *   @param   v2   减数
     *   @return   两个参数的差
     */

    public   static   double   sub(double   v1,double   v2){
        BigDecimal   b1   =   new   BigDecimal(Double.toString(v1));
        BigDecimal   b2   =   new   BigDecimal(Double.toString(v2));
        return   b1.subtract(b2).doubleValue();
    }

    /**
     *   提供精确的乘法运算。
     *   @param   v1   被乘数
     *   @param   v2   乘数
     *   @return   两个参数的积
     */

    public   static   double   mul(double   v1,double   v2){
        BigDecimal   b1   =   new   BigDecimal(Double.toString(v1));
        BigDecimal   b2   =   new   BigDecimal(Double.toString(v2));
        return   b1.multiply(b2).doubleValue();
    }

    /**
     * 除法
     *
     * @param v1
     * @param v2
     * @param scale 精度，到小数点后几位
     * @return
     */
    public static double div(double v1, double v2, int scale) {

        if (scale < 0) {
            throw new IllegalArgumentException("The scale must be a positive integer or ");
        }
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 数字转汉字
     * @param money
     */
    public static String T_money(int money) {
        int num=0;
        String[] MoneyChinese= {"零","一","二","三","四","五","六","七","八","九"};//汉字一到九
        String[] ChineseNum= {"","拾","百","千","万","亿"};//汉字单位
        System.out.println(ChineseNum[0]);
        Integer Money= money;//转化为Integer方便转发类型
        char[] Moneynum=Money.toString().toCharArray();//转换成字符串方便转换成整形
        String[] MoneyChineseNum =new String[Moneynum.length];//用来存放转换后的整形数组
        for(int i=0;i<Moneynum.length;i++) {
            num=Moneynum[i]-48;//转换成整形
            MoneyChineseNum[i]=MoneyChinese[num];//用来映射汉子一到九
        }
        StringBuffer MoneyTime=new StringBuffer();//字符缓冲区方便添加
        int nums=0;//统计要出现的”万“
        int Numss=0;//统计要出现的”亿“
        for(int i=MoneyChineseNum.length-1;i>=0;i--) {

            if(!MoneyChineseNum[i].equals("零"))
            {
                if(!ChineseNum[nums].equals("万"))
                    MoneyTime.append(ChineseNum[nums]);
            }

            if(nums==4&&Numss==0)//添加“万”字因为万字必须出现（必能想千、百、拾，前面有零而省去）
            {
                MoneyTime.append(ChineseNum[nums]);
                nums=0;
                Numss=1;
                if(!MoneyChineseNum[i].equals("零"))
                {MoneyTime.append(MoneyChineseNum[i]);}//如果"万"字前有"零"除去万字前的 "零"
            }
            else if(nums==4&&Numss==1)//添加“亿”字因为万字必须出现（必能想千、百、拾，前面有零而省去）
            {
                MoneyTime.append(ChineseNum[nums+1]);
                nums=0;
                Numss=0;
                if(!MoneyChineseNum[i].equals("零"))
                {MoneyTime.append(MoneyChineseNum[i]);}//如果"亿"字前有"零"除去亿字前的 "零"
            }
            else {
                MoneyTime.append(MoneyChineseNum[i]);
            }
            ++nums;
        }
        return MoneyTime.toString();
    }
}
