/****************************************************************************/
/*                                Apache License                            */
/*                          Version 2.0, January 2004                       */
/*                       http://www.apache.org/licenses/                    */
/****************************************************************************/
import Ember from 'ember';

export default Ember.Controller.extend({
  title_sort: ['title'],
  component_types: Ember.computed(function() {
    return this.store.query('switchcomponenttype', {
      is_template: true
    });
  }),
  sorted_types: Ember.computed.sort('component_types', 'title_sort')
});
