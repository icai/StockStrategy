const getFooter = ()=>{
  return `<div class="footer">
    <p>股市有风险，投资需谨慎！</p>
    <p>投资者依此做出的投资行为风险自担！</p>
  </div>`
}


const htmlIndexTemplate = function (results) {
  let str = `<!DOCTYPE html>
    <html>
    <head>
    <title>股市策略分析报告</title>
    <style type="text/css" media="screen">
    body {
        color: #333;
        font-size: 12px;
        font-family:"Microsoft YaHei",Arial,sans-serif;
        padding: 0;
        margin: 0
    }
    a {
        color: #707070;
        text-decoration: none
    }
    a:hover {
        text-decoration: underline;
        color: #2d64b3
    }
    ul,li {
        list-style: none;
        padding: 0;
        margin: 0
    }
    .index-list::after {
        display: block;
        visibility: hidden;
        height: 0;
        clear: both;
        content: " "
    }
    .index-list li {
        padding-bottom: 10px;
        margin-bottom: 10px;
        border-bottom: 1px solid #e5e4e9;
    }

    h3 {
        color: #1a79ff;
        font-size: 18px;
        margin: 0;
    }

    .text-center{
      text-align:center;
    }
    .wrap {
      width: 1002px;
      margin: 0 auto;
    }

    .footer {
        display: block;
        float: left;
        width: 100%;
        padding-top: 30px
    }

    .footer p {
        text-align: center
    }
    .github-corner {
        border-bottom: 0;
        position: fixed;
        right: 0;
        text-decoration: none;
        top: 0;
        z-index: 1
    }

    .github-corner:hover .octo-arm {
        animation: a .56s ease-in-out
    }

    .github-corner svg {
        color: #fff;
        fill: var(--theme-color,#f00);
        height: 80px;
        width: 80px;
    }
    @keyframes a {
        0%,to {
            transform: rotate(0)
        }

        20%,60% {
            transform: rotate(-25deg)
        }

        40%,80% {
            transform: rotate(10deg)
        }
    }
    </style>
    </head>
    <body>
    <a href="https://github.com/icai/stockstrategy" class="github-corner" aria-label="View source on Github">
      <svg viewBox="0 0 250 250" aria-hidden="true">
        <path d="M0,0 L115,115 L130,115 L142,142 L250,250 L250,0 Z"></path>
        <path d="M128.3,109.0 C113.8,99.7 119.0,89.6 119.0,89.6 C122.0,82.7 120.5,78.6 120.5,78.6 C119.2,72.0 123.4,76.3 123.4,76.3 C127.3,80.9 125.5,87.3 125.5,87.3 C122.9,97.6 130.6,101.9 134.4,103.2"
          fill="currentColor" style="transform-origin: 130px 106px;" class="octo-arm"></path>
        <path d="M115.0,115.0 C114.9,115.1 118.7,116.5 119.8,115.4 L133.7,101.6 C136.9,99.2 139.9,98.4 142.2,98.6 C133.8,88.0 127.5,74.4 143.8,58.0 C148.5,53.4 154.0,51.2 159.7,51.0 C160.3,49.4 163.2,43.6 171.4,40.1 C171.4,40.1 176.1,42.5 178.8,56.2 C183.1,58.6 187.2,61.8 190.9,65.4 C194.5,69.0 197.7,73.2 200.1,77.6 C213.8,80.2 216.3,84.9 216.3,84.9 C212.7,93.1 206.9,96.0 205.4,96.6 C205.1,102.4 203.0,107.8 198.3,112.5 C181.9,128.9 168.3,122.5 157.7,114.1 C157.9,116.9 156.7,120.9 152.7,124.9 L141.0,136.5 C139.8,137.7 141.6,141.9 141.8,141.8 Z"
          fill="currentColor" class="octo-body"></path>
      </svg>
    </a>
    <div class="wrap">
    <h1 class="text-center">股市策略分析报告</h1>
    <ul class="index-list">
    `
  results.forEach(item => {
    str += `<li><h3><a href="${item.url}"  target="_blank"  title="">${item.title}</a></h3></li>`
  })
  str += `
  </ul>
  </div>
${getFooter()}
</body>
</html>`
  return str;
}


const htmlTemplate = function (results, date) {
  let str = `
    <!DOCTYPE html>
    <html>
    <head>
      <title>${date}股市策略分析报告</title>
    <style type="text/css" media="screen">
    body {
        color: #333;
        font-size: 12px;
        font-family:"Microsoft YaHei",Arial,sans-serif;
        padding: 0;
        margin: 0
    }
    ul,li {
        list-style: none;
        padding: 0;
        margin: 0
    }

    dl,dd {
        margin: 0
    }

    img {
        border: 0
    }
    a {
        color: #707070;
        text-decoration: none
    }
    a:hover {
        text-decoration: underline;
        color: #2d64b3
    }
    .text-center{
      text-align:center;
    }
    .wrap {
      width: 1002px;
      margin: 0 auto;
    }

    .left-header {
        position: relative;
        padding-left: 16px;
        font-size: 20px
    }

    .left-header::before {
        position: absolute;
        top: 4px;
        bottom: 2px;
        left: 0;
        width: 6px;
        content: "";
        background-color: #1a79ff;
    }

    .hot-concept ul {
        text-align: center;
        font-size: 13px
    }

    .hot-concept ul li {
        height: 50px;
        line-height: 50px;
        text-overflow: ellipsis;
        white-space: nowrap;
        float: left;
        width: 33%;
    }

    .hot-concept .column2 div {
        line-height: normal
    }

    .hot-concept .column2 div:first-child {
        margin-top: 10px
    }

    .hot-concept .column2.opinion {
        padding-left: 18px;
        margin-right: -18px;
        text-align: left
    }
    .hot-concept ul li:nth-child(6n+1),
    .hot-concept ul li:nth-child(6n+2),
    .hot-concept ul li:nth-child(6n+3) {
        background-color: #f0f4fa
    }
    .hot-concept button {
        width: 50px;
        padding: 0
    }
    .hot-concept::after {
        display: block;
        content: "";
        clear: both
    }

    .footer {
        display: block;
        float: left;
        width: 100%;
        padding-top: 30px
    }

    .footer p {
        text-align: center
    }

    </style>
    </head>
    <body>
    <h1 class="text-center">${date}股市策略分析报告</h1>
    <div class="wrap">
        ${results}
    </div>
    ${getFooter()}
    </body>
    </html>
      `
  return str;
}

const htmlBlockTemplate = function (results, name) {
  let str = `
  <h2 class="left-header">${name}</h2>
  <div class="hot-concept">
  <ul>
    `
  results.forEach(item => {
    str += `<li><a href="http://gupiao.baidu.com/stock/${item.codeA}.html"  target="_blank"  title="">${item.name}(${item.code})</a></li>`
  })
  str += `
  </ul>
  </div>
    `
  return str;
}

module.exports = {
  htmlBlockTemplate,
  htmlIndexTemplate,
  htmlTemplate
}
