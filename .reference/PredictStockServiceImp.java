package logic.stock;

import DAO.stockInfoDAO.QuotaDAO;
import DAO.stockInfoDAO.StockInfoDAO;
import bean.*;
import logic.tools.DateHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import service.stock.PredictStockService;
import vo.stock.LineVO;
import vo.stock.StockAnalysisVO;
import vo.stock.StockInputVO;
import vo.stock.StockPredictVO;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Mark.W on 2017/5/21.
 */
@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PredictStockServiceImp implements PredictStockService {

    @Autowired
    private QuotaDAO quotaDAO;

    @Autowired
    private StockInfoDAO stockInfoDAO;

    @Override
    public StockPredictVO getStockPredictInfo(StockInputVO inputVO) {
        Iterator<StockPredict> stockPredicts = quotaDAO.getPredictData(inputVO.getCode(), inputVO.getStartDate(), inputVO.getEndDate());

        ArrayList<LineVO> actualValues = new ArrayList<>();
        ArrayList<LineVO> predictedValues = new ArrayList<>();

        StockPredict temp = null;
        while (stockPredicts.hasNext()) {
            temp = stockPredicts.next();

            //不是今天
            if (!temp.getDate().equals(DateHelper.getNowDate())) {
                predictedValues.add(new LineVO(temp.getDate(), temp.getPredictedPrice()));
                actualValues.add(new LineVO(temp.getDate(), temp.getActualPrice()));
            }
        }

        //此时temp为今天的数据
        StockPredictVO result = new StockPredictVO(inputVO.getCode(), temp.getPredictedPrice(),
                temp.getPredictIncrease(), temp.getHistoryDeviation(), actualValues, predictedValues);

        return result;
    }

    @Override
    public StockAnalysisVO getStockAnalysisInfo(String code) {
        String endDate = DateHelper.getNowDate();
        String startDate = DateHelper.formerNTradeDay(endDate, 30);

        Iterator<Macd> macds = quotaDAO.getMACDs(startDate, endDate, code);
        Iterator<Kdj> kdjs = quotaDAO.getKDJs(startDate, endDate, code);
        Iterator<Rsi> rsis = quotaDAO.getRSIs(startDate, endDate, code);
        Iterator<Boll> bolls = quotaDAO.getBOLLs(startDate, endDate, code);

        StockAnalysisVO result = new StockAnalysisVO(code, MacdHelper.anaylyseMacd(macds), KdjHelper.anaylyseKdj(kdjs),
                RsiHelper.anaylyseRsi(rsis), BollHelper.anaylyseBoll(bolls));

        return result;
    }


}
