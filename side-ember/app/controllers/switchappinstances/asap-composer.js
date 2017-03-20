/****************************************************************************/
/*                                Apache License                            */
/*                          Version 2.0, January 2004                       */
/*                       http://www.apache.org/licenses/                    */
/****************************************************************************/
import Ember from 'ember';
import App from './../../app';

export default Ember.Controller.extend({
  sessionAccount: Ember.inject.service('session-account'),

  components: Ember.computed(function () {
    return this.store.query('switchcomponent', {
      is_template_component: true
    });
  }),
  init: function () {
    this.set('deploy', App.deploy);
    this.set('ips', App.ips);
  },
  // just in case
  willUpdate: function () {
    this.set('deploy', App.deploy);
    this.set('ips', App.ips);
  },
  // just in case
  willRender: function () {
    this.set('deploy', App.deploy);
    this.set('ips', App.ips);
  },
  // just in case
  didUpdate: function () {
    this.set('deploy', App.deploy);
    this.set('ips', App.ips);
  },
  actions: {
    update_deployment_parameters: function () {
      alert("executed update_deployment_parameters action");
      this.set('deploy', App.deploy);
      this.set('ips', App.ips);
      var deploy = this.get('deploy');
      var ips = this.get('ips');
      this.transitionToRoute('deploy', {query: deploy});
      this.transitionToRoute('ips', {query: ips});
    }
  },
  componentTypes: Ember.computed(function () {
    return this.store.findAll('switchcomponenttype');
  }),
  component: null,
  graph_type: 'app',
  graph_endpoint: 'switchappinstances'
});
