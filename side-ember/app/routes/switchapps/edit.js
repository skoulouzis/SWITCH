import Ember from 'ember';
import SaveModelMixin from 'side-ember/mixins/switchapps/save-model-mixin';
import AuthenticatedRouteMixin from 'ember-simple-auth/mixins/authenticated-route-mixin';

export default Ember.Route.extend(SaveModelMixin, AuthenticatedRouteMixin, {
  renderTemplate: function () {
    this.render();

    this.render('switchapps.nav', {
      outlet: 'nav',
      into: 'application'
    });
  }
});
