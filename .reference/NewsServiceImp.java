package logic.stock;

import DAO.NewsDAO.NewsDAO;
import bean.News;
import logic.tools.TransferHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import service.stock.NewsService;
import vo.stock.NewsVO;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Mark.W on 2017/5/15.
 */
@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class NewsServiceImp implements NewsService {

    @Autowired
    private NewsDAO newsDAO;
    @Autowired
    private TransferHelper transferHelper;

    @Override
    public ArrayList<NewsVO> getNews() {
        Iterator<News> news = newsDAO.getNews();

        ArrayList<NewsVO> newsVOS = new ArrayList<>();

        while (news.hasNext()) {
            newsVOS.add(transferHelper.transToNewsVO(news.next()));
        }

        if (newsVOS.size() == 0) {
            return null;
        }

        return newsVOS;
    }
}
