/****************************************************************************/
/*                                Apache License                            */
/*                          Version 2.0, January 2004                       */
/*                       http://www.apache.org/licenses/                    */
/****************************************************************************/
import Ember from 'ember';
import ENV from 'side-ember/config/environment';

export default Ember.Component.extend({
  session: Ember.inject.service('session'),

  init: function () {
    this._super();
    this.set('current_graph_id', this.get('graph_id'));
  },

  didInsertElement() {
    this._super(...arguments);

    var graph;

    var erd = ['M 20 0 L 0 5 L 20 10 5 z',
      //'M 10 0 L 0 5 L 10 10 L 10 5 L 20 5 L 10 5 z', arrow w/ line
      //'M 10 10 L 0 10 L 10 10 L 20 10 L 10 10 a 5',
      //'M 0 0 L 10 10 L 0 10 L 10 10 L 0 20 L 10 10 L 20 10 L 10 10 a 5',
      'M 0 0 L 20 10 L 0 10 L 20 10 L 0 20 L 20 10 a 5',
      'M 0 0 L 10 10 L 0 10 L 10 10 L 0 20 L 10 10 a 5,5 0 1,0 10,0 a 5,5 0 1,0 -10,0'];

    var ember_app = this;

    var drawing_link = false;
    var modify_groups = false;
    var source_cell = null;
    var last_x = false;
    var last_y = false;

    var gridSize = 1;

    var wrapper_el = this.$('#paper-wrapper');
    var paper_el = this.$('#paper');

    wrapper_el.height($(window).height() - wrapper_el.offset().top - 50);

    $(window).resize(function () {
      wrapper_el.height($(window).height() - wrapper_el.offset().top - 50);
    });

    var paper_width = paper_el.width();
    var paper_height = paper_el.height();

    graph = new joint.dia.Graph();

    graph.on('remove', function(cell) {
      if (cell.attributes.type === 'switch.ComponentLink'){
        ember_app.send('deleteComponent', cell.id);
      }
      else if (cell.attributes.type === 'switch.ServiceLink'){
        ember_app.send('deleteLink', cell.id);
      }
    });


    $.fn.jointGraph(graph);

    var paper = new joint.dia.Paper({
      el: paper_el,
      width: paper_width,
      height: paper_height,
      model: graph,
      gridSize: gridSize,
      defaultConnector: {name: 'smooth'},
      snapLinks: {radius: 75},
      clickThreshold: 1,
      linkPinning: false,
      interactive: function (cellView) {
        if (cellView.model instanceof joint.dia.Link) {
          return {vertexAdd: $.fn.shiftIsPressed()};
        }
        return true;
      },
      validateConnection: function(cellViewS, magnetS, cellViewT, magnetT, end, linkView) {
        // Prevent linking from input ports.
        if (magnetS && magnetS.getAttribute('type') === 'input') {
          return false;
        }
        // Prevent linking from output ports to input ports within one element.
        if (cellViewS === cellViewT) {
          return false;
        }
        // Prevent linking to input ports.
        return magnetT && magnetT.getAttribute('type') === 'input';
      },
      defaultLink: function(elementView, magnet) {
        var link;
        if (elementView.model.attributes.type === 'switch.Component'){
          link = new joint.shapes.switch.ComponentLink();
        }
        else{
          link = new joint.shapes.switch.ServiceLink();
        }
        return link;
      }
    });

    $.fn.jointPaper(paper);

    this.send('loadGraph');

    paper.on('cell:pointerclick', function (cellView, evt, x, y) {
      if (ember_app.get('graph_editable')) {
        var cell = cellView.model;
        if (cell instanceof joint.shapes.switch.Component) {
          if ($.fn.cntrlIsPressed()) {
            var bbox = cellView.getBBox();
            var centre = bbox.x + (bbox.width / 2);

            var in_ports = [];
            var out_ports = [];

            for (var property in cell.ports) {
              if (cell.ports.hasOwnProperty(property)) {
                var port = cell.ports[property];
                if (port['type'] === 'in') {
                  in_ports.push(port);
                } else {
                  out_ports.push(port);
                }
              }
            }

            var label, type;
            var port_uuid = joint.util.uuid();

            if (x < centre) {
              label = in_ports.length + 1;
              type = 'in';
              in_ports.push({id: port_uuid, label: label});
            } else {
              label = out_ports.length + 1;
              type = 'out';
              out_ports.push({id: port_uuid, label: label});
            }

            ember_app.send('addComponentPort', cell.id, type, label, port_uuid);

            cell.set('inPorts', in_ports);
            cell.set('outPorts', out_ports);

            if (in_ports.length > 0 || out_ports.length > 0) {
              var length = Math.max(Math.max(in_ports.length, out_ports.length) * 25, 30);
              cell.resize(100, length);
            } else {
              cell.resize(100, 30);
            }
          } else if ($.fn.shiftIsPressed()) {
            $.fn.toggleMulti(cell, ember_app);
          }
        } else if (cell instanceof joint.shapes.switch.Group || cell instanceof joint.shapes.switch.Host) {
          if ($.fn.shiftIsPressed()) {
            _.each(cell.getEmbeddedCells(), function (child) {
              $.fn.toggleMulti(child, ember_app);
            });
          }
        }
      }
    });

    paper.on('cell:pointerdown', function (cellView, evt, x, y) {
      if ($.fn.cntrlIsPressed() && ember_app.get('graph_editable')) {
        source_cell = cellView.model;
        if ((source_cell instanceof joint.shapes.switch.Attribute || source_cell instanceof joint.shapes.switch.VirtualResource)) {
          drawing_link = true;
        }
        last_y = y;
        last_x = x;
      } else {
        var cell = cellView.model;

        if (!(cell instanceof joint.shapes.switch.Group || cell instanceof joint.shapes.switch.Host)) {
          cell.toFront();
        }

        if (cell.get('parent')) {
          if ($.fn.altIsPressed()) {
            graph.getCell(cell.get('parent')).unembed(cell);
            ember_app.send('embedInstance', cell.id, null);
            $.fn.redraw_groups();
          }
          modify_groups = true;
        }
      }
    });

    paper.on('blank:pointerclick', function () {
      ember_app.send('hideRightBar');
      ember_app.send('hideLeftBar');
    });

    paper.on('cell:pointerup', function (cellView, evt, x, y) {
      var cell = cellView.model;
      var cellViewsBelow = null;

      if (drawing_link) {
        drawing_link = false;
        if (cell instanceof joint.shapes.switch.Attribute || cell instanceof joint.shapes.switch.VirtualResource) {
          cellViewsBelow = paper.findViewsFromPoint(joint.g.point(x, y));

          if (cellViewsBelow.length) {
            var c = cellViewsBelow[cellViewsBelow.length - 1];
            if (c.model.id !== source_cell.id) {
              var target = c.model;
              var target_show = 'none';
              var target_text = '';
              if (target.attr('switch').multi === 'zerotomany' || target.attr('switch').multi === 'onetomany') {
                target_show = 'white';
                if (target.attr('switch').multi === 'zerotomany') {
                  target_text = '0..*';
                } else {
                  target_text = '1..*';
                }
              }

              var link = new joint.shapes.switch.ServiceLink({
                source: {id: source_cell.id},
                target: {id: c.model.id}
              });

              link.attr('switch', { class: 'ServiceLink', title: 'connection'});

              link.label(0, {
                position: 0.2,
                attrs: {
                  text: {fill: 'black', text: ''},
                  rect: {fill: 'none'}
                }
              });
              link.label(1, {
                position: 0.8,
                attrs: {
                  text: {fill: 'black', text: target_text},
                  rect: {fill: target_show}
                }
              });

              graph.addCell(link);
              ember_app.send('createLink', source_cell.id, c.model.id);
            }
          }
        }
      } else {
        if (cell instanceof joint.shapes.switch.Component) {
          cellViewsBelow = [];

          if (cell.attr('switch').class === 'switch.Component') {
            cellViewsBelow = paper.findViewsFromPoint(cell.getBBox().center());
          }

          if (cellViewsBelow.length) {
            _.each(cellViewsBelow, function (c) {
              if (c.model.id !== cell.id) {
                if (c.model instanceof joint.shapes.switch.Group || c.model instanceof joint.shapes.switch.Host) {
                  graph.getCell(c.model).embed(cell);
                  ember_app.send('embedInstance', cell.id, c.model.id);
                  modify_groups = true;
                }
              }
            });
          }
        }
        ember_app.send('saveGraph');
      }

      if (modify_groups) {
        modify_groups = false;
        $.fn.redraw_groups();
      }
    });

    paper.on('cell:pointermove', function (cellView, evt, x, y) {
      if (drawing_link) {
        var cell = cellView.model || cellView;
        cell.set({
          position: {x: last_x, y: last_y}
        }, {skipParentHandler: true});


        //cellView.pointermove(evt, last_x, last_y, {skipParentHandler: true});
      } else {
        var bbox = cellView.getBBox();
        var constrained = false;

        var constrainedX = x;

        if (bbox.x <= 0) {
          constrainedX = x + gridSize;
          constrained = true;
        }
        if (bbox.x + bbox.width >= paper_width) {
          constrainedX = x - gridSize;
          constrained = true;
        }

        var constrainedY = y;

        if (bbox.y <= 0) {
          constrainedY = y + gridSize;
          constrained = true;
        }
        if (bbox.y + bbox.height >= paper_height) {
          constrainedY = y - gridSize;
          constrained = true;
        }

        //if you fire the event all the time you get a stack overflow
        if (constrained) {
          cellView.pointermove(evt, constrainedX, constrainedY);
        }
      }

      if (modify_groups) {
        $.fn.redraw_groups();
      }
    });

    paper.on('blank:contextmenu', function(evt, x, y) {
      evt.stopPropagation(); // Stop bubbling so that the paper does not handle mousedown.
      evt.preventDefault();  // Prevent displaying default browser context menu.
    });

    paper.on('cell:contextmenu', function (cellView, evt) {
      evt.stopPropagation(); // Stop bubbling so that the paper does not handle mousedown.
      evt.preventDefault();  // Prevent displaying default browser context menu.
      var cell = cellView.model;

      $.fn.emberApp(ember_app);

      //TODO: create context menu as ember component
      var contextMenu;
      if (cell instanceof joint.shapes.switch.VirtualResource && isUpAndRunning(cell.attributes.attrs.switch.title)) {
        contextMenu = $('' +
          '<div id="jqxMenu" class="context-menu">' +
          '   <ul>' +
          '       <li><span title="See details" onclick="$.fn.ctxMenuLoadComponent(\'' + cell.id + '\');">Details</li>' +
          '       <li><span title="Open an ssh console" onclick="$.fn.ctxMenuShowSshConsole(\'' + cell.id + '\');">Ssh Console</li>' +
          '       <li><span title="Delet component" onclick="$.fn.ctxMenuDeleteComponent(\'' + cell.id + '\');">Delete component</li>' +
          '   </ul>' +
          '</div>');
      }
      else{
        contextMenu = $('' +
          '<div id="jqxMenu" class="context-menu">' +
          '   <ul>' +
          '       <li><span title="See details" onclick="$.fn.ctxMenuLoadComponent(\'' + cell.id + '\');">Details</li>' +
          '       <li><span title="Delet component" onclick="$.fn.ctxMenuDeleteComponent(\'' + cell.id + '\');">Delete component</li>' +
          '   </ul>' +
          '</div>');
      }

      function isUpAndRunning(vm){
        var ip_start_pos = vm.lastIndexOf("(");
        var ip_end_pos = vm.lastIndexOf(")");
        if (ip_start_pos===-1 || ip_end_pos===-1){
          return false;
        }
        var ip_address = vm.substring(ip_start_pos + 1, ip_end_pos);
        var regEx = /^(\d|[1-9]\d|1\d\d|2([0-4]\d|5[0-5]))\.(\d|[1-9]\d|1\d\d|2([0-4]\d|5[0-5]))\.(\d|[1-9]\d|1\d\d|2([0-4]\d|5[0-5]))\.(\d|[1-9]\d|1\d\d|2([0-4]\d|5[0-5]))$/i;
        return regEx.test(ip_address);
      }

      paper_el.append(contextMenu);

      contextMenu.jqxMenu({
        width: '200px',
        autoOpenPopup: false,
        animationShowDuration: 0,
        animationHideDuration: 0,
        mode: 'popup'
      });
      contextMenu.jqxMenu('open', evt.pageX, evt.pageY);
    });

    graph.on('change:source change:target', function (link) {
      var sourcePort = link.get('source').port;
      var sourceId = link.get('source').id;
      var targetPort = link.get('target').port;
      var targetId = link.get('target').id;

      var id = link.id;
      var link_obj = $('[model-id=' + id + ']');

      if (targetId !== undefined && targetId !== sourceId) {
        var target = graph.getCell(targetId);
        var target_show = 'none';
        var target_text = '';
        if (target.attr('switch').multi === 'zerotomany' || target.attr('switch').multi === 'onetomany') {
          target_show = 'white';
          if (target.attr('switch').multi === 'zerotomany') {
            target_text = '0..*';
          } else {
            target_text = '1..*';
          }
        }

        var source = graph.getCell(sourceId);
        var source_show = 'none';
        var source_text = '';
        if (source.attr('switch').multi === 'zerotomany' || source.attr('switch').multi === 'onetomany') {
          source_show = 'white';
          if (source.attr('switch').multi === 'zerotomany') {
            source_text = '0..*';
          } else {
            source_text = '1..*';
          }
        }

        link.attr('sourcePortObj',source.ports[sourcePort]);
        link.attr('targetPortObj',target.ports[targetPort]);

        link.attr('switch', { class: 'ComponentLink', title: 'connection'});

        link.label(0, {
          position: 0.2,
          attrs: {
            text: {fill: 'black', text: source_text},
            rect: {fill: source_show}
          }
        });
        link.label(1, {
          position: 0.8,
          attrs: {
            text: {fill: 'black', text: target_text},
            rect: {fill: target_show}
          }
        });
        link.label(2, {
          position: 0.5,
          attrs: {
            text: {fill: 'black', text: 'connection'},
            rect: {fill: 'white'}
          }
        });

        link_obj.find('.labels').dblclick(function () {
          ember_app.send('loadComponent', id);
        });

        ember_app.send('initComponent', id, link.attr('switch').title, 1, false, 0, 0, sourcePort, targetPort);
      }
    });

    this.$(function () {
      //Make every clone image unique.

      $("#paper").droppable({
        drop: function (e, ui) {
          $(this).append($(ui.helper).clone());

          var paper = $("#paper");
          var wrapper = $("#paper-wrapper");

          var drag_div = paper.find(".dragDiv");
          var component = paper.find(".dragElement");
          var is_component = (component.attr('elm-type') === 'External Component' || component.attr('elm-type') === 'Component' || component.attr('elm-type') === 'Network');

          var graph_obj;

          var width = 100;
          var height = 30;

          var left = event.pageX - paper.offset().left - (paper.offset().left - wrapper.offset().left);
          var top = event.pageY - paper.offset().top - (paper.offset().top - wrapper.offset().top);
          var text = 'new_' + component.attr('elm-type').toLowerCase().replace(" ", "_");

          if (drag_div !== undefined) {
            text =  drag_div.attr('elm-title');
          }

          if (is_component) {
            left = left - (width / 2);
            top = top - (height / 2);

            graph_obj = new joint.shapes.switch.Component({
              position: {x: left, y: top},
              size: {width: width, height: height},
              inPorts: [],
              outPorts: [],
              attrs: {
                '.body': {
                  fill: component.css('background-color'),
                  'fill-opacity': '1',
                  rx: 4,
                  ry: 4,
                  'stroke-width': 1,
                  stroke: '#000000'
                },
                '.multi': {
                  fill: component.css('border-color'),
                  'fill-opacity': '.0',
                  'stroke-opacity': '.0',
                  rx: 4,
                  ry: 4,
                  'stroke-width': 1,
                  stroke: '#000000'
                },
                '.multi2': {
                  fill: component.css('border-color'),
                  'fill-opacity': '.0',
                  'stroke-opacity': '.0',
                  rx: 4,
                  ry: 4,
                  'stroke-width': 1,
                  stroke: '#000000'
                },
                '.label': {
                  html: text,
                  fill: '#333'
                },
                '.icon': {
                  d: component.attr('fa-d'),
                  fill: component.css('color')
                },
                switch: {
                  class: 'Component',
                  type: text,
                  code: component.attr('fa-code'),
                  title: text
                }
              },
            });
          } else if (component.attr('elm-type') === 'Virtual Machine' || component.attr('elm-type') === 'Virtual Network') {
            graph_obj = new joint.shapes.switch.VirtualResource({
              position: {x: left, y: top},
              attrs: {
                '.body': {
                  fill: component.css('background-color'),
                  'fill-opacity': '.90',
                  'stroke-width': 2,
                  stroke: component.css('border-color')
                },
                '.label': {
                  html: text,
                  fill: '#333'
                },
                '.icon': {
                  d: component.attr('fa-d'),
                  fill: component.css('color')
                },
                switch: {
                  class: component.attr('elm-type'),
                  type: text,
                  code: component.attr('fa-code'),
                  title: text
                },
              }
            });
          } else if (component.attr('elm-type') === 'Component Group') {
            graph_obj = new joint.shapes.switch.Group({
              position: {x: left, y: top},
              attrs: {
                '.body': {
                  fill: component.css('background-color'),
                  'fill-opacity': '.10',
                  'stroke-width': 2,
                  'stroke-dasharray': '5 2',
                  stroke: component.css('border-color')
                },
                '.label': {
                  html: text,
                  fill: '#333'
                },
                '.icon': {
                  d: component.attr('fa-d'),
                  fill: component.css('color')
                },
                switch: {
                  class: component.attr('elm-type'),
                  type: text,
                  code: component.attr('fa-code'),
                  title: text
                }
              }
            });
          } else {
            graph_obj = new joint.shapes.switch.Attribute({
              position: {x: left, y: top},
              attrs: {
                '.body': {
                  fill: component.css('background-color'),
                  'fill-opacity': '.95',
                  'stroke-width': 2,
                  stroke: component.css('border-color')
                },
                '.label': {
                  html: text,
                  fill: '#333'
                },
                '.icon': {
                  d: component.attr('fa-d'),
                  fill: component.css('color')
                },
                switch: {
                  class: component.attr('elm-type'),
                  type: text,
                  code: component.attr('fa-code'),
                  title: text
                }
              }
            });
          }

          paper.find(".dragDiv").remove();
          component.remove();

          graph.addCells([graph_obj]);

          var id = graph_obj.id;
          var rect_obj = $('[model-id=' + id + ']');

          ember_app.send('initComponent', id, text, component.attr('comp-id'), false, left, top, null, null);

          rect_obj.dblclick(function () {
            if (!$.fn.anyIsPressed()) {
              ember_app.send('loadComponent', id);
            }
          });
        }
      });
    });
  },

  didRender(){
    this._super(...arguments);

    if (this.get('current_graph_id') !== this.get('graph_id')) {
      this.set('current_graph_id', this.get('graph_id'));
    }
  },

  willDestroyElement(params) {
    this._super(...arguments);
    $('#component-menu').hide("slide", {direction: "right"}, 500);
    this.send('hideRightBar');
    this.send('hideLeftBar');
  },

  actions: {
    loadComponent: function (uuid) {
      this.sendAction('loadComponent', uuid);
    },

    // maybe add back pushGraph bool
    initComponent: function (uuid, title, component, showRightBar, x, y, source, target) {
      this.sendAction('initComponent', uuid, title, component, showRightBar, x, y, source, target);
    },

    loadGraph: function () {
      this.sendAction('loadGraph', this.get('current_graph_id'));
    },

    saveGraph: function () {
      if (this.get('graph_editable')) {
        this.sendAction('saveGraph', this.get('current_graph_id'));
      }
    },

    toggleComponentMode: function (uuid, mode) {
      this.sendAction('toggleComponentMode', uuid, mode);
    },

    saveComponent: function () {
      this.sendAction('saveComponent');
    },

    hideRightBar: function () {
      this.sendAction('hideRightBar');
    },

    hideLeftBar: function () {
      this.sendAction('hideLeftBar');
    },

    deleteComponent: function (uuid) {
      this.sendAction('deleteComponent', uuid);
    },

    deleteLink: function (uuid) {
      this.sendAction('deleteLink', uuid);
    },

    addComponentPort: function (uuid, type, title, portId) {
      this.sendAction('addComponentPort', uuid, type, title, portId);
    },

    createLink: function (source_id, target_id, uuid) {
      this.sendAction('createLink', source_id, target_id, uuid);
    },

    embedInstance: function (child_id, parent_id) {
      this.sendAction('embedInstance', child_id, parent_id);
    }
  },

  keyDown(event) {
    this.set('shortcut_key_pressed', true);
  },

  keyUp(event) {
    this.set('shortcut_key_pressed', false);
  }
});
