import Ember from 'ember';
import ENV from 'side-ember/config/environment';

import AuthenticatedRouteMixin from 'ember-simple-auth/mixins/authenticated-route-mixin';

export default Ember.Route.extend(AuthenticatedRouteMixin, {

  sessionAccount: Ember.inject.service('session-account'),

  beforeModel() {
    return this._loadCurrentUser();
  },
  _loadCurrentUser() {
    return this.get('sessionAccount').loadCurrentUser();
  },

  model: function () {
    if (this.get('session').get('isAuthenticated')) {
      return {
        switchdocuments: this.store.findAll('switchdocument',{reload:true}),
        documenttypes: this.store.findAll('switchdocumenttype'),
      };
    } else {
      return [];
    }
  },


  actions: {
    uploadDocument: function () {
      var that = this;
      var file = $('#file-field')[0].files[0];
      var description = $('#description').val();
      var document_type_id = $('#documenttype').val();
      this.store.findRecord('switchdocumenttype', document_type_id).then(function(document_type) {

        var message = "Please wait until the file is uploaded";
        that.send('showLoadingModal',message);

        var switch_document = that.store.createRecord('switchdocument',{'file':file, 'description':description, 'document_type':document_type, 'document_type_id': document_type_id});
        switch_document.save().then(function() {
          //Upload ok
          that.send('hideLoadingModal');
          that.send('showNotificationModal', 'File upload', 'ok', ['file upload correctly']);
          that.refresh();
        }).catch(function(error) {
          that.send('hideLoadingModal');
          $.fn.handleEmberPromiseError(error);
        });
      });
    },

    remove: function(model){
      if(confirm('Are you sure?')) {
        model.destroyRecord();
      }
    },

    configure_ec2account: function(user){
      var that = this;
      var message = "Please wait until the ec2 acccount is configured in the provisioner";
      this.send('showLoadingModal',message);

      this.get('session').authorize('authorizer:drf-token-authorizer', (headerName, headerValue) => {
        const headers = {};
        headers[headerName] = headerValue;
        return Ember.$.ajax({
          url: ENV.host + '/api/users/' + user.get('id') + '/configureEC2account',
          type: 'POST',
          dataType: 'json',
          headers: headers
        }).then(function (responseJsonObj) {
          that.send('hideLoadingModal');
          that.send('showNotificationModal', 'Result of configuring ec2 account',responseJsonObj['result'],responseJsonObj['details']);
        }, function(xhr, status, error) {
          that.send('hideLoadingModal');
          $.fn.handleAjaxError(xhr, status, error);
        });
      });
    }
  },
});
