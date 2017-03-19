(function ($) {

  var multi = ['onetomany', 'zerotomany', 'single'];
  var drag_up = false;
  var drag_down = false;
  var drag_left = false;
  var drag_right = false;
  var graph;
  var paper;
  var ember_app;

  var cntrlIsPressed = false;
  var shiftIsPressed = false;
  var altIsPressed = false;
  var altGrIsPressed = false;

  this.$(window).blur(function(){
    cntrlIsPressed = false;
    shiftIsPressed = false;
    altIsPressed = false;
    altGrIsPressed = false;
  });

  this.$(window).focus(function(){
    cntrlIsPressed = false;
    shiftIsPressed = false;
    altIsPressed = false;
    altGrIsPressed = false;
  });

  this.$(document).keydown(function (event) {
    if (event.which === 18) {
      altIsPressed = true;
    }
    if (event.which === 17) {
      cntrlIsPressed = true;
    }
    if (event.which === 16) {
      shiftIsPressed = true;
    }
    if (event.which === 225) {
      altGrIsPressed = true;
    }
  });

  this.$(document).keyup(function () {
    cntrlIsPressed = false;
    shiftIsPressed = false;
    altIsPressed = false;
    altGrIsPressed = false;
  });

  $.fn.cntrlIsPressed = function() { return cntrlIsPressed; };
  $.fn.shiftIsPressed = function() { return shiftIsPressed; };
  $.fn.altIsPressed = function() { return altIsPressed; };
  $.fn.altGrIsPressed = function() { return altGrIsPressed; };
  $.fn.anyIsPressed = function() { return (altIsPressed || shiftIsPressed || cntrlIsPressed || altGrIsPressed); };

  $.fn.jointPaper = function(obj) {
    if (obj != null) {
      paper = obj;
    }
    return paper;
  };

  $.fn.toscaEditor = function(obj) {
    if (obj != null) {
      paper = obj;
    }
    return paper;
  };

  $.fn.jointGraph = function(obj) {
    if (obj != null) {
      graph = obj;
    }
    return graph;
  };

  $.fn.dragUp = function(drag) {
    if (drag != null) {
      drag_up = drag;
    }
    return drag_up;
  };

  $.fn.dragDown = function(drag) {
    if (drag != null) {
      drag_down = drag;
    }
    return drag_down;
  };

  $.fn.dragLeft = function(drag) {
    if (drag != null) {
      drag_left = drag;
    }
    return drag_left;
  };

  $.fn.dragRight = function(drag) {
    if (drag != null) {
      drag_right = drag;
    }
    return drag_right;
  };

  $.fn.isBorderClicked = function (bbox, x, y, strokeWidth) {
    var top_boundry_bottom = bbox.y + strokeWidth;
    var bottom_boundry_top = bbox.y + bbox.height - strokeWidth;

    var left_boundry_right = bbox.x + strokeWidth;
    var right_boundry_left = bbox.x + bbox.width - strokeWidth;

    if (x < left_boundry_right) {
      drag_left = true;
    }

    if (x > right_boundry_left) {
      drag_right = true;
    }

    if (y < top_boundry_bottom) {
      drag_up = true;
    }

    if (y > bottom_boundry_top) {
      drag_down = true;
    }

    return (drag_down || drag_left || drag_right || drag_up);
  };

  $.fn.toggleMulti = function (cell, ember_app) {
    var index = multi.indexOf(cell.attr('switch').multi) + 1;
    if (index >= multi.length) {
      index = 0;
    }
    cell.attr('switch').multi = multi[index];

    ember_app.send('toggleComponentMode', cell.id, multi[index]);

    if (cell.attr('switch').multi === 'single') {
      cell.attr('.multi/fill-opacity', '.0');
      cell.attr('.multi2/fill-opacity', '.0');
      cell.attr('.multi/stroke-opacity', '.0');
      cell.attr('.multi2/stroke-opacity', '.0');
      cell.attr('.multi/ref-x', '0');
      cell.attr('.multi2/ref-x', '0');
      cell.attr('.multi/ref-y', '0');
      cell.attr('.multi2/ref-y', '0');

      _.each(graph.getConnectedLinks(cell), function (l) {
        var position = 0.2;
        var label = 0;
        if (l.get('target').id === cell.id) {
          position = 0.8;
          label = 1;
        }
        l.label(label, {
          position: position,
          attrs: {
            text: {fill: 'black', text: ''},
            rect: {fill: 'none'}
          }
        });
      });
    } else {
      var label_title = '1..*';

      if (cell.attr('switch').multi === 'zerotomany') {
        label_title = '0..*';
      }

      cell.attr('.multi/fill-opacity', '.95');
      cell.attr('.multi2/fill-opacity', '.99');
      cell.attr('.multi/stroke-opacity', '1');
      cell.attr('.multi2/stroke-opacity', '1');
      cell.attr('.multi/ref-x', '3');
      cell.attr('.multi2/ref-x', '6');
      cell.attr('.multi/ref-y', '3');
      cell.attr('.multi2/ref-y', '6');

      _.each(graph.getConnectedLinks(cell), function (l) {
        var position = 0.2;
        var label = 0;
        if (l.get('target').id === cell.id) {
          position = 0.8;
          label = 1;
        }
        l.label(label, {
          position: position,
          attrs: {
            text: {fill: 'black', text: label_title},
            rect: {fill: 'white'}
          }
        });
      });
    }
  };

  $.fn.redraw_groups = function () {
    var allViews = $.fn.jointGraph().getElements();

    _.each(allViews, function (el) {
      var cell = $.fn.jointPaper().findViewByModel(el);

      cell = cell.model || cell;

      if (cell instanceof joint.shapes.switch.Group || cell instanceof joint.shapes.switch.Host) {
        cell.toBack();

        if (!cell.get('originalPosition')) {
          cell.set('originalPosition', cell.get('position'));
        }
        if (!cell.get('originalSize')) {
          cell.set('originalSize', cell.get('size'));
        }

        var originalPosition = cell.get('position');
        var originalSize = cell.get('size');

        var newX = originalPosition.x + 30;
        var newY = originalPosition.y + 25;
        var newCornerX = originalPosition.x + 170;
        var newCornerY = originalPosition.y + 40;
        var newWidth = newCornerX - newX + 60;
        var newHeight = newCornerY - newY + 35;

        var has_child = false;

        _.each(cell.getEmbeddedCells(), function (child) {

          var childBbox = child.getBBox();

          if (childBbox.x < newX || !has_child) {
            newX = childBbox.x;
          }
          if (childBbox.y < newY || !has_child) {
            newY = childBbox.y;
          }
          if (childBbox.corner().x > newCornerX) {
            newCornerX = childBbox.corner().x;
          }
          if (childBbox.corner().y > newCornerY) {
            newCornerY = childBbox.corner().y;
          }

          has_child = true;
        });

        newWidth = newCornerX - newX + 70;
        newHeight = newCornerY - newY + 40;

        newX = newX - 30;
        newY = newY - 25;

        if (newX !== originalPosition.x || newY !== originalPosition.y || newWidth !== originalSize.width || newHeight !== originalSize.height) {
          cell.set({
            position: {x: newX, y: newY},
            size: {width: newWidth, height: newHeight}
          }, {skipParentHandler: true});
          cell.toBack();
        }
      }
    });
  };

  $.fn.delPorts = function () {
    var table = $('#component-port-table').DataTable();
    $('#component-port-table').find('tr').each(function() {
      table.row( $(this) ).remove().draw();
    });
  };

  $.fn.addPort = function (list, label, uuid, editable) {
    var table = $('#component-port-table');

    var checked = list === 'in';
    var count = table.find('tbody tr').length;
    var dataTable = table.DataTable();

    if (uuid === null) {
      uuid = joint.util.uuid();
    }

    if (label === '') {
      label = 'new_port';
    }

    var row;

    if (editable) {
      row = dataTable.row.add(['<input type="checkbox" ' + ((checked) ? 'checked' : '') + '/>',
        '<input class="form-control input-sm" type="text" value="' + label + '" /><input type="hidden" value="' + uuid + '" />',
        '<button class="btn btn-xs btn-danger pull-right"><i class="fa fa-trash"></i></button>']).draw().node();

    } else {
      row = dataTable.row.add(['<input type="checkbox" ' + ((checked) ? 'checked' : '') + '/>',
        '<input readonly="readonly" class="form-control input-sm" type="text" value="' + label + '" /><input type="hidden" value="' + uuid + '" />']).draw().node();
    }

    $(row).addClass('modal-port').find("[type='checkbox']").bootstrapSwitch({
      size: 'mini',
      onColor: 'info',
      offColor: 'primary',
      onText: 'ingress',
      offText: 'egress',
      disabled: !editable
    });

    $('#component-port-table i.fa-trash').unbind().click(function () {
      var button = $(this).parent();
      var td = button.parent();
      var tr = td.parent();
      $('#component-port-table').DataTable().row(tr).remove().draw();
    });
  };

  $.fn.updatePorts = function () {
    $('#component-port-table').find('tr').each(function(index) {
      $(this).find("[type='checkbox']").bootstrapSwitch({
        size: 'mini',
        onColor: 'info',
        offColor: 'primary',
        onText: 'ingress',
        offText: 'egress',
        disabled: false
      });
    });

    $('#component-port-table i.fa-trash').unbind().click(function () {
      var button = $(this).parent();
      var td = button.parent();
      var tr = td.parent();
      $('#component-port-table').DataTable().row(tr).remove().draw();
    });
  };

  $.fn.handleAjaxError = function (xhr, status, error) {
    if (xhr.responseText){
      alert(xhr.responseText);
    }
    else{
      alert('Something went wrong');
    }
  };

  $.fn.handleEmberPromiseError = function (errorReason) {
    alert(errorReason);
  };

  $.fn.emberApp = function (obj){
    if (obj != null) {
      ember_app = obj;
    }
    return ember_app;
  };

  $.fn.ctxMenuLoadComponent = function (uuid){
    ember_app.sendAction('loadComponent', uuid);
  };

  $.fn.ctxMenuDeleteComponent = function (uuid){
    var result = confirm("Are you sure you want to delete this element?");
    if (result) {
      ember_app.sendAction('deleteComponent', uuid);
      var element = $.fn.jointGraph().get('cells').find(function (cell) {
        if (cell.id === uuid) {
          return true;
        }
      });
      element.remove();
    }
  };

  $.fn.ctxMenuShowSshConsole = function (uuid){
    ember_app.sendAction('showSshConsole', uuid);
  };

  String.prototype.decodeEscapeSequence = function () {
    return this.replace(/\\x([0-9A-Fa-f]{2})/g, function () {
      return String.fromCharCode(parseInt(arguments[1], 16));
    });
  };
})(jQuery);
