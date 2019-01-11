// https://gupiao.baidu.com/frontstatic/app/m/js/main.9e748c261a76bb258916.js

    var Convert = function() {
      this._data = {};
    }
    Convert.prototype = {
      get: function (t) {
        return this._data[t]
      },
      set: function (t, e) {
        this._data[t] = e
      },
      getFixedNum: function (t) {
        return null == t && (t = this.get("asset")), 0 === t || 4 === t || 17 === t ? 2 : 15 === t ? 3 : 12 === t || 13 === t ? 4 : 3
      },
      convertUnixTimestamp: function (t) {
        var e = new Date(t);
        return e.getFullYear() + "-" + ("0" + (e.getMonth() + 1)).substr(-2) + "-" + ("0" + e.getDate()).substr(-2) + " " + ("0" + e.getHours()).substr(-2) + ":" + ("0" + e.getMinutes()).substr(-2) + ":" + ("0" + e.getSeconds()).substr(-2)
      },
      convertVolume: function (t) {
        if ("number" == typeof t) {
          var e, i = this.get("exchange");
          return "us" === i || "hk" === i ? e = "股" : (e = "手", t /= 100), t < 1e4 ? t = Math.round(t) : t < 1e8 ? (t = (t / 1e4).toFixed(2), e = "万" + e) : (t = (t / 1e8).toFixed(2), e = "亿" + e), {
            value: t,
            unit: e
          }
        }
        return {
          value: "-",
          unit: "-"
        }
      },
      convertAmount: function (t) {
        if ("number" == typeof t) {
          var e = "";
          return t < 1e4 ? t = t.toFixed(2) : t < 1e8 ? (t = (t / 1e4).toFixed(2), e = "万") : (t = (t / 1e8).toFixed(2), e = "亿"), {
            value: t,
            unit: e
          }
        }
        return {
          value: "-",
          unit: "-"
        }
      },
      convertPrice: function (t, e, i) {
        if ("number" == typeof t) {
          var n = "",
            a = this.get("exchange");
          return e && (n = t > 0 ? "+" : ""), null == i && (i = this.getFixedNum(), ("hk" === a || "us" === a) && t < .5 && i < 3 && (i = 3)), n + t.toFixed(i)
        }
        return "--"
      },
      convertRatio: function (t, e, i) {
        if ("number" == typeof t) {
          var n = "";
          return e && (n = t > 0 ? "+" : ""), n + t.toFixed(i || 2) + "%"
        }
        return "--"
      },
      convertTime: function (t) {
        return t = "0" + t, t.substr(-9, 2) + ":" + t.substr(-7, 2) + ":" + t.substr(-5, 2)
      },
      convertDate: function (t) {
        return t += "", t.substr(0, 4) + "-" + t.substr(4, 2) + "-" + t.substr(6, 2)
      },
      convertRank: function (t) {
        var e;
        switch (t) {
          case 2:
            e = {
              rankName: "买入",
              statusCls: "red1"
            };
            break;
          case 1:
            e = {
              rankName: "增持",
              statusCls: "orange"
            };
            break;
          case 0:
            e = {
              rankName: "中性",
              statusCls: "purple"
            };
            break;
          case -1:
            e = {
              rankName: "减持",
              statusCls: "blue1"
            };
            break;
          case -2:
            e = {
              rankName: "卖出",
              statusCls: "green1"
            };
            break;
          default:
            e = {
              rankName: "未评级",
              statusCls: ""
            }
        }
        return e
      },
      convertStatus: function (t) {
        var e, i = this.get("asset"),
          n = this.get("exchange"),
          a = this.get("exchangeStatus"),
          r = this.get("exchangeStatusName");
        return 0 === t ? e = "停牌" : 1 === t ? e = "已退市" : 2 === t ? (e = r, 1 == a && (4 !== i || "hk" !== n && "us" !== n ? (17 === i || 0 === i && "hk" === n) && (e = "实时") : e = "延时")) : 3 === t ? e = "未上市" : 4 === t ? e = "临时熔断" : 5 === t && (e = "全天熔断"), e
      }
    }

module.exports = function(e) {
  var t = new Convert();
  t.set("asset", e.stockBasic.asset);
  t.set("exchange", e.stockBasic.exchange);
  var i = {
    code: e.stockBasic.stockCode,
    name: e.stockBasic.stockName,
    asset: Number(e.stockBasic.asset),
    // status: t.convertStatus(Number(e.stockBasic.stockStatus)),
    usestedt: e.usestedt,
    date: t.convertDate(e.date),
    time: t.convertTime(e.time),
    netChange: t.convertPrice(e.netChange, !0, 2),
    netChangeRatio: t.convertRatio(e.netChangeRatio, !0),
    preClose: t.convertPrice(e.preClose || "--"),
    open: t.convertPrice(e.open || "--"),
    close: t.convertPrice(e.close || "--"),
    high: t.convertPrice(e.high || "--"),
    low: t.convertPrice(e.low || "--"),
    limitDown: t.convertPrice(e.limitDown),
    limitUp: t.convertPrice(e.limitUp),
    volume: t.convertVolume(e.volume),
    amount: t.convertAmount(e.amount),
    turnoverRatio: t.convertRatio(e.turnoverRatio),
    amplitudeRatio: t.convertRatio(e.amplitudeRatio),
    inside: t.convertVolume(e.inside),
    outside: t.convertVolume(e.outside),
    currencyValue: t.convertAmount(e.currencyValue),
    weibiRatio: t.convertRatio(e.weibiRatio),
    volumeRatio: void 0 != e.volumeRatio ? e.volumeRatio.toFixed(2) : ""
  };
  return i;
}
