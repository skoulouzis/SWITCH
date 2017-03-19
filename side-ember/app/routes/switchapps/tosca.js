import Ember from 'ember';
import ENV from 'side-ember/config/environment';

export default Ember.Route.extend({
  renderTemplate: function () {
    this.render();

    this.render('switchapps.nav', {
      outlet: 'nav',
      into: 'application'
    });
  },

  session: Ember.inject.service('session'),

  actions: {
    changeJson: function (from_graph_id, to_graph_id) {
      //var jsonDoc = $.fn.jointGraph().toJSON();
      //var jsonString = JSON.stringify(jsonDoc);
      //var switch_app = this;
      //
      //this.get('session').authorize('authorizer:drf-token-authorizer', (headerName, headerValue) => {
      //  const headers = {};
      //  headers[headerName] = headerValue;
      //  return Ember.$.ajax({
      //    url: ENV.host + '/api/switchapps/' + from_graph_id + '/graphs/json/',
      //    type: 'PUT',
      //    data: jsonString,
      //    contentType: "application/json",
      //    dataType: 'json',
      //    headers: headers,
      //    complete: function() {
      //      switch_app.send('loadGraph', to_graph_id);
      //    }
      //  });
      //});
    },

    loadJson: function (graph_id) {
      var composer = this;
      this.get('session').authorize('authorizer:drf-token-authorizer', (headerName, headerValue) => {
        const headers = {};
        headers[headerName] = headerValue;
        return Ember.$.ajax({
          url: ENV.host + '/api/switchapps/' + graph_id + '/tosca',
          type: 'GET',
          dataType: 'json',
          headers: headers
        }).then(function (json) {
          var doc = json2yaml(json['data']).decodeEscapeSequence();
          $.fn.toscaEditor().getDoc().setValue(doc);
        }, function(xhr, status, error) {
          $.fn.handleAjaxError(xhr, status, error);
        });
      });
    },

    saveJson: function(graph_id) {

    }
  }
});
