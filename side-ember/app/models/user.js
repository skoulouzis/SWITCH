/****************************************************************************/
/*                                Apache License                            */
/*                          Version 2.0, January 2004                       */
/*                       http://www.apache.org/licenses/                    */
/****************************************************************************/
import DS from 'ember-data';
import attr from 'ember-data/attr';

export default DS.Model.extend({
  username: attr('string'),
  email: attr('string')
});
