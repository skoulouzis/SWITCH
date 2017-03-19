import DS from 'ember-data';
import Model from 'ember-data/model';
import attr from 'ember-data/attr';
import Ember from 'ember';
// import { belongsTo, hasMany } from 'ember-data/relationships';

export default Model.extend({
  title: attr('string'),
  created_at: attr('string'),
  belongs_to_user: attr('boolean'),
  status: attr('number', { defaultValue: 0 }),
  user: DS.belongsTo('user'),
  notifications: DS.hasMany('switchnotification'),
  unread_notifications: attr('number', { defaultValue: 0 }),
  readonly: Ember.computed('isNew', function() {
    if (this.get('isNew')) {
      return null;
    } else {
      return true;
    }
  }),
  is_template: Ember.computed(function() {
    return false;
  })
});
