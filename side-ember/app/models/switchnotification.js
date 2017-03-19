import DS from 'ember-data';
import Model from 'ember-data/model';
import attr from 'ember-data/attr';
import Ember from 'ember';
// import { belongsTo, hasMany } from 'ember-data/relationships';

export default Model.extend({
  title: attr('string'),
  message: attr('string'),
  created_at: attr('string'),
  updated_at: attr('string'),
  severity: attr('number'),
  viewed: attr('boolean')
});
