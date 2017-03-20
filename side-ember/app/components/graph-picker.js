/****************************************************************************/
/*                                Apache License                            */
/*                          Version 2.0, January 2004                       */
/*                       http://www.apache.org/licenses/                    */
/****************************************************************************/
import Ember from 'ember';

export default Ember.Component.extend({

  init: function () {
    this._super();
    this.set('current_graph_id', this.get('graph_id'));
  },

  didRender() {
    this._super(...arguments);

    if (this.get('current_graph_id') !== this.get('graph_id')) {
      this.set('current_graph_id', this.get('graph_id'));
    }

    var counts = [0];

    this.$(function () {
      //Make every clone image unique.

      $(".dragDiv").draggable({
        helper: "clone",
        start: function () {
          counts[0]++;
        }
      });
    });
  },

  actions: {
    loadGraph: function () {
      this.sendAction('loadGraph', this.get('graph_id'));
    },

    showLeftBar: function () {
      this.sendAction('showLeftBar');
    },

    exportGraph: function () {
      this.sendAction('exportGraph', this.get('graph_id'));
    },

    validateGraph: function () {
      this.sendAction('validateGraph', this.get('graph_id'));
    },

    planVirtualInfrastructure: function() {
      this.sendAction('planVirtualInfrastructure', this.get('graph_id'));
    },

    provisionVirtualInfrastructure: function() {
      this.sendAction('provisionVirtualInfrastructure', this.get('graph_id'));
    },

    deployApplication: function() {
      this.sendAction('deployApplication', this.get('graph_id'));
    }
  }

});
