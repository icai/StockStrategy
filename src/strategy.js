const utils = require('./utils');

// {
//   "date": 20180920,
//   "kline": {
//     "open": 7.5799999237061,
//     "high": 7.6300001144409,
//     "low": 7.4800000190735,
//     "close": 7.5900001525879,
//     "volume": 11183586,
//     "amount": 84446999,
//     "ccl": null,
//     "preClose": 7.5700001716614,
//     "netChangeRatio": 0.26420053839684
//   },
//   "ma5": {
//     "volume": 17296711.8,
//     "avgPrice": 7.5740000724792,
//     "ccl": null
//   },
//   "ma10": {
//     "volume": 15443547,
//     "avgPrice": 7.3920000076294,
//     "ccl": null
//   },
//   "ma20": {
//     "volume": 14639378.7,
//     "avgPrice": 7.319000005722,
//     "ccl": null
//   },
//   "macd": {
//     "diff": 0.098713766623495,
//     "dea": 0.0555865019882,
//     "macd": 0.086254529270588
//   },
//   "kdj": {
//     "k": 71.587695082197,
//     "d": 69.257331209609,
//     "j": 76.248422827372
//   },
//   "rsi": {
//     "rsi1": 68.702520237412,
//     "rsi2": 60.402594254967,
//     "rsi3": 56.709084875231
//   }
// }

/**
 * @name changeRatio
 * @description 振幅捕捉5%
 * @param {Array} data
 */
const changeRatio = (data) => {
  let first = data[0],
    second = data[1];
  let {
    open,
    close,
    high,
    low,
    netChangeRatio
  } = first.kline;

  if (first.date == utils.getStockDate() &&
    high > open && low < open && netChangeRatio > 2 && (close - open) / open < 0.01) {
    return true
  } else {
    return false
  }
}

/**
 * @name changeRatio2
 * @description T振幅 捕捉5%
 * @param {*} data
 */
const changeRatio2 = (data)=> {
    let first = data[0],
      second = data[1];
    let {
      open,
      close,
      high,
      low,
      netChangeRatio
    } = first.kline;

    if (first.date == utils.getStockDate() &&
    high > open && low < close && netChangeRatio > 2 && (close - open) / open < 0.01) {
      return true
    } else {
      return false
    }
}


/**
 * @name macdgrow
 * @description MACD 买入时机
 * @param {Array} data
 */
const macdgrow = function (data) {
  let first = data[0],
    second = data[1];
  let {
    rsi1,
    rsi2,
    rsi3
  } = first.rsi;

  let {
    diff,
    dea,
    macd
  } = first.macd;
  let {
    diff: sdiff,
    dea: sdea,
    macd: smacd
  } = second.macd;
  let rsi = rsi1 > 40;
  let ma = Math.abs(macd) < 0.05 && macd > smacd;
  if (first.date == utils.getStockDate() && ma) {
    return true
  } else {
    return false
  }
}
/**
 * @name macdgoldsearch
 * @description macd 黄金搜索
 * @param {Array} data
 */
const macdgoldsearch = function (data) {
  let first = data[0],
    second = data[1];
  let {
    k,
    d,
    j
  } = first.kdj;
  let {
    k: sk,
    d: sd,
    j: sj
  } = second.kdj;
  let {
    rsi1,
    rsi2,
    rsi3
  } = first.rsi;
  let {
    diff,
    dea,
    macd
  } = first.macd;
  let {
    diff: sdiff,
    dea: sdea,
    macd: smacd
  } = second.macd;
  if (first.date == utils.getStockDate()) {
    let s = false, p = false;
    for (var i = 0; i < 5; i++) {
      var item = data[i];
      var p5 = item.ma5.avgPrice;
      var p10 = item.ma10.avgPrice;
      var p20 = item.ma20.avgPrice;
      if (p5 > p10 && p10 > p20 &&
        data[1].ma5.avgPrice < data[0].ma5.avgPrice &&
        data[i + 1].ma5.avgPrice < data[i].ma5.avgPrice &&
        data[i + 2].ma5.avgPrice < data[i + 1].ma5.avgPrice) {
        s = true;
        break;
      }
    }
    if (s) {
      for (var i = 0; i < 5; i++) {
        var item = data[i];
        var p5 = item.ma5.avgPrice;
        var p10 = item.ma10.avgPrice;
        var p20 = item.ma20.avgPrice;
        if (p20 > p5 || p20 > p10) {
          p = true;
          break;
        }
      }
    }
    if(p) {
      var item = data[0];
      var p5 = item.ma5.avgPrice;
      var p10 = item.ma10.avgPrice;
      var p20 = item.ma20.avgPrice;
      if (Math.abs(p20 - p5) < 0.05 ||
        Math.abs(p10 - p5) < 0.05 ||
        Math.abs(p20 - p10) < 0.05) {
        return true;
      }
    }
    return false
  } else {
    return false
  }
}


/**
 * @name kdjj30abs5
 * @description KDJ预备金叉，J小于30，RSI 40，JD差值为5
 * @param {Array} data
 */
const kdjj30abs5 = function (data) {
  let first = data[0],
    second = data[1];
  let {
    k,
    d,
    j
  } = first.kdj;
  let {
    k: sk,
    d: sd,
    j: sj
  } = second.kdj;
  let {
    rsi1,
    rsi2,
    rsi3
  } = first.rsi;
  let {
    diff,
    dea,
    macd
  } = first.macd;
  let {
    diff: sdiff,
    dea: sdea,
    macd: smacd
  } = second.macd;
  let bb = Math.abs(j - d);
  let kdj = d >= k && k >= j && (sd >= sk && sk >= sj) && j < 30 && bb < 5;
  let rsi = rsi1 > 40;
  let ma = 1 || Math.abs(macd) > 0.1 || macd > smacd;
  if (first.date == utils.getStockDate() && rsi && kdj && ma) {
    return true
  } else {
    return false
  }
}
/**
 * @name kdjj30abs5z10
 * @description KDJ预备金叉，J小于30，RSI 40，JD差值为5-10
 * @param {Array} data
 */
const kdjj30abs5z10 = function (data) {
  let first = data[0],
    second = data[1];
  let {
    k,
    d,
    j
  } = first.kdj;
  let {
    k: sk,
    d: sd,
    j: sj
  } = second.kdj;
  let {
    rsi1,
    rsi2,
    rsi3
  } = first.rsi;
  let {
    diff,
    dea,
    macd
  } = first.macd;
  let {
    diff: sdiff,
    dea: sdea,
    macd: smacd
  } = second.macd;
  let bb = Math.abs(j - d);
  let kdj = d >= k && k >= j && j < 30 && (sd >= sk && sk >= sj) && bb < 10 && bb >= 5;
  let rsi = rsi1 > 40;
  let ma = 1 && Math.abs(macd) < 0.1 || macd > smacd;
  if (first.date == utils.getStockDate() && rsi && kdj && ma) {
    return true
  } else {
    return false
  }
}

/**
 * @name kdjjg30abs5
 * @description KDJ预备金叉，J大于30，RSI 40，JD差值为5
 * @param {Array} data
 */
const kdjjg30abs5 = function (data) {
  let first = data[0],
    second = data[1];
  let {
    k,
    d,
    j
  } = first.kdj;
  let {
    k: sk,
    d: sd,
    j: sj
  } = second.kdj;
  let {
    rsi1,
    rsi2,
    rsi3
  } = first.rsi;
  let {
    diff,
    dea,
    macd
  } = first.macd;
  let {
    diff: sdiff,
    dea: sdea,
    macd: smacd
  } = second.macd;
  let bb = Math.abs(j - d);
  let kdj = d >= k && k >= j && (sd >= sk && sk >= sj) && j >= 30 && bb < 5;
  let rsi = rsi1 > 40;
  let ma = 1 && Math.abs(macd) < 0.1 || macd > smacd;
  if (first.date == utils.getStockDate() && rsi && kdj && ma) {
    return true
  } else {
    return false
  }
}

/**
 * @name kdjj00000
 * @description KDJ，J小于30，K - J大于20，RSI 40
 * @param {Array} data
 */
const kdjj00000 = function (data) {
  let first = data[0],
    second = data[1];
  let {
    k,
    d,
    j
  } = first.kdj;
  let {
    k: sk,
    d: sd,
    j: sj
  } = second.kdj;
  let {
    rsi1,
    rsi2,
    rsi3
  } = first.rsi;
  let {
    diff,
    dea,
    macd
  } = first.macd;
  let {
    diff: sdiff,
    dea: sdea,
    macd: smacd
  } = second.macd;
  let bb = Math.abs(j - d);
  let kdj = j < 30 && k - j > 20;
  let rsi = rsi1 > 40;
  let ma = 1 && Math.abs(macd) < 0.1 || macd > smacd;
  if (first.date == utils.getStockDate() && rsi && kdj && ma) {
    return true
  } else {
    return false
  }
}


/**
 * @name inkdjj30abs5
 * @description KDJ金叉， J小于30， JD差值为5
 * @param {Array} data
 */
const inkdjj30abs5 = function (data) {
  let first = data[0],
    second = data[1];
  let {
    k,
    d,
    j
  } = first.kdj;
  let {
    k: sk,
    d: sd,
    j: sj
  } = second.kdj;
  let {
    rsi1,
    rsi2,
    rsi3
  } = first.rsi;
  let {
    diff,
    dea,
    macd
  } = first.macd;
  let {
    diff: sdiff,
    dea: sdea,
    macd: smacd
  } = second.macd;
  let bb = Math.abs(j - d);
  let kdj = j >= k && k >= d && (sd > sk && sk > sj) && j < 30 && bb < 5;
  let rsi = rsi1 > 40;
  let ma = 1 || Math.abs(macd) < 0.1 || macd > smacd;
  if (first.date == utils.getStockDate() && rsi && kdj && ma) {
    return true
  } else {
    return false
  }
}
/**
 * @name inkdjjg30abs5
 * @description KDJ金叉，J大于30，JD差值为5
 * @param {Array} data
 */
const inkdjjg30abs5 = function (data) {
  let first = data[0],
    second = data[1];
  let {
    k,
    d,
    j
  } = first.kdj;
  let {
    k: sk,
    d: sd,
    j: sj
  } = second.kdj;
  let {
    rsi1,
    rsi2,
    rsi3
  } = first.rsi;
  let {
    diff,
    dea,
    macd
  } = first.macd;
  let {
    diff: sdiff,
    dea: sdea,
    macd: smacd
  } = second.macd;
  let bb = Math.abs(j - d);
  let kdj = j >= k && k >= d && (sd > sk && sk > sj) && j >= 30 && bb < 5;
  let rsi = rsi1 > 40;
  let ma = 1 || Math.abs(macd) < 0.1 || macd > smacd;
  if (first.date == utils.getStockDate() && rsi && kdj && ma) {
    return true
  } else {
    return false
  }
}

module.exports = {
  changeRatio2,
  changeRatio,
  macdgrow,
  macdgoldsearch,
  kdjj30abs5,
  kdjj30abs5z10,
  kdjjg30abs5,
  inkdjj30abs5,
  inkdjjg30abs5,
  kdjj00000
};
