/****************************************************************************/
/*                                Apache License                            */
/*                          Version 2.0, January 2004                       */
/*                       http://www.apache.org/licenses/                    */
/****************************************************************************/
import Ember from 'ember';

export default Ember.Component.extend({

  actions: {
    okModal: function() {
      $('.modal').modal('hide');
      this.sendAction('hideNotificationModal');
    },

    hideNotificationModal: function() {
      this.sendAction('hideNotificationModal');
    },
  },

  show: Ember.on('didInsertElement', function() {
     Ember.on('hidden.bs.modal', function() {
       this.sendAction('hideNotificationModal');
     }.bind(this), this.$('.modal').modal());
  })
});
