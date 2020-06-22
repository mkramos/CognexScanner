// Empty constructor
function CognexScanner() {}

// The function that passes work along to native shells
// Message is a string, duration may be 'long' or 'short'
CognexScanner.prototype.show = function(message, duration, successCallback, errorCallback) {
  var options = {};
  options.message = message;
  options.duration = duration;
  cordova.exec(successCallback, errorCallback, 'CognexScanner', 'show', [options]);
}

// Installation constructor that binds ToastyPlugin to window
CognexScanner.install = function() {
  if (!window.plugins) {
    window.plugins = {};
  }
  window.plugins.cognexscanner = new CognexScanner();
  return window.plugins.cognexscanner;
};
cordova.addConstructor(CognexScanner.install);
