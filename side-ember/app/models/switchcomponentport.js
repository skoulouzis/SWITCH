import DS from 'ember-data';
import attr from 'ember-data/attr';
import Ember from 'ember';

export default DS.Model.extend({
  instance: DS.belongsTo('switchcomponentinstance'),
  type: attr('string'),
  title: attr('string'),
  uuid: attr('string'),
  checked: Ember.computed('type', function() {
    return (this.get('type') === 'in');
  })
});
