var exec = require('cordova/exec');
var collectData   = {
  collectData:function (arg0, success, error) {
    exec(success, error, 'sunDebug', 'open', [arg0]);
	}
};
module.exports = collectData;