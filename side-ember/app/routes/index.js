/****************************************************************************/
/*                                Apache License                            */
/*                          Version 2.0, January 2004                       */
/*                       http://www.apache.org/licenses/                    */
/****************************************************************************/
import Ember from 'ember';

export default Ember.Route.extend({
  session: Ember.inject.service('session'),
  beforeModel() {
    //if (this.get('session') &&  this.get('session').get('isAuthenticated')) {
    //  this.transitionTo('switchapps');
    //}
  }
});
