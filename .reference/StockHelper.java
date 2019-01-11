package logic.stock;

import bean.*;
import logic.strategy.backTesting.YieldStock;
import logic.tools.MathHelper;
import vo.stock.MaVO;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Mark.W on 2017/6/3.
 */
public class StockHelper {

    /**
     * 判断代码是否是大盘
     * sh=上证指数 sz=深圳成指 zxb=中小板 cyb=创业板
     * @param code 股票代码
     */
    public static boolean isBlock(String code) {
        if(code.equals("sh") || code.equals("sz") || code.equals("zxb") || code.equals("cyb")) {
            return true;
        }
        return false;
    }

    public static String getBlockName(String code) {
        String result = null;

        if (code.startsWith("300")) { //cyb
            result = "创业板";
        } else if (code.startsWith("600") || code.startsWith("601") || code.startsWith("603") ) {  //sh
            result = "上证";
        } else if (code.startsWith("000")) { //sz
            result = "深证";
        } else if (code.startsWith("002")){ //zxb
            result = "中小板";
        }
        return result;
    }

    /**
     * 均线图是将每天的收盘价加权平均,从而得到一条带有趋势性的轨迹。
     * @param sourceStocks 股票信息
     * @param period 均线周期
     * @return ArrayList<MaVO>
     */
    public static ArrayList<MaVO> calculateMA(Iterator<Stock> sourceStocks, int period) {
        //如果总天数小于均线图的时间间隔出错
        ArrayList<Stock> stocks = new ArrayList<>();
        while(sourceStocks.hasNext()) {
            stocks.add(sourceStocks.next());
        }

        if(period >= stocks.size()) {
            return null;
        }

        ArrayList<MaVO> result = new ArrayList<>();
        Stock stock;

        for (int i = period-1; i<stocks.size(); ++i){
            stock = stocks.get(i);

            double all = 0;
            for(int j=i-period+1; j<i+1; ++j) {
                all += stocks.get(j).getClose();
            }

            double average = MathHelper.formatData(all/(double)period, 4);

            result.add(new MaVO(stock.getCode(),period, stock.getDate(), average));
        }

        return result;
    }
}
