import Ember from 'ember';

export default Ember.Component.extend({
  session: Ember.inject.service('session'),

  actions: {
    hideLeftBar: function () {
      this.sendAction('hideLeftBar');
    }
  },

  didInsertElement() {
    var that = this;

    $('#component-type-menu').show("slide", {direction: "right  "}, 500);
  },

  didRender() {
    this._super(...arguments);

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

  willDestroyElement(params) {
    $('#component-type-menu').hide("slide", {direction: "left"}, 300);
  }
});
