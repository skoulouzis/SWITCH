/****************************************************************************/
/*                                Apache License                            */
/*                          Version 2.0, January 2004                       */
/*                       http://www.apache.org/licenses/                    */
/****************************************************************************/
import Ember from 'ember';

export default Ember.Controller.extend({
  sessionAccount: Ember.inject.service('session-account'),

  componentTypes: Ember.computed(function() {
    return this.store.findAll('switchcomponenttype');
  }),

  component: null,
  graph_type: 'component',
  graph_endpoint: 'switchcomponents',

  components: Ember.computed('model', function() {

    var component_id = this.get('model').id;

    return this.store.query('switchcomponent', {
      is_template_component: true,
      component_id: component_id
    });
  })
});
