import Ember from 'ember';

export default Ember.Route.extend({
  session: Ember.inject.service('session'),
  beforeModel() {
    //if (this.get('session') &&  this.get('session').get('isAuthenticated')) {
    //  this.transitionTo('switchapps');
    //}
  }
});
