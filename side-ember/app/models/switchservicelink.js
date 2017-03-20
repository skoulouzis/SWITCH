/****************************************************************************/
/*                                Apache License                            */
/*                          Version 2.0, January 2004                       */
/*                       http://www.apache.org/licenses/                    */
/****************************************************************************/
import DS from 'ember-data';
import attr from 'ember-data/attr';

export default DS.Model.extend({
  uuid: attr('string'),
  graph: DS.belongsTo('switchgraph'),
  source: DS.belongsTo('switchcomponentinstance'),
  target: DS.belongsTo('switchcomponentinstance')
});
