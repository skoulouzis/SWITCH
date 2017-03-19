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
    var that = this;

    var container = document.getElementById("output");
    var editor = CodeMirror.fromTextArea(container, {
      mode: 'text/x-yaml',
      lineNumbers: true,
      lineWrapping: true,
      foldGutter: true,
      readonly: that.get('graph_editable'),
      gutters: ["CodeMirror-linenumbers", "CodeMirror-foldgutter"]
    });

    $.fn.toscaEditor(editor);

    setTimeout(function() {
      $.fn.toscaEditor().refresh();
      that.send('loadJson');
    },1);
  },

  didRender(){
    this._super(...arguments);

    if (this.get('current_graph_id') !== this.get('graph_id')) {
      this.sendAction('changeJson', this.get('current_graph_id'), this.get('graph_id'));
      this.set('current_graph_id', this.get('graph_id'));
    }

    var wrapper_el = this.$('#wrapper');
    wrapper_el.height($(window).height() - wrapper_el.offset().top - 20);

    $(window).resize(function(){
      wrapper_el.height($(window).height() - wrapper_el.offset().top - 20);
    });
  },

  actions: {
    changeJson: function (from_graph_id, to_graph_id) {
      this.sendAction('changeJson', from_graph_id, to_graph_id);
    },

    loadJson: function () {
      this.sendAction('loadJson', this.get('current_graph_id'));
    },

    saveJson: function () {
      this.sendAction('saveJson', this.get('current_graph_id'));
    }
  }
});
