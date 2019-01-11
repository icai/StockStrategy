package logic.stock;

import bean.Kdj;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Mark.W on 2017/6/5.
 *
 * 1.取值范围
 *  KDJ指标中，K值和D值的取值范围都是0—100，而J值的取值范围可以超过100和低于0，但在分析软件上KDJ的研判范围都是0—100。
 *  通常就敏感性而言，J值最强，K值次之，D值最慢，而就安全性而言，J值最差，K值次之，D值最稳。
 *
 * 2.超买超卖信号
 *  根据KDJ的取值，可将其划分为几个区域，即超买区、超卖区和徘徊区。
 *  按一般划分标准，K、D、J这三值在20以下为超卖区，是买入信号；
 *  K、D、J这三值在80以上为超买区，是卖出信号；
 *  K、D、J这三值在20—80之间为徘徊区，宜观望。
 *
 * 3.多空力量对比
 *  一般而言，当K、D、J三值在50附近时，表示多空双方力量均衡；
 *  当K、D、J三值都大于50时，表示多方力量占优；
 *  当K、D、J三值都小于50时，表示空方力量占优。
 *
 * 4.KDJ曲线的形态
 *  1、当KDJ曲线在50上方的高位时，如果KDJ曲线的走势形成M头或三重顶等顶部反转形态，可能预示着股价由强势转为弱势，股价即将大跌，应及时卖出股票。
 *      如果股价的曲线也出现同样形态则更可确认，其跌幅可以用M头或三重顶等形态理论来研判。
 *  2、当KDJ曲线在50下方的低位时，如果KDJ曲线的走势出现W底或三重底等底部反转形态，可能预示着股价由弱势转为强势，股价即将反弹向上，可以逢低少量吸纳股票。
 *      如果股价曲线也出现同样形态更可确认，其涨幅可以用W底或三重底形态理论来研判。
 *  3、KDJ曲线的形态中M头和三重顶形态的准确性要大于W底和三重底。
 *
 * 5.KDJ曲线的交叉
 * KDJ曲线的交叉分为黄金交叉和死亡交叉两种形式
 * 一般而言，在一个股票的完整的升势和跌势过程中，KDJ指标中的K、D、J线会出现两次或以上的“黄金交叉”和“死亡交叉”情况。
 * 1、K线向上穿过D线，形成金叉，为买入信号。金叉出现的位置越高，信号越强烈。
 * 2、近期K线向下穿过D线，形成死叉，为卖出信号。金叉出现的位置越低，信号越强烈。
 *
 */
public class KdjHelper {

    private static final int KDJ_ACROSS_INTERVAL = 7;

    private static final String KDJ_INFO_11 = "K、D值在20以下为超卖区，出现买入信号。";
    private static final String KDJ_INFO_12 = "K、D值在80以上为超买区，出现卖出信号。";
    private static final String KDJ_INFO_13 = "K、D值在20—80之间为徘徊区，宜观望。";

    private static final String KDJ_INFO_21 = "K、D、J三值都大于50，表示多方力量占优。";
    private static final String KDJ_INFO_22 = "K、D、J三值都小于50，表示空方力量占优。";

    private static final String KDJ_INFO_31 = "近期K线向上穿过D线，形成金叉，为买入信号。金叉出现的位置越高，信号越强烈。";
    private static final String KDJ_INFO_32 = "近期K线向下穿过D线，形成死叉，为卖出信号。死叉出现的位置越低，信号越强烈。";
    private static final String KDJ_INFO_33 = "近期K线与D线没有交叉，投资者宜观望。";

    private static final String KDJ_INFO = "股票数据缺失,无法获得分析数据。";
    /**
     * kdj指标的分析
     * @param kdjIterator kdjs
     * @return result
     */
    public static String anaylyseKdj(Iterator<Kdj> kdjIterator) {

        StringBuffer result = new StringBuffer();
        ArrayList<Kdj> kdjs = new ArrayList<>();

        while(kdjIterator.hasNext()) {
            kdjs.add(kdjIterator.next());
        }

        if (kdjs.size() == 0) {
            return KDJ_INFO;
        }

        double nowK = kdjs.get(kdjs.size()-1).getK(),
                nowD = kdjs.get(kdjs.size()-1).getD(),
                nowJ = kdjs.get(kdjs.size()-1).getJ();

        if (nowK <= 20 && nowD <= 20) {
            result.append(KDJ_INFO_11);
        } else  if (nowK >= 80 && nowD >= 80) {
            result.append(KDJ_INFO_12);
        } else {
            result.append(KDJ_INFO_13);
        }

        if (nowK <= 50 && nowD <= 50 && nowJ <= 50) {
            result.append(KDJ_INFO_22);
        } else if(nowK >= 50 && nowD >= 50 && nowJ >= 50) {
            result.append(KDJ_INFO_21);
        }

        if (kdjs.size() < KDJ_ACROSS_INTERVAL) {
            return result.toString();
        }

        for (int i=kdjs.size()-1; i>=kdjs.size()-KDJ_ACROSS_INTERVAL; --i) {
            if (kdjs.get(i).getK() > kdjs.get(i).getD()
                    && kdjs.get(i-1).getK() < kdjs.get(i-1).getD()) {
                result.append(KDJ_INFO_31);
                break;
            } else if (kdjs.get(i).getK() < kdjs.get(i).getD()
                    && kdjs.get(i-1).getK() > kdjs.get(i-1).getD()) {
                result.append(KDJ_INFO_32);
                break;
            } else if (i == kdjs.size()-6) {
                result.append(KDJ_INFO_33);
            }
        }

        return result.toString();
    }

}
