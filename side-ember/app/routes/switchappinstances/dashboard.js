import Ember from 'ember';

export default Ember.Route.extend({
  renderTemplate: function () {
    this.render();

    this.render('switchappinstances.nav', {
      outlet: 'nav',
      into: 'application'
    });
  }
});
