package logic.stock;

import DAO.stockInfoDAO.StockInfoDAO;
import bean.Current;
import bean.CurrentIndex;
import bean.MarketInfo;
import bean.Stock;
import logic.tools.DateHelper;
import logic.tools.MathHelper;
import logic.tools.TimeHelper;
import logic.tools.TransferHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import service.stock.MarketInfoService;
import vo.stock.CurrentIndexVO;
import vo.stock.MarketInfoVO;
import vo.stock.RealTimeLineVO;
import vo.stock.TopStockVO;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Mark.W on 2017/5/15.
 */
@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class MarketInfoServiceImp implements MarketInfoService{

    private static final int TOP_NUMBER = 15;

    @Autowired
    private StockInfoDAO stockInfoDAO;

    @Autowired
    private TransferHelper transferHelper;

    @Override
    public RealTimeLineVO getIndexRealTimeLine(String indexName) {
        String code = stockInfoDAO.getIndexCodeByName(indexName);
        if (code == null) {
            return  null;
        }

        Iterator<CurrentIndex> currentIndexs = stockInfoDAO.getCurrentIndexByCode(code);

        ArrayList<String> times = new ArrayList<>();
        ArrayList<Double> nowPrice = new ArrayList<>();
        ArrayList<Double> volumn = new ArrayList<>();

        while(currentIndexs.hasNext()) {
            CurrentIndex temp = currentIndexs.next();
            times.add(temp.getT());
            nowPrice.add(MathHelper.formatData(temp.getCloseNum(),2));
            volumn.add((double)temp.getVolume());
        }

        String time;
        if (times.size() > 0) {
            time = times.get(times.size()-1);
        } else {
            time = TimeHelper.getNowTime();
        }

        while(TimeHelper.isMarketOpen(time) != -1) {
            time = TimeHelper.nextNMinutes(time, 2);
            times.add(time);
        }

        RealTimeLineVO result = new RealTimeLineVO(code, times, nowPrice, volumn);
        return result;
    }

    @Override
    public CurrentIndexVO getIndexRealTimeInfo(String indexName) {
        String code = stockInfoDAO.getIndexCodeByName(indexName);
        if (code == null) {
            return  null;
        }
        Iterator<CurrentIndex> currentIndexs = stockInfoDAO.getCurrentIndexByCode(code);

        CurrentIndex now = null;

        while(currentIndexs.hasNext()) {
            now = currentIndexs.next();
        }

        CurrentIndexVO result = transferHelper.transToCurrentIndexVO(now);

        return result;
    }

    @Override
    public ArrayList<TopStockVO> getTopStocks(int upOrDown) {
        Iterator<Current> stocks = stockInfoDAO.getLatestCurrents();
        ArrayList<TopStock> topStocks = new ArrayList<>();
        ArrayList<TopStockVO> result = new ArrayList<>();

        while(stocks.hasNext()) {
            Current current = stocks.next();
            topStocks.add(new TopStock(current.getCode(), current.getChangepercent(), current.getTrade()));
        }

System.out.println("*********************************topstocks.size: " + topStocks.size());

        if (topStocks.size() == 0) {
            return null;
        }

        for (int i=0; i<TOP_NUMBER; ++i) {
            int topIndex = 0;

            for (int j=1; j<topStocks.size(); ++j) {
                if (upOrDown == 0) {
                    if (topStocks.get(j).getIncreaseRate() > topStocks.get(topIndex).getIncreaseRate()) {
                        topIndex = j;
                    }
                } else {
                    if (topStocks.get(j).getIncreaseRate() < topStocks.get(topIndex).getIncreaseRate()) {
                        topIndex = j;
                    }
                }
            }

            MarketInfo marketInfo = stockInfoDAO.getMarketInfo(topStocks.get(topIndex).getCode());
            if (marketInfo == null) {
                i --;
                topStocks.remove(topIndex);
                continue;
            }

System.out.println("******************************************************************************loop: " + marketInfo.getCode());

            result.add(new TopStockVO(upOrDown, marketInfo.getCode(), marketInfo.getName(),
                    topStocks.get(topIndex).getIncreaseRate(), topStocks.get(topIndex).getNowPrice()));

            topStocks.remove(topIndex);
        }

        return result;
    }

    @Override
    public ArrayList<TopStockVO> getTopIndustryStocks(int upOrDown) {
        Iterator<String> names = stockInfoDAO.getAllIndustryNames();
        ArrayList<TopStock> topStocks = new ArrayList<>();
        ArrayList<TopStockVO> result = new ArrayList<>();

        while(names.hasNext()) {
            String name = names.next();
            Iterator<String> codes = stockInfoDAO.getAllStockCodesByIndustry(name);

            String topCode = codes.next();
            if (topCode == null) {
                continue;
            }

            //初始化
            Current current = stockInfoDAO.getStockRealTimeInfo(topCode);
            if (current == null) {
                continue;
            }
            double averagerate=current.getChangepercent(), rate=averagerate;
            int num = 1;

            while (codes.hasNext()) {
                num ++;
                current = stockInfoDAO.getStockRealTimeInfo(codes.next());
                averagerate += current.getChangepercent();

                if (upOrDown == 0) {
                    if (rate < current.getChangepercent()) {
                        rate = current.getChangepercent();
                        topCode = current.getCode();
                    }
                } else {
                    if (rate > current.getChangepercent()) {
                        rate = current.getChangepercent();
                        topCode = current.getCode();
                    }
                }
            }

            averagerate /= num;

            topStocks.add(new TopStock(name, averagerate, topCode));

        }


        for (int i=0; i<TOP_NUMBER; ++i) {
            int topIndex = 0;

            for (int j=1; j<topStocks.size(); ++j) {
                if (upOrDown == 0) {
                    if (topStocks.get(j).getIncreaseRate() > topStocks.get(topIndex).getIncreaseRate()) {
                        topIndex = j;
                    }
                } else {
                    if (topStocks.get(j).getIncreaseRate() < topStocks.get(topIndex).getIncreaseRate()) {
                        topIndex = j;
                    }
                }
            }

            MarketInfo marketInfo = stockInfoDAO.getMarketInfo(topStocks.get(topIndex).getTopCode());
            if (marketInfo == null) {
                i --;
                continue;
            }

            result.add(new TopStockVO(upOrDown, topStocks.get(topIndex).getBlockName(),
                    topStocks.get(topIndex).getIncreaseRate(), marketInfo.getName(), marketInfo.getCode()));
            topStocks.remove(topIndex);
        }

        return result;
    }

    @Override
    public MarketInfoVO getRealTimeMarketInfo(String marketType) {

        double rate;
        int[] rateNums = new int[10]; //数据依次为跌停，-10%- -5%，-5%-0，0-5%，5%-10%，涨停
        for(int i=0; i<rateNums.length; ++i) {
            rateNums[i] = 0;
        }

        String date = DateHelper.getNowDate();
        String time = TimeHelper.getNowTime();
        if (TimeHelper.isMarketOpen(time) == -1) {
            return new MarketInfoVO(date, time, marketType, rateNums);
        }

        Iterator<Current> currents = stockInfoDAO.getLatestCurrents(marketType);
        while (currents.hasNext()) {
            Current temp = currents.next();
            if (temp == null) {
                continue;
            }
            rate = temp.getChangepercent();

            //ST股不同判断条件 判断指定日期板块的股票涨跌幅情况
            if (temp.getCode().contains("st") || temp.getCode().contains("ST")) {
                if (rate <= -5) {
                    rateNums[0]++;
                } else if (rate > -5 && rate <= -4) {
                    rateNums[2]++;
                } else if (rate > -4 && rate <= -2) {
                    rateNums[3]++;
                } else if (rate > -2 && rate <=0) {
                    rateNums[4]++;
                } else if (rate > 0 && rate <= 2) {
                    rateNums[5]++;
                } else if (rate > 2 && rate <= 4) {
                    rateNums[6]++;
                } else if (rate > 4 && rate < 5) {
                    rateNums[7]++;
                } else if (rate >= 5) {
                    rateNums[9]++;
                }
            } else {
                if (rate <= -10) {
                    rateNums[0]++;
                } else if (rate > -10 && rate <= -6) {
                    rateNums[1]++;
                } else if (rate > -6 && rate <= -4) {
                    rateNums[2]++;
                } else if (rate > -4 && rate <= -2) {
                    rateNums[3]++;
                } else if (rate > -2 && rate <=0) {
                    rateNums[4]++;
                } else if (rate > 0 && rate <= 2) {
                    rateNums[5]++;
                } else if (rate > 2 && rate <= 4) {
                    rateNums[6]++;
                } else if (rate > 4 && rate <= 6) {
                    rateNums[7]++;
                } else if (rate > 6 && rate < 10) {
                    rateNums[8]++;
                } else if (rate >= 10) {
                    rateNums[9]++;
                }
            }
        }

        //首先获取该板块在指定时间的基础数据
        MarketInfoVO result = new MarketInfoVO(date, TimeHelper.getNowTime(), marketType, rateNums);
        return result;
    }


//    @Override
//    public MarketInfoVO getRealTimeMarketInfo(String marketType, String date) {
//
//        boolean isRealTime = false;
//        if (DateHelper.getNowDate().equals(date)) {
//            isRealTime = true;
//        }
//
//        Iterator<String> codes = stockInfoDAO.getAllStockCodesByBlock(marketType);
//
//        double rate;
//        int[] rateNums = new int[6]; //数据依次为跌停，-10%- -5%，-5%-0，0-5%，5%-10%，涨停
//
//        while (codes.hasNext()) {
//            String code = codes.next();
//
//            //然后获取属于该板块的股票数据 计算涨跌幅股票的数量
//            if(isRealTime) {
//                Current temp = stockInfoDAO.getStockRealTimeInfo(code);
//                if (temp == null) {
//                    continue;
//                }
//                rate = temp.getChangepercent();
//            } else {
//                Stock temp = stockInfoDAO.getStockInfo(code, date);
//                if (temp == null) {
//                    continue;
//                }
//                rate = temp.getpChange();
//            }
//
//            //ST股不同判断条件 判断指定日期板块的股票涨跌幅情况
//            if (code.contains("st") || code.contains("ST")) {
//                if (rate <= -0.05) {
//                    rateNums[0]++;
//                } else if (rate >= -0.05 && rate < 0) {
//                    rateNums[2]++;
//                } else if (rate > 0 && rate <= 0.05) {
//                    rateNums[3]++;
//                } else if (rate >= 0.05) {
//                    rateNums[5]++;
//                }
//            } else {
//                if (rate <= -0.1) {
//                    rateNums[0]++;
//                } else if (rate > -0.1 && rate < -0.05) {
//                    rateNums[1]++;
//                } else if (rate >= -0.05 && rate < 0) {
//                    rateNums[2]++;
//                } else if (rate > 0 && rate <= 0.05) {
//                    rateNums[3]++;
//                } else if (rate > 0.05 && rate < 0.1) {
//                    rateNums[4]++;
//                } else if (rate >= 0.1) {
//                    rateNums[5]++;
//                }
//            }
//        }
//
//        MarketInfoVO result;
//
//        //首先获取该板块在指定时间的基础数据
//        if(isRealTime) {
//            Current current = stockInfoDAO.getStockRealTimeInfo(marketType);
//            result = new MarketInfoVO(date, TimeHelper.getNowTime(), marketType, current.getTrade(), current.getVolume(), rateNums);
//        } else {
//            Stock s = stockInfoDAO.getStockInfo(marketType, date);
//            result = new MarketInfoVO(date, marketType, s.getVolume(), rateNums);
//        }
//
//        return result;
//    }



}
