import DS from 'ember-data';
import attr from 'ember-data/attr';

export default DS.Model.extend({
  description: attr('string'),
  belongs_to_user: attr('boolean'),
  file: DS.attr('file'),
  document_type: DS.belongsTo('switchdocumenttype'),
  document_type_id: attr('string'),
  user: DS.belongsTo('user'),
});
