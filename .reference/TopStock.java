package logic.stock;

/**
 * Created by Mark.W on 2017/6/3.
 * 用于排序的股票对象
 */
public class TopStock {
    private String code;
    private String blockName;

    private double increaseRate;
    private double nowPrice;

    private String topCode;

    /**
     * 用于行业计算涨跌幅榜
     * @param blockName 行业名称
     * @param increaseRate 涨跌幅
     * @param topCode 领涨股或者领跌股
     */
    public TopStock(String blockName, double increaseRate, String topCode) {
        this.blockName = blockName;
        this.increaseRate = increaseRate;
        this.topCode = topCode;
    }

    /**
     * 个股涨跌幅榜
     * @param code 股票代码
     * @param increaseRate 涨跌幅
     * @param nowPrice 现价
     */
    public TopStock(String code, double increaseRate, double nowPrice) {
        this.code = code;
        this.increaseRate = increaseRate;
        this.nowPrice = nowPrice;
    }

    public String getBlockName() {
        return blockName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public double getIncreaseRate() {
        return increaseRate;
    }

    public double getNowPrice() {
        return nowPrice;
    }

    public String getTopCode() {
        return topCode;
    }

}
