import Ember from 'ember';
import ApplicationRouteMixin from 'ember-simple-auth/mixins/application-route-mixin';

export default Ember.Route.extend(ApplicationRouteMixin, {
  sessionAccount: Ember.inject.service('session-account'),

  beforeModel() {
    this._startPolster();
    return this._loadCurrentUser();
  },
  model: function () {
    if (this.get('session').get('isAuthenticated')) {
      return {
        switchapps: this.store.findAll('switchapp', {reload: true}),
      };
    } else {
      return [];
    }
  },
  _startPolster() {
    console.log("starting polster");
    var _this = this;
    Ember.run.later(function () {
      var cur_pollModel = _this.controller.get('pollModel');
      if (cur_pollModel != null) {
        cur_pollModel.reload();
      }
      _this.send('poll');
    }, 6000);
  },
  sessionAuthenticated() {
    this._loadCurrentUser().then(()=> {
      this.refresh();
      this.transitionTo('index');
    }).catch(() => this.get('session').invalidate());
  },
  _loadCurrentUser() {
    return this.get('sessionAccount').loadCurrentUser();
  },

  actions: {
    showRightBar: function (component) {
      this.controller.setProperties({'component': component});
      this.controller.setProperties({'showRightBar': true});
      $('#rightbar-properties').trigger("refresh");
    },

    hideRightBar: function () {
      var ember_app = this;

      $('#component-menu').hide("slide", {direction: "right"}, 500, function () {
        ember_app.controller.setProperties({'showRightBar': false});
      });
    },

    showLeftBar: function (components) {
      this.controller.setProperties({'components': components});
      this.controller.setProperties({'showLeftBar': true});
      $('#leftbar-properties').trigger("refresh");
    },

    hideLeftBar: function () {
      var ember_app = this;

      $('#component-type-menu').hide("slide", {direction: "left"}, 500, function () {
        ember_app.controller.setProperties({'showLeftBar': false});
      });
    },

    showNotificationModal: function (title, result, details) {
      this.controller.setProperties({'title': title});
      this.controller.setProperties({'result': result});
      this.controller.setProperties({'details': details});

      this.controller.setProperties({'showNotificationModal': true});
    },

    hideNotificationModal: function () {
      this.controller.setProperties({'showNotificationModal': false});
    },

    showSshTerminal: function (component) {
      this.controller.setProperties({'component': component});
      this.controller.setProperties({'showSshConsole': true});
    },

    hideSshConsole: function () {
      this.controller.setProperties({'showSshConsole': false});
    },

    showLoadingModal: function (message) {
      this.controller.setProperties({'waitingForResponse': true});
      var that = this;
      setTimeout(function() {
        if (that.controller.get('waitingForResponse')){
          that.controller.setProperties({'message': message});
          that.controller.setProperties({'showLoadingModal': true});
        }
      }, 5 * 1000);
    },

    hideLoadingModal: function () {
      this.controller.setProperties({'waitingForResponse': false});
      this.controller.setProperties({'showLoadingModal': false});
    },

    updatePoll: function(model) {
      this.controller.set('pollModel', model);
    },

    poll: function() {
      var _this = this;
      var pollModel = this.controller.get('pollModel');

      if (pollModel != null) {
        var unread = pollModel.get('unread_notifications');
        console.log(unread);
        if (unread > 0) {
          var notifications = pollModel.get('notifications').forEach(function (notification) {
            var viewed = notification.get('viewed');
            if (viewed === false) {
              console.log(notification.get('message'));
              var updates = _this.controller.get('newNotifications');
              updates.pushObject(notification);
              console.log(updates.length);
              notification.set('viewed', true);
              notification.save();
            }
          });
        }
      } else {
        console.log('no model');
      }

      Ember.run.later(function () {
        var cur_pollModel = _this.controller.get('pollModel');
        if (cur_pollModel != null) {
          cur_pollModel.reload();
        }
        var cur_component = _this.controller.get('component');
        if (cur_component != null) {
          cur_component.reload();
        }
        _this.send('poll');
      }, 6000);
    }
  }
});
