/****************************************************************************/
/*                                Apache License                            */
/*                          Version 2.0, January 2004                       */
/*                       http://www.apache.org/licenses/                    */
/****************************************************************************/
import Ember from 'ember';
import AuthenticatedRouteMixin from 'ember-simple-auth/mixins/authenticated-route-mixin';

export default Ember.Route.extend(AuthenticatedRouteMixin, {

  renderTemplate: function () {
    this.render();

    this.render('switchcomponents.nav', {
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
    return this.store.findAll('switchcomponent');
  }
});
