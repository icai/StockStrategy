const cheerio = require('cheerio');
const request = require('superagent');

const fs = require('fs');
const async = require('async');
const comments = require('js-comments');
const strategy = require('./src/strategy');
const file = require('./src/file');
const utils = require('./src/utils');
const log = utils.log;
const error = utils.error;
require('superagent-charset')(request);

let commentsStrs = fs.readFileSync('./src/strategy.js', 'utf8');
let comms = comments.parse(commentsStrs, {});
const getCommentDescByName = (name) => {
  for (let i = 0; i < comms.length; i++) {
    const item = comms[i];
    if (item.name == name) {
      return item.description
    }
  }
  return name;
}
const tmpl = require('./src/template');

//let errStocks = [];

// https://gupiao.baidu.com/api/stocks/stockbets?from=h5
// &os_ver=0&cuid=xxx&vv=2.2&format=json&stock_code=sh600200

// Mozilla/5.0 (iPhone; CPU iPhone OS 11_0 like Mac OS X) AppleWebKit/604.1.38 (KHTML, like Gecko) Version/11.0 Mobile/15A372 Safari/604.1
/**

  "preClose": 昨收,
  "high": 最高,
  "open": 今开,
  "low": 最低,
  "close": 今收,
  "volume": 成交量,
  "nowVol": 0,
  "amount": 成交额,
  "netChange": 涨跌价,
  "netChangeRatio": 涨跌幅,
  "amplitudeRatio": 振幅,
  "turnoverRatio": 量比,
  "turnoverRatio": 换手率,
  "inside": 内盘,
  "outside": 外盘,
  "currencyValue": 流通市值,
  "capitalization": 总市值,
  "peratio": 市盈率MRQ,
  "volumeRatio": 量比,
  "weibiRatio": 委比,
  "perShareEarn": 每股收益,
  "usestedt": "EDT",
  "LYRPeratio": LYR市盈率,
  "TTMPeratio": TTM市盈率,
  "netFundsFlow": 18265644,
  "limitUp": 涨停,
  "limitDown": 跌停,
  "netAssetsPerShare": 每股净资产,
  "totalShareCapital": 总股本,
  "circulatingCapital": 流通股

 * @param {*} symbol
 * @param {*} callback
 */
const fetchSnapShot = function (symbol, callback) {
  var targetUrl = 'https://gupiao.baidu.com/api/stocks/stockbets'
  // &os_ver=0&cuid=xxx&vv=2.2&format=json&stock_code=sh600200
  request.get(targetUrl)
    .query({
      from: 'h5',
      os_ver: 0,
      cuid: 'xxx',
      vv: '2.2',
      format: 'json',
      stock_code: symbol,
      timestamp: Date.now
    })
    .end(function (err, res) {
      if (err) {
        error(symbol + (err && err.message))
        callback(null, null)
        return;
      }
      if (res) {
        var json = JSON.parse(res.text)
        if (json) {
          if (json.errorMsg == 'SUCCESS' && json.snapShot) {
            var resultBuffer = JSON.stringify(json.snapShot);
            //将数据写入文件，放入当前目录下的Data文件夹中
            var filePath = `./data/snapshot/${symbol}.json`;
            file.writeFile(filePath, resultBuffer, function (err) {
              if (err) throw err;
              log(symbol + ' write Success');
              callback(null, null);
            });
          } else {
            error(symbol + ' no Data');
            callback(null, null);
          }
        } else { //该代码对应的股票不存在，数据为空
          error(symbol + ' ' + json.errorMsg);
          callback(null, null);
        }
      } else { //对没有返回正确格式的文档做容错处理
        error(symbol + ' no res...');
        callback(null, null);
      }
    });
}

const fetchStock = function (symbol, date, callback) {
  // https://gupiao.baidu.com/api/stocks/stockdaybar?from=pc
  // &os_ver=1&cuid=xxx&vv=100&format=json&stock_code=sh600581&step=3&start=
  // &count=160&fq_type=no&timestamp=1521378067379
  var targetUrl = 'https://gupiao.baidu.com/api/stocks/stockdaybar'
  request.get(targetUrl)
    .query({
      from: 'pc',
      os_ver: 1,
      cuid: 'xxx',
      vv: 100,
      format: 'json',
      stock_code: symbol,
      step: 3,
      start: '',
      count: 60,
      fq_type: 'no',
      timestamp: Date.now
    })
    .end(function (err, res) {
      if (err) {
        error(symbol + (err && err.message))
        callback(null, null)
        return;
      }
      if (res) {
        var json = JSON.parse(res.text)
        if (json) {
          if (json.errorMsg == 'SUCCESS' && json.mashData) {
            var resultBuffer = JSON.stringify(json.mashData);
            //将数据写入文件，放入当前目录下的Data文件夹中
            var filePath = `./data/${date}/${symbol}.json`;
            file.writeFile(filePath, resultBuffer, function (err) {
              if (err) throw err;
              log(symbol + ' write Success');
              callback(null, null);
            });
          } else {
            error(symbol + ' no Data');
            callback(null, null);
          }
        } else { //该代码对应的股票不存在，数据为空
          error(symbol + ' ' + json.errorMsg);
          callback(null, null);
        }
      } else { //对没有返回正确格式的文档做容错处理
        error(symbol + ' no res...');
        callback(null, null);
      }
    });
}


const getStockList = function () {
  let regexp = new RegExp("\\(((0|6|3|9|4|8)\\d+)\\)$")
  let ignoredStocks = ["600296", "600349", "031005", "031007", "038011", "038014", "038015",
    "038016", "038017", "000508", "000991", "031007", "600991", "601299", "002720", "002710"
  ];
  let targetUrl = 'http://quote.eastmoney.com/stocklist.html';
  return new Promise(function (resolve, reject) {
    request.get(targetUrl)
      .charset()
      .end(function (err, res) {
        if (res) {
          var $ = cheerio.load(res.text);
          // #quotesearch > ul > li > a
          let lists = $("ul li a", "#quotesearch");
          // log(as)
          let keys = Object.keys(lists)
          let datas = {};
          keys.forEach(k => {
            if (isNaN(Number(k))) {
              return false;
            }
            let str = $(lists[k]).text();
            let href = $(lists[k]).attr("href");
            let rangs = regexp.exec(str);
            if (rangs) {
              let stock = {};
              let stockCode = href.replace("http://quote.eastmoney.com/", "").replace(".html", "");
              stock.code = rangs[1];
              stock.codeA = stockCode;
              stock.name = str.replace(rangs[0], "").replace("*", "");
              if (ignoredStocks.includes(stock.code)) { //已经退市或者不存在
                return false;
              }
              datas[stockCode] = stock;
            }
          })
          file.writeFile("./data/stockIndex.json", JSON.stringify(datas), function (err) {
            if (err) throw err;
            resolve(datas);
            log('stockIndex' + ' write Success');
            log('-'.repeat(20));
          })
        }
      })
  })
}

const generateStockTask = function (data, loopcall, end) {
  const stocklist = data || require('./data/stockIndex.json')
  const date = utils.getStockDate()
  var tasks = {};
  codes = [
    [600000, 602000],
    [603000, 604000],
    [0, 1000],
    [2000, 3000],
    [300000, 300750]
  ]; //股票代码范围
  for (var c = 0; c < codes.length; c++) {
    for (var i = codes[c][0]; i < codes[c][1]; i++) {
      (function (k) {
        //对股票代码做处理，使其满足新浪财经接口的字段参数格式
        var k_str = k.toString();
        if (k_str.length < 6) {
          var zeros = "";
          for (var m = 0; m < 6 - k_str.length; m++) {
            zeros += "0";
          }
          k_str = zeros + k_str;
        }
        var market = "sh";
        if (k_str.substring(0, 1) == "6") {
          market = "sh";
        } else {
          market = "sz";
        }
        let symbol = market + k_str;
        if (stocklist[symbol]) {
          tasks[symbol] = function (callback) {
            loopcall(symbol, date, callback)
          }
        }
      })(i);
    }
  }
  return new Promise(function (resolve, reject) {
    //并发抓取数据，控制下并发数，这里设为10
    async.parallelLimit(tasks, 3, function (err, result) {
      if (err) {
        log(err);
        reject();
      }
      resolve();
      end && end();
    });
  });
}

const getStockData = function (data) {
  return generateStockTask(data, function (symbol, date, callback) {
    fetchStock(symbol, date, callback);
  }, function() {
    log("All fetchStock files are writen~");
    log('-'.repeat(20));
  })
}

const getSnapShotData = function (data) {
  return generateStockTask(data, function (symbol, date, callback) {
    fetchSnapShot(symbol, callback);
  }, function () {
    log("All Snap Shot files are writen~");
    log('-'.repeat(20));
  })
}


const margeData = (item, data) => {

}

const reIndex = () => {
  const glob = require("glob");
  let indexList = [];
  glob.sync('./dist/report/*.html').forEach(path => {
    let str = fs.readFileSync(path, 'utf8');
    let matchs = str.match(/<(title)>([\w\W]+?)<\/\1>/)
    if (matchs) {
      indexList.push({
        title: matchs[2],
        url: path.split('./dist/')[1]
      })
    }
  })
  indexList = indexList.sort((a, b) => {
    return (parseInt(b.title) - parseInt(a.title));
  })

  file.writeFile(`./dist/index.html`, tmpl.htmlIndexTemplate(indexList), function (err) {
    if (err) throw err;
    log(`reIndexed`);
  })
}

const runStrategy = function (stocklist) {
  stocklist = stocklist || require('./data/stockIndex.json')
  let date = utils.getStockDate();
  let tasks = {};
  let str = '';
  let i = 0;
  utils.loopObj(strategy, function (strat, fnName) {
    tasks[fnName] = function (callback) {
      let results = [];
      utils.loopObj(stocklist, function (item, name) {
        try {
          let filePath = `./data/${date}/${name}.json`;
          let data = require(filePath)
          if (strat(data)) {
            // margeData(item,data)
            results.push(item)
          }
        } catch (e) {
          i++;
        }
      })
      str += tmpl.htmlBlockTemplate(results, getCommentDescByName(fnName));
      callback()
    }
  })
  async.parallel(tasks, function (err, results) {
    if (err) {
      log(err);
    }
    file.writeFile(`./dist/report/${date}.html`, tmpl.htmlTemplate(str, date), function (err) {
      if (err) throw err;
      log('no find count: ' + i)
      log('-'.repeat(10));
      log(`${date} All strategy are writen`);
      reIndex();
    })
  })
}

let argv = process.argv.slice(2);
if (argv[0] == '-a') {
  getStockList()
    .then((data) => {
      getStockData(data)
        .then(() => {
          getSnapShotData(data)
            .then(() => {
              runStrategy();
            })
        })
    })
} else if (argv.includes('-d')) {
  getStockData()
    .then(function () {
      runStrategy();
    })
} else if (argv.includes('-l')) {
  getStockList()
    .then(function (data) {})
} else {
  // fetchSnapShot('sh600200');
  runStrategy();
}
