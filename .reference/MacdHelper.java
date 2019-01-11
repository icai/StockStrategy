package logic.stock;

import bean.Macd;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Mark.W on 2017/6/5.
 * 1、 DIF与DEA均为正值时,大势属多头市场,
 * 2、 DIF与DEA均为负值时,大势属空头市场,
 * 3、 DIF向上突破DEA时,可买进,
 * 4、 DIF向下突破DEA时,应卖出
 */
public class MacdHelper {

    private static final int MACD_ACROSS_INTERVAL = 7;

    private static final String MACD_INFO_11 = "DIFF与DEA均为正值,大势属多头市场。";
    private static final String MACD_INFO_12 = "DIFF与DEA均为负值,大势属空头市场。";

    private static final String MACD_INFO_21 = "近期DIFF由下向上穿破DEA,形成黄金交叉,为买入信号。";
    private static final String MACD_INFO_22 = "当前处于多头市场的强势之中,近期DIFF向上突破DEA,表明股价经过一段时间的高位回档整理后,新一轮涨升即将开始,激进的投资者可以短线加码买进,稳健型的投资者则继续持股待涨。";
    private static final String MACD_INFO_23 = "当前处于空头市场的弱势之中,近期DIFF向上突破DEA,表明股价经过长期下跌后,短线有反弹的可能。激进的投资者可以短线买入,快进快出;稳健型的投资者则继续持币观望。";

    private static final String MACD_INFO_31 = "近期DIFF由上向下突破DEA,形成死亡交叉,为卖出信号。";
    private static final String MACD_INFO_32 = "当前处于多头市场的强势之中,近期DIFF向下突破DEA,表明股价经过前期连续上涨,短线有调整要求。激进的投资者可以短线高抛低吸;稳健型的投资者可考虑暂时退出观望。";
    private static final String MACD_INFO_33 = "当前处于空头市场的弱势之中,近期DIFF向下突破DEA,表明股价经过短线反弹后,将开始新一轮下跌,此时应果断卖出股票。";

    private static final String MACD_INFO_4 = "近期DIFF与DEA没有交叉。";

    private static final String MACD_INFO = "股票数据缺失,无法获得分析数据。";

    /**
     * macd指标的分析
     * @param macdIterator macds
     * @return result
     */
    public static String anaylyseMacd(Iterator<Macd> macdIterator) {
        StringBuffer result = new StringBuffer();
        ArrayList<Macd> macds = new ArrayList<>();

        while(macdIterator.hasNext()) {
            macds.add(macdIterator.next());
        }

        if (macds.size() == 0) {
            return MACD_INFO;
        }

        double nowDif = macds.get(macds.size()-1).getDiff(),
                nowDea = macds.get(macds.size()-1).getDea();
        boolean isStrong = false, isWeak = false;

        if (nowDif >=0 && nowDea >=0) {
            result.append(MACD_INFO_11);
            isStrong = true;
        } else if (nowDif <= 0 && nowDea <=0) {
            result.append(MACD_INFO_12);
            isWeak = true;
        }


        if(macds.size()-MACD_ACROSS_INTERVAL < 0) {
            return MACD_INFO;
        }

        for (int i=macds.size()-1; i>=macds.size()-MACD_ACROSS_INTERVAL; --i) {
            if (macds.get(i).getDiff() > macds.get(i).getDea() && macds.get(i-1).getDiff() < macds.get(i-1).getDea()) {
                if (isStrong) {
                    result.append(MACD_INFO_22);
                } else if (isWeak) {
                    result.append(MACD_INFO_23);
                } else {
                    result.append(MACD_INFO_21);
                }
                break;
            } else if (macds.get(i).getDiff() < macds.get(i).getDea() && macds.get(i-1).getDiff() > macds.get(i-1).getDea()) {
                if (!isStrong && !isWeak) {
                    result.append(MACD_INFO_31);
                } else if (isStrong) {
                    result.append(MACD_INFO_32);
                } else if (isWeak){
                    result.append(MACD_INFO_33);
                }
                break;
            } else if (i==macds.size()-MACD_ACROSS_INTERVAL) {
                result.append(MACD_INFO_4);
            }
        }

        return result.toString();
    }

}
