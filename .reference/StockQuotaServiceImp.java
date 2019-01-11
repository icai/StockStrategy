package logic.stock;

import DAO.stockInfoDAO.QuotaDAO;
import DAO.stockInfoDAO.StockInfoDAO;
import bean.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import service.stock.StockQuotaService;
import vo.stock.LineVO;
import vo.stock.StockIndexVO;
import vo.stock.StockInputVO;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Mark.W on 2017/6/9.
 */
@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class StockQuotaServiceImp implements StockQuotaService {

    @Autowired
    private QuotaDAO quotaDAO;

    @Autowired
    private StockInfoDAO stockInfoDAO;

    @Override
    public StockIndexVO getStockIndex(StockInputVO inputVO) {
        //macd
        ArrayList<LineVO> diff = new ArrayList<>();
        ArrayList<LineVO> dea = new ArrayList<>();
        ArrayList<LineVO> macd = new ArrayList<>();
        //kdj
        ArrayList<LineVO> k = new ArrayList<>();
        ArrayList<LineVO> d = new ArrayList<>();
        ArrayList<LineVO> j = new ArrayList<>();
        //rsi
        ArrayList<LineVO> rsi6 = new ArrayList<>();
        ArrayList<LineVO> rsi12 = new ArrayList<>();
        ArrayList<LineVO> rsi24 = new ArrayList<>();
        //boll
        ArrayList<LineVO> mid = new ArrayList<>();
        ArrayList<LineVO> up = new ArrayList<>();
        ArrayList<LineVO> low = new ArrayList<>();

        Iterator<Macd> macds = quotaDAO.getMACDs(inputVO.getStartDate(), inputVO.getEndDate(), inputVO.getCode());
        Iterator<Kdj> kdjs = quotaDAO.getKDJs(inputVO.getStartDate(), inputVO.getEndDate(), inputVO.getCode());
        Iterator<Rsi> rsis = quotaDAO.getRSIs(inputVO.getStartDate(), inputVO.getEndDate(), inputVO.getCode());
        Iterator<Boll> bolls = quotaDAO.getBOLLs(inputVO.getStartDate(), inputVO.getEndDate(), inputVO.getCode());

        Macd m;
        while(macds.hasNext()) {
            m = macds.next();
            diff.add(new LineVO(m.getDate(), m.getDiff()));
            dea.add(new LineVO(m.getDate(), m.getDea()));
            macd.add(new LineVO(m.getDate(), m.getMacd()));
        }

        Kdj kdj;
        while(kdjs.hasNext()) {
            kdj = kdjs.next();
            k.add(new LineVO(kdj.getDate(), kdj.getK()));
            d.add(new LineVO(kdj.getDate(), kdj.getD()));
            j.add(new LineVO(kdj.getDate(), kdj.getJ()));
        }

        Rsi rsi;
        while(rsis.hasNext()) {
            rsi = rsis.next();
            rsi6.add(new LineVO(rsi.getDate(), rsi.getRsi6()));
            rsi12.add(new LineVO(rsi.getDate(), rsi.getRsi12()));
            rsi24.add(new LineVO(rsi.getDate(), rsi.getRsi24()));
        }

        Boll boll;
        while(bolls.hasNext()) {
            boll = bolls.next();
            mid.add(new LineVO(boll.getDate(), boll.getMid()));
            up.add(new LineVO(boll.getDate(), boll.getUp()));
            low.add(new LineVO(boll.getDate(), boll.getLow()));
        }

        String stockName;
        if(!StockHelper.isBlock(inputVO.getCode())) {
            MarketInfo market = stockInfoDAO.getMarketInfo(inputVO.getCode());
            stockName = market.getName();
        } else {
            stockName = "";
        }

        StockIndexVO result = new StockIndexVO(inputVO.getCode(), stockName, StockHelper.getBlockName(inputVO.getCode()),
                inputVO.getStartDate(), inputVO.getEndDate(), diff, dea, macd, k, d, j, rsi6, rsi12, rsi24, mid, up, low);

        return result;
    }
}
