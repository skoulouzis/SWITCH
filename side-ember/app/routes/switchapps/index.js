import Ember from 'ember';
import ENV from 'side-ember/config/environment';
import AuthenticatedRouteMixin from 'ember-simple-auth/mixins/authenticated-route-mixin';

export default Ember.Route.extend(AuthenticatedRouteMixin, {

  renderTemplate: function () {
    this.render();

    this.render('switchapps.nav', {
      outlet: 'nav',
      into: 'application'
    });
  },

  actions: {
    remove: function(model) {
      if(confirm('Are you sure?')) {
        model.destroyRecord();
      }
    },

    clone: function(switchapp){
      if(confirm('Do you want to clone this application?')) {
        var message = "Please wait until the application is copied";
        this.send('showLoadingModal', message);
        var that = this;

        this.get('session').authorize('authorizer:drf-token-authorizer', (headerName, headerValue) => {
          const headers = {};
          headers[headerName] = headerValue;
          return Ember.$.ajax({
            url: ENV.host + '/api/switchapps/' + switchapp.get('id') + '/clone',
            type: 'POST',
            contentType: "application/json",
            dataType: 'json',
            headers: headers,
          }).then(function () {
            that.send('hideLoadingModal');
            that.send('showNotificationModal', 'Result', 'ok', ['Application copied correctly']);
            that.refresh();
          }, function (xhr, status, error) {
            that.send('hideLoadingModal');
            $.fn.handleAjaxError(xhr, status, error);
          });
        });
      }
    }
  },

  model: function() {
    return this.store.findAll('switchapp');
  }
});
