/****************************************************************************/
/*                                Apache License                            */
/*                          Version 2.0, January 2004                       */
/*                       http://www.apache.org/licenses/                    */
/****************************************************************************/
import Ember from 'ember';

export default Ember.Controller.extend({
  sessionAccount: Ember.inject.service('session-account'),

  components: Ember.computed(function() {
    return this.store.query('switchcomponent', {
      is_template_component: false
    });
  }),

  componentTypes: Ember.computed(function() {
    return this.store.findAll('switchcomponenttype');
  }),

  component: null,
  graph_type: 'app',
  graph_endpoint: 'switchappinstances'
});
