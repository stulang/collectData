var exec = require('cordova/exec');
var collectData   = {
  collectData:function (arg0, success, error) {
    exec(success, error, 'collectData', 'collectData', [arg0]);
	}
};
module.exports = collectData;