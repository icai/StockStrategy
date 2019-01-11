var ghpages = require('gh-pages');
const utils = require('./src/utils');
const log = utils.log;
ghpages.publish('dist', {
  repo: 'https://github.com/icai/stockstrategy.git'
}, function (err) {
  if(err) {
    log(err)
    return false
  }
  log('deployed')
});
