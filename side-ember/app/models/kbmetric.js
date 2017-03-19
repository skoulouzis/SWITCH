import DS from 'ember-data';
import Model from 'ember-data/model';
import attr from 'ember-data/attr';
import Ember from 'ember';

var inflector = Ember.Inflector.inflector;
inflector.irregular('kbmetric', 'metrics');
inflector.singular(/kbmetric/, 'metric');

export default DS.Model.extend({
  name: attr('string'),
  type: attr('string'),
  unit: attr('string'),
  description: attr('string')
});
