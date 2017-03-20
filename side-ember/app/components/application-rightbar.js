/****************************************************************************/
/*                                Apache License                            */
/*                          Version 2.0, January 2004                       */
/*                       http://www.apache.org/licenses/                    */
/****************************************************************************/
import Ember from 'ember';
import App from './../app';

export default Ember.Component.extend({
  session: Ember.inject.service('session'),

  actions: {
    invalidateSession() {
      this.get('session').invalidate();
    },

    hideRightBar: function () {
      this.sendAction('hideRightBar');
    },

    saveComponent: function() {
      var ember_app = this;
      var id = $('#component-id').val();

      var type = 'Component';
      var element_cell;

      var element = $.fn.jointGraph().get('cells').find(function (cell) {
        if (cell.id === id) {
          if (cell instanceof joint.shapes.switch.ComponentLink) {
            type = 'ComponentLink';
          } else if (cell instanceof joint.shapes.switch.ServiceLink){
            type = 'ServiceLink';
          } else if (cell instanceof joint.shapes.switch.Attribute) {
            type = 'Attribute';
          } else if (cell instanceof joint.shapes.switch.VirtualResource) {
            type = 'VirtualResource';
          } else if (cell instanceof joint.shapes.switch.Group) {
            type = 'Group';
          } else if (cell instanceof joint.shapes.switch.Host) {
            type = 'Host';
          }
          element_cell = cell;
          return true;
        }
      });

      var in_ports = [];
      var out_ports = [];
      var ports = [];

      if (type === 'Component') {
        $('#component-port-table').find('tbody').children().each(function () {
          var checkbox = $(this).find('[type=checkbox]');
          if ($(this).find('[type=text]').val() !== undefined) {
            var port = {label: $(this).find('[type=text]').val(), id: $(this).find('[type=hidden]').val()};

            if (checkbox.is(':checked')) {
              port['type'] = 'in';
              in_ports.push(port);
            } else {
              port['type'] = 'out';
              out_ports.push(port);
            }

            ports.push(port);
          }
        });
        ember_app.sendAction('updatePorts', ports);
      }

      var properties = [];

      $('#props').find('ul').children().each(function () {
        properties.push($(this).find('.key_put').val() + ":" + $(this).find('.val_put').val());
      });

      var title = $('#title').val();

      element.set('properties', properties);
      element.set('title', title);
      element.attr('switch').title = title;

      if (type !== 'ComponentLink' && type !== 'ServiceLink') {
        if (type === 'Component') {
          element.set('inPorts', in_ports);
          element.set('outPorts', out_ports);

          if (in_ports.length > 0 || out_ports.length > 0) {
            var length = Math.max(Math.max(in_ports.length, out_ports.length) * 25, 30);
            element_cell.resize(element_cell.getBBox().width, length);
          } else {
            element_cell.resize(element_cell.getBBox().width, 30);
          }
        }

        element.attr('.label')['html'] = title;
        var rect_obj = $('[model-id=' + id + ']');
        var label = rect_obj.find('.label');
        label.html(title);
      } else {

        element.label(2, {
          position: '.5',
          attrs: {
            rect: {fill: 'white'},
            text: {fill: 'black', text: title}
          }
        });
      }

      ember_app.sendAction('saveComponent');
    },

    deleteComponent: function() {
      var ember_app = this;
      var component = this.get('instance');
      var id = component.get('uuid');

      var element = $.fn.jointGraph().get('cells').find(function (cell) {
        if (cell.id === id) {
          return true;
        }
      });

      //element.remove();

      ember_app.sendAction('deleteComponent');
    }
  },

  didInsertElement() {
    // clear ASAP deployment properties
    App.deploy = null;
    App.ips = null;

    var that = this;
    updateRightBar(that);

    var properties_container = $('#properties')[0];
    var properties_hidden = $('#rightbar-properties');

    var properties_editor = CodeMirror.fromTextArea(properties_container, {
      mode: 'text/x-yaml',
      lineNumbers: true,
      autoRefersh:true,
      gutters: ["CodeMirror-linenumbers"]
    });

    properties_editor.on("change", function(cm, change) {
      if (that.get('instance').get('editable')) {
        properties_hidden.val(cm.getValue());
        properties_hidden.blur();
      }
    });

    properties_hidden.on("refresh", function() {
      setTimeout(function() {
        properties_editor.getDoc().setValue(properties_hidden.val());
        properties_editor.refresh();
        updateRightBar(that);
      },1);
    });

    var artifacts_container = $('#artifacts')[0];
    var artifacts_hidden = $('#rightbar-artifacts');

    var artifacts_editor = CodeMirror.fromTextArea(artifacts_container, {
      mode: 'text/x-yaml',
      lineNumbers: true,
      autoRefersh:true,
      gutters: ["CodeMirror-linenumbers"]
    });

    artifacts_editor.on("change", function(cm, change) {
      if (that.get('instance').get('editable')) {
        artifacts_hidden.val(cm.getValue());
        artifacts_hidden.blur();
      }
    });

    artifacts_hidden.on("refresh", function() {
      setTimeout(function() {
        artifacts_editor.getDoc().setValue(artifacts_hidden.val());
        artifacts_editor.refresh();
        updateRightBar(that);
      },1);
    });

    setTimeout(function() {
      properties_editor.refresh();
      artifacts_editor.refresh();
    },1);

    //todo - figure out how to switch off editor!
    $('#component-save').click(function () {
      //save tosca properties for deployment and additional data
      App.deploy = extractPropertyValue(properties_editor.getValue(),"deploy");
      App.ips = extractPropertyValue(properties_editor.getValue(),"ips");

      properties_hidden.val(properties_editor.getValue());
      artifacts_hidden.val(artifacts_editor.getValue());
    });

    function extractPropertyValue(str, property) {
      var splitStr = str.split(/\n/);

      for (var i = 0; i < splitStr.length; i++) {
        var entrySplit = splitStr[i].split(":");
        if (entrySplit[0].replace("\\s+", "") === property.replace("\\s+", "")){
          return entrySplit[1].replace(/ /g,'');
        }

      }
      return null;
    }

    function updateRightBar(that) {
      var component = that.get('instance');
      var component_id = component.get('uuid');

      var element = $.fn.jointGraph().get('cells').find(function (cell) {
        if (cell.id === component_id) {
          return true;
        }
      });

      $('#component-id').val(element.id);

      var component_type = $('#type');
      if (component_type.val() === '') {
        component.type = element.attr('switch').class;
      }

      var component_mode = $('#mode');
      if (component_mode.val() === '') {
        component.mode = element.attr('switch').multi;
      }

      var ports_tab = $('#ports-tab').hide();
      var properties_tab = $('#properties-tab').hide();
      var artifacts_tab = $('#artifacts-tab').hide();

      if (element.attr('switch').class === 'switch.Component') {
        ports_tab.show();
        artifacts_tab.show();
      }

      properties_tab.show();

      $('#component-menu').show("slide", {direction: "right"}, 500);

    }

  },

  didRender() {
    this._super(...arguments);

    var ember_app = this;

    this.$('.panel-title').each(function () {
      var a = $(this).find('a:first').css('display', 'block');
      if (a.attr('href') === '#collapseFour') {
        a.click(function() {
          $('#rightbar-properties').trigger("refresh");
        });
      }
      if (a.attr('href') === '#collapseThree') {
        a.click(function() {
          $('#rightbar-artifacts').trigger("refresh");
        });
      }
    });

    $('#component-port-table').DataTable();

    $.fn.updatePorts();
  },

  willDestroyElement(params) {
    $('#component-menu').hide("slide", {direction: "right"}, 500);
  }
});
