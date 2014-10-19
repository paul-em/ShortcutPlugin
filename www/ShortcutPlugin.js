//Copyright 2013 Jorge Cisneros jorgecis@gmail.com

var ShortcutPlugin = function () {
};

ShortcutPlugin.prototype.CreateShortcut = function (data, successCallback, errorCallback) {

  // to provide backwards compatibility
  if (typeof data === 'String') {
    data = {
      shortcuttext: data
    };
  }

  if (typeof data !== 'Object' || typeof data.shortcuttext !== 'String') {
    errorCallback('required shortcuttext is not set or not a string');
    return;
  }

  cordova.exec(
    successCallback,
    errorCallback,
    'ShortcutPlugin',
    'addShortcut',
    [data]
  );
};
ShortcutPlugin.prototype.RemoveShortcut = function (shortcut_text, successCallback, errorCallback) {
  cordova.exec(
    successCallback,
    errorCallback,
    'ShortcutPlugin',
    'delShortcut',
    [{
      "shortcuttext": shortcut_text
    }]
  );
};

if (!window.plugins) {
  window.plugins = {};
}
if (!window.plugins.Shortcut) {
  window.plugins.Shortcut = new ShortcutPlugin();
}
