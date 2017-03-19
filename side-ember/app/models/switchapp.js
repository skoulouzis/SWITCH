import DS from 'ember-data';
import attr from 'ember-data/attr';
import Ember from 'ember';

export default DS.Model.extend({
  title: attr('string'),
  description: attr('string'),
  uuid: attr('string'),
  visible: attr('boolean'),
  editable: attr('boolean'),
  belongs_to_user: attr('boolean'),
  public_view: attr('boolean'),
  public_editable: attr('boolean'),
  status: attr('number', { defaultValue: 0 }),
  user: DS.belongsTo('user'),
  notifications: DS.hasMany('switchnotification'),
  unread_notifications: attr('number', { defaultValue: 0 }),
  readonly: Ember.computed('isNew', 'editable', function() {
    if (this.get('isNew') || this.get('editable')) {
      return null;
    } else {
      return true;
    }
  }),
  is_template: Ember.computed(function() {
    return false;
  }),
  //poll: Ember.observer('unread_notifications', function() {
  //  var _this = this;
  //  var unread = this.get('unread_notifications');
  //  console.log(unread);
  //  if (unread > 0) {
  //    var notifications = this.get('notifications').forEach(function(notification) {
  //      var viewed = notification.get('viewed');
  //      if (viewed === false) {
  //        console.log(notification.get('message'));
  //        notification.set('viewed', true);
  //        notification.save();
  //      }
  //    });
  //  }
  //  Ember.run.later( function() {
  //    _this.reload();
  //    _this.poll();
  //  }, 6000);
  //})
});
