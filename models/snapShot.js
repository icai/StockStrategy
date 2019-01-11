/* jshint indent: 2 */

module.exports = function(sequelize, DataTypes) {
  return sequelize.define('snapshot', {
    uid: {
      type: DataTypes.CHAR(8),
      allowNull: false,
      primaryKey: true
    },
    date: {
      type: DataTypes.INTEGER(8),
      allowNull: false
    },
    time: {
      type: DataTypes.INTEGER(9),
      allowNull: false
    },
    preClose: {
      type: DataTypes.FLOAT,
      allowNull: false
    },
    high: {
      type: DataTypes.FLOAT,
      allowNull: false
    },
    open: {
      type: DataTypes.FLOAT,
      allowNull: false
    },
    low: {
      type: DataTypes.FLOAT,
      allowNull: false
    },
    close: {
      type: DataTypes.FLOAT,
      allowNull: false
    },
    volume: {
      type: DataTypes.INTEGER(11),
      allowNull: false
    },
    amount: {
      type: DataTypes.INTEGER(11),
      allowNull: false
    },
    netChange: {
      type: DataTypes.FLOAT,
      allowNull: false
    },
    netChangeRatio: {
      type: DataTypes.FLOAT,
      allowNull: false
    },
    amplitudeRatio: {
      type: DataTypes.FLOAT,
      allowNull: false
    },
    turnoverRatio: {
      type: DataTypes.FLOAT,
      allowNull: false
    },
    inside: {
      type: DataTypes.INTEGER(11),
      allowNull: false
    },
    outside: {
      type: DataTypes.INTEGER(11),
      allowNull: false
    },
    currencyValue: {
      type: DataTypes.INTEGER(11),
      allowNull: false
    },
    capitalization: {
      type: DataTypes.INTEGER(11),
      allowNull: false
    },
    peratio: {
      type: DataTypes.FLOAT,
      allowNull: false
    },
    volumeRatio: {
      type: DataTypes.FLOAT,
      allowNull: false
    },
    weibiRatio: {
      type: DataTypes.FLOAT,
      allowNull: false
    },
    bvRatio: {
      type: DataTypes.FLOAT,
      allowNull: false
    },
    psRatio: {
      type: DataTypes.FLOAT,
      allowNull: false
    },
    dealCount: {
      type: DataTypes.INTEGER(11),
      allowNull: false
    },
    perShareEarn: {
      type: DataTypes.FLOAT,
      allowNull: false
    },
    LYRPeratio: {
      type: DataTypes.FLOAT,
      allowNull: false
    },
    TTMPeratio: {
      type: DataTypes.FLOAT,
      allowNull: false
    },
    netFundsFlow: {
      type: "DOUBLE(10,5)",
      allowNull: false
    },
    limitUp: {
      type: "DOUBLE(11,3)",
      allowNull: false
    },
    limitDown: {
      type: "DOUBLE(11,3)",
      allowNull: false
    },
    netAssetsPerShare: {
      type: "DOUBLE(11,5)",
      allowNull: false
    },
    totalShareCapital: {
      type: DataTypes.INTEGER(11),
      allowNull: false
    },
    circulatingCapital: {
      type: DataTypes.INTEGER(11),
      allowNull: false
    },
    stockStatus: {
      type: DataTypes.INTEGER(1),
      allowNull: false
    },
    stockCode: {
      type: DataTypes.STRING(10),
      allowNull: false
    },
    stockName: {
      type: DataTypes.STRING(255),
      allowNull: false
    },
    stockAsset: {
      type: DataTypes.INTEGER(2),
      allowNull: false
    }
  }, {
    tableName: 'snapshot'
  });
};
