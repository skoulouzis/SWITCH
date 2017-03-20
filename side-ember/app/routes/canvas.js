/****************************************************************************/
/*                                Apache License                            */
/*                          Version 2.0, January 2004                       */
/*                       http://www.apache.org/licenses/                    */
/****************************************************************************/
import Ember from 'ember';
import ENV from 'side-ember/config/environment';

import hbs from 'htmlbars-inline-precompile';

export default Ember.Route.extend({

  session: Ember.inject.service('session'),

  actions: {
    initComponent: function (uuid, title, component_id, showRightBar, x, y, source, target) {
      var canvas = this;
      var store = this.store;
      var app = this.currentModel;
      store.find('switchcomponent', component_id).then(function(component) {
        var instance = store.createRecord('switchcomponentinstance', {
          title: title,
          uuid: uuid,
          graph_id: app.get('id'),
          component: component,
          component_id: component_id,
          app: app,
          last_x: Math.round(x),
          last_y: Math.round(y),
          mode: 'single',
          graph_type: canvas.controller.get('graph_type')
        });

        instance.save().then(function() {
          if (source !== null && target !== null) {
            canvas.get('session').authorize('authorizer:drf-token-authorizer', (headerName, headerValue) => {
              const headers = {};
              headers[headerName] = headerValue;
              return Ember.$.ajax({
                url: ENV.host + '/api/switchcomponentinstances/' + instance.get('id') + '/link',
                type: 'POST',
                data: {'graph_id': app.get('id'), 'source_id': source, 'target_id': target},
                dataType: 'json',
                headers: headers,
                complete: function () {
                  canvas.send('loadGraph', app.get('id'));
                }
              });
            });
          } else {
            canvas.send('loadGraph', app.get('id'));
          }
        });

        if (showRightBar) {
          canvas.send('showRightBar', instance);
          canvas.controller.setProperties({'component': instance});
        }
      });
    },

    loadComponent: function (uuid) {
      var canvas = this;
      var store = this.store;
      var app = this.currentModel;
      var instance = store.query('switchcomponentinstance', {
        uuid: uuid,
        graph_id: app.get('id')
      }).then(function (switchcomponent) {
        var instance = switchcomponent.objectAt(0);
        if (instance === undefined) {
          $.fn.jointGraph().get('cells').find(function (cell) {
            if (cell.id === uuid) {
              canvas.send('initComponent', uuid, cell.attr('switch').title, cell.attr('switch').class, true);
            }
          });
        } else {
          canvas.send('showRightBar', instance);
          canvas.controller.setProperties({'component': instance});
        }
      }).catch(function(error) {
        $.fn.handleEmberPromiseError(error);
      });
    },

    toggleComponentMode: function (uuid, mode) {
      var app = this.currentModel;
      this.store.query('switchcomponentinstance', {uuid: uuid, graph_id: app.get('id')}).then(function (switchcomponent) {
        var instance = switchcomponent.objectAt(0);
        instance.set('mode', mode);
        instance.save();
      }).catch(function(error) {
        $.fn.handleEmberPromiseError(error);
      });
    },

    embedInstance: function (child_id, parent_id) {
      var canvas = this;
      var app = this.currentModel;
      this.store.query('switchcomponentinstance', {uuid: child_id, graph_id: app.get('id')}).then(function(switchcomponent) {
        var instance = switchcomponent.objectAt(0);
        canvas.get('session').authorize('authorizer:drf-token-authorizer', (headerName, headerValue) => {
          const headers = {};
          headers[headerName] = headerValue;
          var data = {};
          if (parent_id !== null) {
            data = {'parent_id': parent_id};
          }

          return Ember.$.ajax({
            url: ENV.host + '/api/switchcomponentinstances/' + instance.get('id') + '/embed',
            type: 'POST',
            data: data,
            dataType: 'json',
            headers: headers
          });
        });
      });
    },

    createLink: function (source_id, target_id, uuid) {
      var canvas = this;
      var app = this.currentModel;
      this.store.query('switchcomponentinstance', {graph_id: app.get('id')}).then(function (switchcomponents) {
        var source = null;
        var target = null;

        switchcomponents.forEach(function(instance) {
          var uuid = instance.get('uuid');

          if (uuid === source_id) {
            source = instance;
          } else if (uuid === target_id) {
            target = instance;
          }
        });

        if (source !== null && target !== null) {
          var link = canvas.store.createRecord('switchservicelink', {
            graph: source.get('graph'),
            source: source,
            target: target,
            uuid: uuid
          });
          link.save();
        }
      }).catch(function(error) {
        $.fn.handleEmberPromiseError(error);
      });
    },

    deleteLink: function (uuid) {
      var canvas = this;
      var app = this.currentModel;
      if (uuid !== undefined) {
        var link = this.store.query('switchservicelink', {
          uuid: uuid,
          graph_id: app.get('id')
        }).then(function (switchcomponent) {
          var instance = switchcomponent.objectAt(0);
          if (instance !== undefined) {
            instance.deleteRecord();
            instance.get('isDeleted');
            instance.save().catch(function(error) {
              console.log("I'm already gone");
            });
          }
        }).catch(function(error) {
          $.fn.handleEmberPromiseError(error);
        });
      }
    },

    updatePorts: function (ports) {
      var canvas = this;
      var app = this.currentModel;
      var instance = this.controller.get('component');
      canvas.get('session').authorize('authorizer:drf-token-authorizer', (headerName, headerValue) => {
        const headers = {};
        headers[headerName] = headerValue;

        var data = {
          ports: ports
        };

        return Ember.$.ajax({
          url: ENV.host + '/api/switchcomponentinstances/' + instance.get('id') + '/ports',
          type: 'POST',
          data: JSON.stringify(data),
          dataType: 'json',
          contentType: "application/json",
          headers: headers,
          complete: function() {
            canvas.store.unloadAll('switchcomponentport');
          }
        });
      });
    },

    addComponentPort: function (uuid, type, title, portId) {
      var canvas = this;
      var app = this.currentModel;
      this.store.query('switchcomponentinstance', {uuid: uuid, graph_id: app.get('id')}).then(function (switchcomponent) {
        var instance = switchcomponent.objectAt(0);
        var port = canvas.store.createRecord('switchcomponentport', {
          title: title,
          uuid: portId,
          type: type,
          instance: instance
        });
        port.save();
      }).catch(function(error) {
        $.fn.handleEmberPromiseError(error);
      });
    },

    saveComponent: function () {
      var app = this.currentModel;
      var canvas = this;
      this.controller.get('component').save().then(function () {
        canvas.send('hideRightBar');
      }).catch(function(error) {
        $.fn.handleEmberPromiseError(error);
      });
    },

    deleteComponent: function (uuid) {
      var app = this.currentModel;
      var instance;
      var canvas = this;
      if (uuid !== undefined) {
        var store = this.store;
        instance = store.query('switchcomponentinstance', {
          uuid: uuid,
          graph_id: app.get('id')
        }).then(function (switchcomponent) {
          var instance = switchcomponent.objectAt(0);
          if (instance !== undefined) {
            canvas.controller.setProperties({'component': instance});
            instance.deleteRecord();
            instance.get('isDeleted');
            instance.save().then(function() {
              canvas.send('loadGraph', app.get('id'));
            });
          }
        }).catch(function(error) {
          $.fn.handleEmberPromiseError(error);
        });
      } else {
        instance = this.controller.get('component');
        instance.deleteRecord();
        instance.get('isDeleted');
        instance.save().then(function() {
          canvas.send('loadGraph', app.get('id'));
        });
        this.send('hideRightBar');
      }
    },

    loadGraph: function (graph_id) {
      var canvas = this;

      //canvas.store.findAll('kbmetric').then(function() {
      //  alert('success!');
      //});

      var message = "Please wait until the component is updated";
      this.send('showLoadingModal',message);

      this.get('session').authorize('authorizer:drf-token-authorizer', (headerName, headerValue) => {
        const headers = {};
        headers[headerName] = headerValue;
        return Ember.$.ajax({
          url: ENV.host + '/api/' + canvas.controller.get('graph_endpoint') + '/' + graph_id + '/graph',
          type: 'GET',
          dataType: 'json',
          headers: headers
        }).then(function (json) {
          canvas.send('hideLoadingModal');
          if (json['data']['attributes']) {
            $.fn.jointGraph().fromJSON(json['data']['attributes']['graph']);

            $.fn.jointGraph().get('cells').find(function (cell) {
              var o = cell.toJSON();
              var id = o['id'];

              var rect_obj = $('[model-id=' + id + ']');

              if (cell instanceof joint.dia.Link) {
                rect_obj = rect_obj.find('.labels');

                if (cell.attr('switch') === undefined || cell.attr('switch').class === undefined) {
                  return false;
                }
              }

              rect_obj.dblclick(function () {
                if (!$.fn.anyIsPressed()) {
                  canvas.send('loadComponent', id);
                }
              });
            });
            $.fn.redraw_groups();
          }
          else{
            //No graph file has been obtained
          }
        }, function(xhr, status, error) {
          canvas.send('hideLoadingModal');
          $.fn.handleAjaxError(xhr, status, error);
        });
      });
    },

    saveGraph: function (graph_id, callBackFunction) {
      var canvas = this;
      var jsonDoc = $.fn.jointGraph().toJSON();
      var jsonString = JSON.stringify(jsonDoc);

      var message = "Please wait until the component is updated";
      this.send('showLoadingModal',message);

      this.get('session').authorize('authorizer:drf-token-authorizer', (headerName, headerValue) => {
        const headers = {};
        headers[headerName] = headerValue;
        return Ember.$.ajax({
          url: ENV.host + '/api/' + canvas.controller.get('graph_endpoint') + '/' + graph_id + '/graph',
          type: 'POST',
          data: jsonString,
          contentType: "application/json",
          dataType: 'json',
          headers: headers
        }).then(function () {
          // Graph saved correctly
          canvas.send('hideLoadingModal');
          if (callBackFunction){
            callBackFunction();
          }
        }, function(xhr, status, error) {
          canvas.send('hideLoadingModal');
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
    },

    showLeftBarClick: function() {
      var that = this;
      this.store.query('switchcomponent', {
        is_template_component: true,
      }).then(function(components) {
        that.send('showLeftBar', components);
      });
    }
  }
});
