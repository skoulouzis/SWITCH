/****************************************************************************/
/*                                Apache License                            */
/*                          Version 2.0, January 2004                       */
/*                       http://www.apache.org/licenses/                    */
/****************************************************************************/
import Ember from 'ember';
import ENV from 'side-ember/config/environment';
import hbs from 'htmlbars-inline-precompile';
import CanvasRoute from './../canvas';

export default CanvasRoute.extend({

  renderTemplate: function () {
    this.render();

    this.render('switchappinstances.nav', {
      outlet: 'nav',
      into: 'application'
    });

    this.send('updatePoll', this.currentModel);
  }
});
