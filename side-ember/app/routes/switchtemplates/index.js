import Ember from 'ember';
import AuthenticatedRouteMixin from 'ember-simple-auth/mixins/authenticated-route-mixin';

export default Ember.Route.extend(AuthenticatedRouteMixin, {

  renderTemplate: function () {
    this.render();

    this.render('switchtemplates.nav', {
      outlet: 'nav',
      into: 'application'
    });
  },

  actions: {
    remove: function(model) {
      if(confirm('Are you sure?')) {
        model.destroyRecord();
      }
    }
  },

  model: function() {
    return this.store.findAll('switchcomponenttype');
  }
});
