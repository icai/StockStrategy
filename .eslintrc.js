// https://eslint.org/docs/user-guide/configuring

module.exports = {
  root: true,
  // parserOptions: {
  //   parser: 'babel-eslint'
  // },
  env: {
    browser: true,
    node: true
  },
  extends: [
    // https://github.com/standard/standard/blob/master/docs/RULES-en.md
    //'standard',
	  "eslint:recommended"
  ],
  globals: {
  },
  plugins: [

  ],
  // add your custom rules here
  rules: {
    // allow async-await
    'generator-star-spacing': 'off',
    // allow debugger during development
    'no-debugger': 'off',
    'no-console': 'off',
    "no-const-assign": "warn",
    "no-this-before-super": "warn",
    "no-undef": "warn",
    "no-unreachable": "warn",
    "no-unused-vars": "off",
    "constructor-super": "warn",
    "valid-typeof": "warn"
  }
}
