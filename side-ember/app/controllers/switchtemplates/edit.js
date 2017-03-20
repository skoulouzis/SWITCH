/****************************************************************************/
/*                                Apache License                            */
/*                          Version 2.0, January 2004                       */
/*                       http://www.apache.org/licenses/                    */
/****************************************************************************/
import Ember from 'ember';

export default Ember.Controller.extend({
  component_types: Ember.computed(function() {
    return this.store.findAll('switchcomponenttype');
  })
});
