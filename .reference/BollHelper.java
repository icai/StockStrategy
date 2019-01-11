package logic.stock;

import bean.Boll;
import bean.Stock;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Mark.W on 2017/6/5.
 * 1.若BOLL指标的上、中、下轨三线均向上运行,说明股价处在强势行情中,短期仍有继续上涨的动力,应持股待涨或逢低吸销。
 * 2.若BOLL指标的上、中、下轨三线均向下运行,说明股价处在弱势行情中,短期仍有下跌的趋势,应持币观望或逢高抛售。
 * 3.若BOLL指标的上轨线向下运行,而中轨和下轨两线依然向上运行,说明股价处于整理态势中，可持币观望。
 * 4.若BOLL指标的上轨线向上运行,而中轨线和下轨线同时向下运行,就不作研判。
 *
 * （1）股价由下向上穿越下轨线(LOWER)时,可视为买进信号。
 * （2）股价由下向上穿越中轨时,股价将加速上扬,是加仓买进的信号。
 * （3）股价在中轨与上轨(UPER)之间波动运行时为多头市场,可持股观望。
 * （4）股价在中轨与下轨(LOWER)之间向下波动运行时为空头市场,此时投资者应持币观望。
 * （5）当布林带上轨向上翘头，下轨向下翘头，并且中轨向上翘头，为快速上涨趋势。
 * （6）当布林带上轨向上翘头，下轨向下翘头，中轨向下运行，为加速下跌趋势。
 */
public class BollHelper {

    private static final int BOLL_ACROSS_INTERVAL = 7;

    private static final String BOLL_INFO_11 = "股价由下向上穿越下轨线,此为买进信号。";
    private static final String BOLL_INFO_12 = "股价由下向上穿越中轨时,股价将加速上扬,是加仓买进的信号。";

    private static final String BOLL_INFO_21 = "近期布林带上轨向上翘头，下轨向下翘头，并且中轨向上翘头，股票有快速上涨趋势。";
    private static final String BOLL_INFO_22 = "近期布林带上轨向上翘头，下轨向下翘头，中轨向下运行，股票有加速下跌趋势。";
    private static final String BOLL_INFO_23 = "近期布林带上轨向上翘头，下轨向下翘头，当开口不能继续放大转为收缩时,此时是卖出信号。";

    private static final String BOLL_INFO_31 = "上、中、下轨三线均向上运行,说明股价处在强势行情中,短期仍有继续上涨的动力,应持股待涨或逢低吸销。";
    private static final String BOLL_INFO_32 = "上、中、下轨三线均向下运行,说明股价处在弱势行情中,短期仍有下跌的趋势,应持币观望或逢高抛售。";
    private static final String BOLL_INFO_33 = "上轨线向下运行,而中轨和下轨两线依然向上运行,说明股价处于整理态势中，可持币观望。";
    private static final String BOLL_INFO_34 = "上轨线向上运行,而中轨线和下轨线同时向下运行,股市形式不明朗，可持币观望。";
    private static final String BOLL_INFO_44 = "股市形式不明朗，可持币观望。";

    private static final String BOLL_INFO = "股票数据缺失,无法获得分析数据。";

    /**
     * boll指标的分析
     * @param bollIterator macds
     * @return result
     */
    public static String anaylyseBoll(Iterator<Boll> bollIterator) {
        StringBuffer result = new StringBuffer();
        ArrayList<Boll> bolls = new ArrayList<>();

        while(bollIterator.hasNext()) {
            bolls.add(bollIterator.next());
        }

        if (bolls.size() == 0) {
            return BOLL_INFO;
        }

        ArrayList<Double> upper = new ArrayList<>();
        ArrayList<Double> lower = new ArrayList<>();
        ArrayList<Double> mid = new ArrayList<>();
        for (int i=15; i<bolls.size(); ++i) {
            upper.add(bolls.get(i).getUp());
            lower.add(bolls.get(i).getLow());
            mid.add(bolls.get(i).getMid());
        }
        double a1 = lineFit(upper);
        double a2 = lineFit(mid);
        double a3 = lineFit(lower);

        //开口
        if (a1 > 0 && a3 < 0 && a2 > 0) {
            result.append(BOLL_INFO_21);
        } else if (a1 > 0 && a3 < 0 && a2 < 0) {
            result.append(BOLL_INFO_22);
        } else if (a1 > 0 && a3 < 0) {
            result.append(BOLL_INFO_23);
        }

        if(a1 > 0 && a2 > 0 && a3 >0) {
            result.append(BOLL_INFO_31);
        } else if(a1 < 0 && a2 < 0 && a3 < 0) {
            result.append(BOLL_INFO_32);
        } else if(a1 < 0 && a2 > 0 && a3 > 0) {
            result.append(BOLL_INFO_33);
        } else if(a1 > 0 && a2 < 0 && a3 < 0) {
            result.append(BOLL_INFO_34);
        } else {
            result.append(BOLL_INFO_44);
        }

        return result.toString();
    }

    /**
     * 线性拟合确定数据趋势
     * @param y 数据
     * @return 1:上涨 -1:下跌
     */
    public static double lineFit(ArrayList<Double> y) {
        double averageY=0, averageX=0, averageXY=0, averageXX=0;
        for (int i=0; i<y.size(); ++i) {
            averageX += i;
            averageY += y.get(i);
            averageXY += i*y.get(i);
            averageXX += i * i;
        }

        averageX /= y.size();
        averageY /= y.size();
        averageXY /= y.size();
        averageXX /= y.size();

        double b = (averageXY-averageX*averageY)/(averageXX-averageX*averageX);
        return b;
    }
}
