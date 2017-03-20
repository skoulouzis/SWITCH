/****************************************************************************/
/*                                Apache License                            */
/*                          Version 2.0, January 2004                       */
/*                       http://www.apache.org/licenses/                    */
/****************************************************************************/
import Ember from 'ember';
import ENV from 'side-ember/config/environment';
 
export default Ember.Controller.extend({
 
  actions: {
    register() {
      let {username, email, password, confirm_password} = this.getProperties(
        'username',
        'email',
        'password',
        'confirm_password'
      );
 
      Ember.$.ajax({
        url: ENV.host + '/api-register/',
        type: 'POST',
        data: JSON.stringify({
          username: username,
          email: email,
          password: password,
          confirm_password: confirm_password
        }),
        contentType: 'application/json;charset=utf-8',
        dataType: 'json'
      }).then((response) => {
        this.set('signupComplete', true);
      }, (xhr, status, error) => {
        this.set('error', xhr.responseText);
      });
    }
  }
});
