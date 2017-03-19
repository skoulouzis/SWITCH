import Ember from 'ember';
import ENV from 'side-ember/config/environment';
import CanvasRoute from './../canvas';
import App from './../../app';

export default CanvasRoute.extend({

  renderTemplate: function () {
    this.render();

    this.render('switchapps.nav', {
      outlet: 'nav',
      into: 'application'
    });

    this.send('updatePoll', this.currentModel);
  },

  actions: {
    validateGraph: function () {
      var composer = this;
      var app = this.currentModel;

      var message = "Please wait until the application is validated";
      this.send('showLoadingModal',message);

      this.get('session').authorize('authorizer:drf-token-authorizer', (headerName, headerValue) => {
        const headers = {};
        headers[headerName] = headerValue;
        return Ember.$.ajax({
          url: ENV.host + '/api/switchapps/' + app.get('id') + '/validate',
          type: 'GET',
          dataType: 'json',
          headers: headers
        }).then(function (responseJsonObj) {
          composer.send('hideLoadingModal');
          composer.send('showNotificationModal', 'Results of validation', responseJsonObj['result'], responseJsonObj['details']);
        }, function(xhr, status, error) {
          composer.send('hideLoadingModal');
          $.fn.handleAjaxError(xhr, status, error);
        });
      });
    },

    planVirtualInfrastructure: function () {
      var composer = this;
      var app = this.currentModel;

      var message = "Please wait until the planification of the virtual infrastructure is completed";
      this.send('showLoadingModal',message);

      this.get('session').authorize('authorizer:drf-token-authorizer', (headerName, headerValue) => {
        const headers = {};
        headers[headerName] = headerValue;
        return Ember.$.ajax({
          url: ENV.host + '/api/switchapps/' + app.get('id') + '/planVirtualInfrastructure',
          type: 'GET',
          dataType: 'json',
          headers: headers
        }).then(function (responseJsonObj) {
          composer.send('hideLoadingModal');
          composer.send('showNotificationModal','Result of planning the virtual infrastructure',responseJsonObj['result'],responseJsonObj['details']);
          composer.send('loadGraph', app.get('id'));
        }, function(xhr, status, error) {
          composer.send('hideLoadingModal');
          $.fn.handleAjaxError(xhr, status, error);
        });
      });
    },

    showSshConsole: function (uuid){
      var switch_app = this;
      var store = this.store;
      var app = this.currentModel;
      var instance = store.query('switchcomponentinstance', {
        uuid: uuid,
        graph_id: app.get('id')
      }).then(function (switchcomponent) {
        var instance = switchcomponent.objectAt(0);
        if (instance !== undefined) {
          switch_app.send('showSshTerminal', instance);
        }
      }).catch(function(error) {
        $.fn.handleEmberPromiseError(error);
      });
    },

    provisionVirtualInfrastructure: function () {
      var composer = this;
      var app = this.currentModel;
      var message = "Please wait until the virtual infrastructure is provisioned";
      this.send('showLoadingModal',message);

      this.get('session').authorize('authorizer:drf-token-authorizer', (headerName, headerValue) => {
        const headers = {};
        headers[headerName] = headerValue;
        return Ember.$.ajax({
          url: ENV.host + '/api/switchapps/' + app.get('id') + '/provisionVirtualInfrastructure',
          type: 'GET',
          dataType: 'json',
          headers: headers
        }).then(function (responseJsonObj) {
          composer.send('hideLoadingModal');
          composer.send('showNotificationModal', 'Result of provisioning virtual infrastructure',responseJsonObj['result'],responseJsonObj['details']);
          composer.send('loadGraph', app.get('id'));
        }, function(xhr, status, error) {
          composer.send('hideLoadingModal');
          $.fn.handleAjaxError(xhr, status, error);
        });
      });
    },

    deployApplication: function () {
      var composer = this;
      var app = this.currentModel;
      var message = "Please wait until the application is deployed";
      this.send('showLoadingModal',message);

      this.get('session').authorize('authorizer:drf-token-authorizer', (headerName, headerValue) => {
        const headers = {};
        headers[headerName] = headerValue;
        return Ember.$.ajax({
          url: ENV.host + '/api/switchapps/' + app.get('id') + '/deploy',
          type: 'GET',
          dataType: 'json',
          headers: headers
        }).then(function (responseJsonObj) {
          composer.send('hideLoadingModal');

          var pk = responseJsonObj['pk'];
          // ASAP deployment attributes
          var deployment =  App.deploy;
          var ips =  App.ips;

          if (pk != null) {
            var instance = composer.store.find('switchappinstance', pk);

            // ASAP integration: calling ASAP dedicated deployment frontend component
            if (deployment) {
              composer.transitionTo('switchappinstances.asap-composer', instance).then(function () {
              });
            } else {
              composer.transitionTo('switchappinstances.composer', instance).then(function () {
                composer.send('showNotificationModal', 'Result of deployment', responseJsonObj['result'], responseJsonObj['details']);
              });
            }
          } else {
            composer.send('showNotificationModal', 'Result of deployment', responseJsonObj['result'], responseJsonObj['details']);
          }
        }, function(xhr, status, error) {
          composer.send('hideLoadingModal');
          $.fn.handleAjaxError(xhr, status, error);
        });
      });
    },

    exportGraph: function () {
      var top_component = 0;
      var bottom_component = 1;
      var left_component = 0;
      var right_component = 1;

      var app = this.currentModel;
      var title = app.get('title').toLowerCase() + '_graph';

      var svgText = $("#paper").find("svg")[0];
      var myCanvas = document.getElementById("canvas");
      var ctxt = myCanvas.getContext("2d");

      function traverse(obj, tree) {
        tree.push(obj);
        if (obj.hasChildNodes()) {
          var child = obj.firstChild;
          while (child) {
            if (child.nodeType === 1 && child.nodeName !== 'SCRIPT') {
              traverse(child, tree);
            }
            child = child.nextSibling;
          }
        }
      }

      var emptySVG = $('#emptysvg')[0];
      var emptySvgDeclarationComputed = getComputedStyle(emptySVG);

      var allElements = [];
      traverse(svgText, allElements);
      var i = allElements.length;
      while (i--) {
        explicitlySetStyle(allElements[i]);
      }

      function explicitlySetStyle(element) {
        var cSSStyleDeclarationComputed = getComputedStyle(element);
        var i, len, key, value;
        var computedStyleStr = "";
        for (i = 0, len = cSSStyleDeclarationComputed.length; i < len; i++) {
          key = cSSStyleDeclarationComputed[i];
          value = cSSStyleDeclarationComputed.getPropertyValue(key);
          if (value !== emptySvgDeclarationComputed.getPropertyValue(key) && key !== 'width' && key !== 'height') {
            computedStyleStr += key + ":" + value + ";";
          }
        }
        element.setAttribute('style', computedStyleStr);
      }

      var allViews = $.fn.jointGraph().getElements();

      _.each(allViews, function (el) {
        var cell = $.fn.jointPaper().findViewByModel(el);
        var bbox = cell.getBBox();

        var top = bbox.y;
        var bottom = bbox.y + bbox.height;
        var left = bbox.x;
        var right = bbox.x + bbox.width;

        if (top < top_component || top_component === 0) {
          top_component = top;
        }
        if (bottom > bottom_component) {
          bottom_component = bottom;
        }
        if (left < left_component || left_component === 0) {
          left_component = left;
        }
        if (right > right_component) {
          right_component = right;
        }
      });

      var svg = new Blob([svgText.outerHTML], {type: "image/svg+xml;charset=utf-8"}),
        domURL = window.URL || window.webkitURL || window,
        url = domURL.createObjectURL(svg),
        img = new Image();

      var imgURI = url;

      ctxt.fillStyle = "#FF0000";
      ctxt.fillRect(0, 0, 150, 100);

      img.onload = function () {
        myCanvas.width = img.width;
        myCanvas.height = img.height;
        myCanvas.width = right_component + left_component;
        myCanvas.height = bottom_component + top_component;
        //myCanvas.setAttribute('background-color', '#FFFFFF');
        ctxt.drawImage(this, 0, 0);
        domURL.revokeObjectURL(url);

        imgURI = myCanvas
          .toDataURL('image/png')
          .replace('image/png', 'image/octet-stream');

        var evt = new MouseEvent('click', {
          view: window,
          bubbles: false,
          cancelable: true
        });

        var a = document.createElement('a');
        a.setAttribute('download', title + '.png');
        a.setAttribute('href', imgURI);
        a.setAttribute('target', '_blank');

        a.dispatchEvent(evt);
      };

      img.src = url;

      i = allElements.length;
      while (i--) {
        allElements[i].setAttribute('style', '');
      }
    }
  }
});
