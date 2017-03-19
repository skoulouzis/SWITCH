import DS from 'ember-data';
import attr from 'ember-data/attr';
import Ember from 'ember';

export default DS.Model.extend({
  title: attr('string'),
  belongs_to_user: attr('boolean'),
  editable: attr('boolean'),
  type: DS.belongsTo('switchcomponenttype'),
  root_type: DS.belongsTo('switchcomponenttype'),
  is_template: Ember.computed(function() {
    return true;
  })
});
