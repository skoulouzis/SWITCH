/****************************************************************************/
/*                                Apache License                            */
/*                          Version 2.0, January 2004                       */
/*                       http://www.apache.org/licenses/                    */
/****************************************************************************/
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
