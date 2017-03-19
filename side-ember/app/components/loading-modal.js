import Ember from 'ember';

export default Ember.Component.extend({

  actions: {
    okModal: function() {
      $('.modal').modal('hide');
      this.sendAction('hideLoadingModal');
    },

    hideLoadingModal: function() {
      $('.modal').modal('hide');
      this.sendAction('hideLoadingModal');
    },
  },

  show: Ember.on('didInsertElement', function() {
    Ember.on('hidden.bs.modal', function() {
     this.sendAction('hideLoadingModal');
    }.bind(this), this.$('.modal').modal());
  }),

  willDestroyElement(params) {
    $('.modal').modal('hide');
  }
});
