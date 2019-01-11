package logic.stock;

import DAO.stockInfoDAO.StockInfoDAO;
import bean.*;
import logic.tools.MathHelper;
import logic.tools.TimeHelper;
import logic.tools.TransferHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import service.stock.StockBasicInfoService;
import vo.stock.*;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Mark.W on 2017/5/15.
 */
@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class StockBasicInfoServiceImp implements StockBasicInfoService {

    @Autowired
    private StockInfoDAO stockInfoDAO;

    @Autowired
    private TransferHelper transferHelper;

    @Override
    public RealTimeLineVO getStockRealTimeLineInfo(String code) {
        Iterator<Current> stocks = stockInfoDAO.getStockRealTimeList(code);

        ArrayList<String> times = new ArrayList<>();
        ArrayList<Double> nowPrice = new ArrayList<>();
        ArrayList<Double> volumn = new ArrayList<>();

        while(stocks.hasNext()) {
            Current temp = stocks.next();
            int timeFlag = TimeHelper.isMarketOpen(temp.getTime());
            if (timeFlag !=0 && timeFlag != 1) {
                continue;
            }
            times.add(temp.getTime());
            nowPrice.add(MathHelper.formatData(temp.getTrade(),2));
            volumn.add((double)temp.getVolume());
        }

        String time;
        if (times.size() > 0) {
            time = times.get(times.size()-1);
        } else {
            time = TimeHelper.getNowTime();
        }

        int flag = TimeHelper.isMarketOpen(time);
        if (flag == 0) {
            while(TimeHelper.isMarketOpen(time) == 0) {
                time = TimeHelper.nextNMinutes(time, 2);
                if (TimeHelper.isMarketOpen(time) != 0) {
                    break;
                }
                times.add(time);
            }

            time = "13:00";
            while(TimeHelper.isMarketOpen(time) == 1) {
                times.add(time);
                time = TimeHelper.nextNMinutes(time, 2);
            }
        } else if (flag == 1) {
            while(TimeHelper.isMarketOpen(time) == 1) {
                time = TimeHelper.nextNMinutes(time, 2);
                if (TimeHelper.isMarketOpen(time) != 1) {
                    break;
                }
                times.add(time);
            }
        } else if (flag == 2) {
            time = "13:00";
            while(TimeHelper.isMarketOpen(time) == 1) {
                times.add(time);
                time = TimeHelper.nextNMinutes(time, 2);
            }
        }

        RealTimeLineVO result = new RealTimeLineVO(code, times, nowPrice, volumn);
        return result;
    }

    @Override
    public StockCurrentVO getStockRealTimeInfo(String code) {
        MarketInfo marketInfo = stockInfoDAO.getMarketInfo(code);
        Current current = stockInfoDAO.getStockRealTimeInfo(code);

        return transferHelper.transToStockCurrent(marketInfo, current);
    }

    @Override
    public ArrayList<StockCurrentVO> getAllStockRealTime() {
        Iterator<String> codes = stockInfoDAO.getAllStockCodes();
        return this.getStockCurrentVOs(codes);
    }

    @Override
    public ArrayList<StockCurrentVO> getStocksByIndustry(String industryName) {
        Iterator<String> codes = stockInfoDAO.getAllStockCodesByIndustry(industryName);
        return this.getStockCurrentVOs(codes);
    }

    @Override
    public String getCodeByName(String name) {
        return stockInfoDAO.getCodeByName(name);
    }

    @Override
    public String getNameByCode(String code) {
        if (code == null) {
            return null;
        }
        MarketInfo marketInfo = stockInfoDAO.getMarketInfo(code);
        if (marketInfo == null) {
            return null;
        }
        return marketInfo.getName();
    }


    @Override
    public ArrayList<String> getAllStockCodeAndNames() {
       Iterator<MarketInfo> marketInfos = stockInfoDAO.getAllStockAndNames();
       ArrayList<String> result = new ArrayList<>();
       while(marketInfos.hasNext()) {
           MarketInfo temp = marketInfos.next();
           result.add(temp.getCode() + " " + temp.getName());
       }

       return result;
    }

    /**
     * 根据代码列表获取数据 用来方法服用
     * @param codes 代码
     * @return ArrayList<StockCurrentVO>
     */
    public ArrayList<StockCurrentVO> getStockCurrentVOs(Iterator<String> codes) {

        ArrayList<StockCurrentVO> result = new ArrayList<>();

        while(codes.hasNext()) {
            String code = codes.next();
            if(code != null && !code.equals("")) {
                StockCurrentVO temp = getStockRealTimeInfo(code);
                if (temp != null) {
                    result.add(temp);
                }
            }
        }

        if(result.size() == 0) {
            return null;
        }
        return result;
    }


    @Override
    public StockHistoricalVO getStockHistoricalInfo(StockInputVO inputVO) {
        assert inputVO != null : "StockBasicInfoServiceImp.getStockHistoricalInfo.inputvo为null";
        //日k
        ArrayList<KLineVO> kLine = new ArrayList<>();
        //成交量
        ArrayList<LineVO> volume = new ArrayList<>();
        //均线图
        ArrayList<LineVO> ma5 = new ArrayList<>();
        ArrayList<LineVO> ma10 = new ArrayList<>();
        ArrayList<LineVO> ma20 = new ArrayList<>();
        //对数收益
        ArrayList<LineVO> logarithmYields = new ArrayList<>();

        //判断周k还是日k
        if(inputVO.getType().equals("w")) {

            Iterator<StockWeek> stockWeeks = stockInfoDAO.getWeekK(inputVO.getCode(), inputVO.getStartDate(), inputVO.getEndDate());
            StockWeek formerWeek, stockWeek=null;
            String date;
            while(stockWeeks.hasNext()) {
                formerWeek = stockWeek;
                stockWeek = stockWeeks.next();

                if (stockWeek != null) {
                    date = stockWeek.getDate();

                    kLine.add(new KLineVO(date, stockWeek.getOpen(), stockWeek.getClose(), stockWeek.getLow(), stockWeek.getHigh()));
                    volume.add(new LineVO(date, MathHelper.formatData(stockWeek.getVolume() / 10000, 2)));
                    ma5.add(new LineVO(date, stockWeek.getMa5()));
                    ma10.add(new LineVO(date, stockWeek.getMa10()));
                    ma20.add(new LineVO(date, stockWeek.getMa20()));

                    //对数收益率
                    if (formerWeek != null) {
                        double logarithmYield = Math.log(stockWeek.getClose() / formerWeek.getClose());
                        logarithmYields.add(new LineVO(date, logarithmYield));
                    }
                }
            }

        } else if (inputVO.getType().equals("m")) {

            Iterator<StockMonth> stockMonths = stockInfoDAO.getMonthK(inputVO.getCode(), inputVO.getStartDate(), inputVO.getEndDate());
            StockMonth formerMonth, stockMonth=null;
            String date;
            while(stockMonths.hasNext()) {
                formerMonth = stockMonth;
                stockMonth = stockMonths.next();

                if(stockMonth == null) {
                    continue;
                }

                date = stockMonth.getDate();

                kLine.add(new KLineVO(date, stockMonth.getOpen(), stockMonth.getClose(), stockMonth.getLow(), stockMonth.getHigh()));
                volume.add(new LineVO(date, MathHelper.formatData(stockMonth.getVolume()/10000,2)));
                ma5.add(new LineVO(date, stockMonth.getMa5()));
                ma10.add(new LineVO(date, stockMonth.getMa10()));
                ma20.add(new LineVO(date, stockMonth.getMa20()));

                //对数收益率
                if(formerMonth != null) {
                    double logarithmYield = Math.log(stockMonth.getClose() / formerMonth.getClose());
                    logarithmYields.add(new LineVO(date, logarithmYield));
                }
            }

        } else if (inputVO.getType().equals("d")) {
            Iterator<Stock> stocks = stockInfoDAO.getStockInfo(inputVO.getCode(), inputVO.getStartDate(), inputVO.getEndDate());
            Stock stock = null, formerStock;
            String date;

            while(stocks.hasNext()) {
                formerStock = stock;
                stock = stocks.next();

                if(stock == null) {
                    continue;
                }

                date = stock.getDate();

                kLine.add(new KLineVO(date, stock.getOpen(), stock.getClose(), stock.getLow(), stock.getHigh()));
                volume.add(new LineVO(date, MathHelper.formatData(stock.getVolume()/10000,2)));
                ma5.add(new LineVO(date, stock.getMa5()));
                ma10.add(new LineVO(date, stock.getMa10()));
                ma20.add(new LineVO(date, stock.getMa20()));

                //对数收益率
                if(formerStock != null) {
                    double logarithmYield = Math.log(stock.getClose() / formerStock.getClose());
                    logarithmYields.add(new LineVO(date, logarithmYield));
                }
            }
        }

        String stockName;
        if(!StockHelper.isBlock(inputVO.getCode())) {
            MarketInfo market = stockInfoDAO.getMarketInfo(inputVO.getCode());
            stockName = market.getName();
        } else {
            stockName = "";
        }

        StockHistoricalVO result = new StockHistoricalVO(inputVO.getCode(), stockName, StockHelper.getBlockName(inputVO.getCode()),
                inputVO.getStartDate(), inputVO.getEndDate(), kLine, volume, ma5, ma10, ma20, logarithmYields);

        return result;
    }



}
