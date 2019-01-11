const config = require('../config')

const getDate = function (date, cutlen = 8) {
  if (!(date instanceof Date)) {
    len = date;
    date = new Date();
  }
  var arr = [date.getMonth() + 1, date.getDate(), date.getHours(), date.getMinutes(), date.getMilliseconds()];
  for (var i = 0, len = arr.length; i < len; i++) {
    var sl = ("" + arr[i]).length;
    if (i == len - 1) {
      if (sl < 3) {
        arr[i] = "0".repeat(3 - sl) + arr[i];
      }
    } else {
      if (sl == 1) {
        arr[i] = "0" + arr[i];
      }
    }
  }
  return ('' + date.getFullYear() + arr.join('')).slice(0, cutlen);
}

const getStockDate = (date) => {
  date = date || new Date();
  if (date.getHours() < 9) {
    date.setDate(date.getDate() - 1)
  }
  let day = date.getDay()
  if (day >= 5) {
    date.setDate(date.getDate() - day + 5)
  }
  if(day == 0) {
	date.setDate(date.getDate() - 2)  
  }
  let _getDate = (date)=>{
    let gdate = getDate(date)
    if (~config.disableDays.indexOf(gdate)) {
      date.setDate(date.getDate() - 1)
      return getStockDate(date)
    }
    return gdate;
  }

  return _getDate(date)
}

const loopObj = function (obj, callback) {
  for (var i in obj) {
    callback(obj[i], i)
  }
}

function colorInfo(useColor, msg) {
  if (useColor) {
    // Make text blue and bold, so it *pops*
    return `\u001b[1m\u001b[34m${msg}\u001b[39m\u001b[22m`;
  }
  return msg;
}

function colorError(useColor, msg) {
  if (useColor) {
    // Make text red and bold, so it *pops*
    return `\u001b[1m\u001b[31m${msg}\u001b[39m\u001b[22m`;
  }
  return msg;
}

const log = function (msg) {
  console.info(colorInfo(true, msg))
}
const error = function (msg) {
  console.info(colorError(true, msg))
}

module.exports = {
  getDate,
  getStockDate,
  loopObj,
  colorInfo,
  colorError,
  log,
  error
}
