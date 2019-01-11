# StockStrategy

>股票策略分析

策略分析不成熟，暂时不公布代码

## Feature

- [x] 获取沪深股票数据

- [x] 并输出KDJ等策略结果


## Install
```
npm install --registry=https://registry.npm.taobao.org
```

## Bootstrap

```bash
node index.js -a
node index.js -d
node index.js -l
node index.js
```

```js
if (argv[0] == '-a') {
  getStockList()
  .then(function (data) {
    getStockData(data)
    .then(function () {
      runStrategy();
    })
  })
} else if (argv.includes('-d')){
  getStockData()
  .then(function () {
    runStrategy();
  })
} else if (argv.includes('-l')) {
  getStockList()
  .then(function (data) {
  })
} else {
  runStrategy();
}

```


数据将以json文件格式存入data目录下  
策略结果将以html文件格式存入dist目录下


## Strategy

Q:如何编写策略？   
A: 参考  ./src/strategy.js 文件


## License
Copyright (c) 2018 Terry Cai. Licensed under the MIT license.
