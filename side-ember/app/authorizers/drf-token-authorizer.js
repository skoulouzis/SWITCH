/****************************************************************************/
/*                                Apache License                            */
/*                          Version 2.0, January 2004                       */
/*                       http://www.apache.org/licenses/                    */
/****************************************************************************/
import Ember from 'ember';
import Base from 'ember-simple-auth/authorizers/base';

export default Base.extend({
  session: Ember.inject.service('session'),

  authorize: function(sessionData, block) {
    if (this.get('session.isAuthenticated') && !Ember.isEmpty(sessionData.token)) {
      block('Authorization', 'Token ' + sessionData.token);
    }
  }
});
