package logic.stock;

import DAO.stockInfoDAO.CollectStockDAO;
import DAO.stockInfoDAO.QuotaDAO;
import bean.StockPredict;
import logic.tools.DateHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import service.stock.CollectStockService;
import vo.stock.StockCollectInputVO;
import vo.stock.StockCurrentVO;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

/**
 * Created by Mark.W on 2017/5/15.
 */
@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CollectStockServiceImp implements CollectStockService{

    @Autowired
    private CollectStockDAO collectStockDAO;

    @Autowired
    private QuotaDAO quotaDAO;

    @Autowired
    private StockBasicInfoServiceImp stockBasicInfoServiceImp;

    @Override
    public ArrayList<StockCurrentVO> getCollectedStocks(String userID) {
        Iterator<String> codes = collectStockDAO.getCollectedStocks(userID);
        return stockBasicInfoServiceImp.getStockCurrentVOs(codes);
    }

    @Override
    public boolean collectStock(StockCollectInputVO inputVO) {
        if(collectStockDAO.addCollectedStock(inputVO.getUserID(), inputVO.getCode())) {
            return true;
        }

        return false;
    }

    @Override
    public boolean deleteCollectedStock(StockCollectInputVO inputVO) {
        if(collectStockDAO.removeCollectedStock(inputVO.getUserID(), inputVO.getCode())) {
            return true;
        }
        return false;
    }

    @Override
    public ArrayList<String> getRecommendedStock(String userID, int n) {
        ArrayList<String> result = new ArrayList<>();

        if (userID == null) {
            String date = DateHelper.getNowDate();
            //当前时间收盘
            if (DateHelper.isClosed()) {
                date = DateHelper.nextTradeDay(date);
            } else {
                if (!DateHelper.isTradeDay(date)) {
                    date = DateHelper.nextTradeDay(date);
                }
            }

            Iterator<StockPredict> iterator = quotaDAO.getAllStockPredictData(date);
            ArrayList<StockPredict> stockPredicts = new ArrayList<>();

            while(iterator.hasNext()) {
                stockPredicts.add(iterator.next());
            }

assert (stockPredicts.size() > 0) : "stockPredicts.size为0";

            for (int i=0; i<n; ++i) {
                if (stockPredicts.size() == 0) {
                    break;
                }

                int index = 0;
                for (int j=1; j<stockPredicts.size(); ++j) {
                    if (stockPredicts.get(index).getPredictIncrease() < stockPredicts.get(j).getPredictIncrease()) {
                        index = j;
                    }
                }

                result.add(stockPredicts.get(index).getCode());
                stockPredicts.remove(index);
            }
        }

        return result;
    }
}
