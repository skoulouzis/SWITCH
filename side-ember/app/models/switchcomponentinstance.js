import DS from 'ember-data';
import attr from 'ember-data/attr';
import Ember from 'ember';

export default DS.Model.extend({
  graph: DS.belongsTo('switchgraph'),
  component: DS.belongsTo('switchcomponent'),
  graph_id: attr('string'),
  graph_type: attr('string'),
  component_id: attr('string'),
  title: attr('string'),
  uuid: attr('string'),
  mode: attr('string'),
  last_x: attr('string'),
  last_y: attr('string'),
  editable: attr('boolean'),
  deleteable: attr('boolean'),
  ports: DS.hasMany('switchcomponentport'),
  properties: attr('string', { defaultValue: '' }),
  artifacts: attr('string', { defaultValue: '' }),
  readonly: Ember.computed('isNew', 'editable', function() {
    if (this.get('isNew') || this.get('editable')) {
      return null;
    } else {
      return true;
    }
  })
});
