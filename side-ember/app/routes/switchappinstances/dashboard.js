/****************************************************************************/
/*                                Apache License                            */
/*                          Version 2.0, January 2004                       */
/*                       http://www.apache.org/licenses/                    */
/****************************************************************************/
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
