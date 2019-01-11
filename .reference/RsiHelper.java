package logic.stock;

import bean.Rsi;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Mark.W on 2017/6/5.
 * 分析
 * 1.强弱指标保持高于50表示为强势市场，反之低于50表示为弱势市场.
 * 2.强弱指标多在70与30之间波动.当六日指标上升到达80时，表示股市已有超买现象，假如一旦继续上升，超过90以上时，则表示已到严重超买的警戒区，股价已形成头部，极可能在短期内反转回转.
 * 3.当六日强弱指标下降至20时，表示股市有超卖现象，假如一旦继续下降至10以下时则表示已到严重超卖区域，股价极可能有止跌回升的机会.
 * 4.RSI值处于50左右时，市场处于整理状态，投资者可观望
 * 5.短期RSI是指参数相对小的RSI,长期RSI是指参数相对较长的RSI。
 *   比如,6日RSI和12日RSI中 ,6日RSI即为短期RSI,12日RSI即为长期RSI。长短期RSI线的交叉情况可以作为我们研判行情的方法。
 *   1.当短期RSI>长期RSI时，市场则属于多头市场；
 *   2.当短期RSI<长期RSI时，市场则属于空头市场；
 *   3.当短期RSI线在低位向上突破长期RSI线时，一般为RSI指标的“黄金交叉”，为买入信号；
 *   4.当短期RSI线在高位向下突破长期RSI线时，一般为RSI指标的“死亡交叉”，为卖出信号。
 */
public class RsiHelper {

    private static final int RSI_ACROSS_INTERVAL = 7;

    private static final String RSI_INFO_11 = "目前市场处于强势市场,";
    private static final String RSI_INFO_12 = "目前市场处于弱势市场,";

    private static final String RSI_INFO_13 = "多头市场。";
    private static final String RSI_INFO_14 = "空头市场。";

    private static final String RSI_INFO_21 = "RSI6上升到达80，表示股市已有超买现象。如一旦继续上升，超过90以上，则表示已到严重超买的警戒区，极可能在短期内反转回转。";
    private static final String RSI_INFO_22 = "RSI6下降至20，表示股市有超卖现象假。如一旦继续下降至10以下，则表示已到严重超卖区域，股价极可能有止跌回升的机会。";
    private static final String RSI_INFO_23 = "RSI值处于50上下，市场处于整理状态，投资者可观望。";

    private static final String RSI_INFO_31 = "短期RSI线在低位向上突破长期RSI线，为RSI指标的“黄金交叉”，出现买入信号";
    private static final String RSI_INFO_32 = "短期RSI线在高位向下突破长期RSI线，为RSI指标的“死亡交叉”，出现卖出信号";

    private static final String RSI_INFO = "股票数据缺失,无法获得分析数据。";

    /**
     * rsi指标的分析
     * @param rsis rsis
     * @return result
     */
    public static String anaylyseRsi(Iterator<Rsi> rsis) {
        StringBuffer result = new StringBuffer();
        ArrayList<Rsi> rsi = new ArrayList<>();

        while(rsis.hasNext()) {
            rsi.add(rsis.next());
        }

        if (rsi.size() == 0) {
            return RSI_INFO;
        }

        double nowRsi6 = rsi.get(rsi.size()-1).getRsi6();
        double nowRsi12 = rsi.get(rsi.size()-1).getRsi12();

        if(nowRsi6 > 50) {
            result.append(RSI_INFO_11);
        } else {
            result.append(RSI_INFO_12);
        }

        if(nowRsi6 > nowRsi12) {
            result.append(RSI_INFO_13);
        } else {
            result.append(RSI_INFO_14);
        }

        if (nowRsi6 > 80) {
            result.append(RSI_INFO_21);
        } else if(nowRsi6 < 20) {
            result.append(RSI_INFO_22);
        } else if(nowRsi6 <= 70 && nowRsi6 >= 30) {
            result.append(RSI_INFO_23);
        }

        if (rsi.size() < RSI_ACROSS_INTERVAL) {
            return result.toString();
        }

        for(int i=rsi.size()-1; i>=rsi.size()-RSI_ACROSS_INTERVAL; --i) {
            if (rsi.get(i).getRsi6()>rsi.get(i).getRsi12()
                    && rsi.get(i-1).getRsi6()<rsi.get(i-1).getRsi12()) {
                result.append(RSI_INFO_31);
                break;
            } else if (rsi.get(i).getRsi6()<rsi.get(i).getRsi12()
                    && rsi.get(i-1).getRsi6()>rsi.get(i-1).getRsi12()) {
                result.append(RSI_INFO_32);
                break;
            }
        }

        return result.toString();
    }

}

