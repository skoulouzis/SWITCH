/****************************************************************************/
/*                                Apache License                            */
/*                          Version 2.0, January 2004                       */
/*                       http://www.apache.org/licenses/                    */
/****************************************************************************/
import DS from 'ember-data';
import Ember from 'ember';

export default DS.JSONAPISerializer.extend({
  keyForAttribute: function(attr) {
    return Ember.String.underscore(attr);
  },
  keyForRelationship: function(attr) {
    return Ember.String.underscore(attr);
  }
});
