package pw.wechatbrother.base.utils.hongbao;

import java.math.BigDecimal;
/**红包实示例1
 * Created by zhengjingli on 2017/2/12.
 * 参考来源 http://www.zuidaima.com/code/file/2214023444104192.htm?dir=/2214023444104192.java
 */
public class HongBao {
    public static void main(String args[]){
        HongBao object = new HongBao();
        //object.hb(5, 5, 0.01);//金额，个数，最少值
        object.hb(5, 5, 0.01);//金额，个数，最少值
    }
    public void hb(double total,int num,double min){
        for(int i=1;i<num;i++){
            double safe_total=(total-(num-i)*min)/(num-i);
            double money=Math.random()*(safe_total-min)+min;
            BigDecimal money_bd=new BigDecimal(money);
            money=money_bd.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
            total=total-money;
            BigDecimal total_bd=new BigDecimal(total);
            total=total_bd.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
            System.out.println("第"+i+"个红包："+money+",余额为:"+total+"元");
        }
        System.out.println("第"+num+"个红包："+total+",余额为:0元");
    }

    public void zb(){
        for(int a=0;a<=10000;a++){
            if(a % 1000== 0)
                System.out.println (a);
        }

    }

}