import Ember from 'ember';

export default Ember.Controller.extend({
  component_types: Ember.computed(function() {
    return this.store.findAll('switchcomponenttype');
  })
});
