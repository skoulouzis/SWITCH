/****************************************************************************/
/*                                Apache License                            */
/*                          Version 2.0, January 2004                       */
/*                       http://www.apache.org/licenses/                    */
/****************************************************************************/
import DS from 'ember-data';
import Model from 'ember-data/model';
import attr from 'ember-data/attr';
import Ember from 'ember';
// import { belongsTo, hasMany } from 'ember-data/relationships';

export default DS.Model.extend({
  title: attr('string'),
  description: attr('string'),
  primary_colour: attr('string'),
  secondary_colour: attr('string'),
  icon_name: attr('string'),
  icon_class: attr('string'),
  icon_style: attr('string'),
  icon_svg: attr('string'),
  icon_code: attr('string'),
  icon_colour: attr('string'),
  switch_class: DS.belongsTo('switchcomponentclass'),
  is_core_component: attr('boolean'),
  is_template_component: attr('boolean'),
  is_component_group: attr('boolean'),
  classpath: attr('string'),
  safe_icon_style: Ember.computed('icon_style', function() {
    return new Ember.String.htmlSafe(this.get('icon_style'));
  })


});
