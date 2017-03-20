/****************************************************************************/
/*                                Apache License                            */
/*                          Version 2.0, January 2004                       */
/*                       http://www.apache.org/licenses/                    */
/****************************************************************************/
/*jshint node:true*/
/* global require, module */
var EmberApp = require('ember-cli/lib/broccoli/ember-app');

module.exports = function(defaults) {
  var app = new EmberApp(defaults, {
    // Add options here
  });

  // Use `app.import` to add additional libraries to the generated
  // output files.
  //
  // If you need to use different assets in different
  // environments, specify an object as the first parameter. That
  // object's keys should be the environment name and the values
  // should be the asset to use in that environment.
  //
  // If the library that you are including contains AMD or ES6
  // modules that you would like to import into your application
  // please specify an object with the list of modules as keys
  // along with the exports of each module as its value.

  app.import('vendor/fonts/fontawesome-webfont.ttf');
  app.import('vendor/fonts/fontawesome-webfont.woff');
  app.import('vendor/fonts/fontawesome-webfont.woff2');
  app.import('vendor/fonts/fontawesome-webfont.svg');

  app.import('bower_components/jquery-ui/jquery-ui.min.js');
  app.import('bower_components/datatables/media/js/jquery.dataTables.js');
  app.import('bower_components/datatables/media/js/dataTables.jqueryui.js');
  app.import('bower_components/datatables/media/js/dataTables.bootstrap.min.js');
  app.import('bower_components/datatables-responsive/js/dataTables.responsive.js');
  app.import('bower_components/jsoneditor/dist/jsoneditor.js');
  app.import('vendor/js/data-tables.cell-edit.js');
  app.import('bower_components/lodash/lodash.min.js');
  app.import('bower_components/backbone/backbone-min.js');
  app.import('vendor/js/joint.js');
  app.import('vendor/js/json2yaml.js');
  app.import('bower_components/bootstrap/dist/js/bootstrap.min.js');
  app.import('vendor/js/bootstrap-switch.min.js');
  app.import('bower_components/metisMenu/dist/metisMenu.min.js');
  app.import('vendor/js/sb-admin-2.js');
  app.import('bower_components/codemirror/lib/codemirror.js');
  app.import('bower_components/codemirror/mode/yaml/yaml.js');
  app.import('bower_components/codemirror/addon/fold/foldcode.js');
  app.import('bower_components/codemirror/addon/fold/foldgutter.js');
  app.import('bower_components/codemirror/addon/fold/indent-fold.js');
  //app.import('bower_components/bootstrap/js/dropdown.js');
  app.import('vendor/js/bootstrap-select.min.js');
  app.import('vendor/js/side.js');
  app.import('bower_components/socket.io-client/socket.io.js');
  app.import('bower_components/yaml-js/yaml.js');
  app.import('bower_components/remarkable-bootstrap-notify/dist/bootstrap-notify.js');

  app.import('bower_components/bootstrap/dist/css/bootstrap.min.css');
  app.import('bower_components/bootstrap/dist/css/bootstrap.min.css.map');
  app.import('bower_components/datatables/media/css/dataTables.bootstrap.min.css');
  app.import('bower_components/metisMenu/dist/metisMenu.min.css');
  app.import('bower_components/jsoneditor/dist/jsoneditor.min.css');
  app.import('vendor/css/bootstrap-switch.min.css');
  app.import('vendor/css/timeline.css');
  app.import('vendor/css/sb-admin-2.css');
  app.import('vendor/css/joint.css');
  app.import('bower_components/morrisjs/morris.css');
  app.import('bower_components/font-awesome/css/font-awesome.min.css');
  app.import('bower_components/codemirror/lib/codemirror.css');
  app.import('bower_components/codemirror/addon/fold/foldgutter.css');
  app.import('vendor/css/bootstrap-select.min.css');
  app.import('vendor/css/side.css');
  app.import('bower_components/animate.css/animate.min.css');

  app.import('bower_components/jqwidgets/jqwidgets/jqxcore.js');
  app.import('bower_components/jqwidgets/jqwidgets/jqxmenu.js');

  return app.toTree();
};
