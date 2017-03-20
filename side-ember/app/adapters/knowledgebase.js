/****************************************************************************/
/*                                Apache License                            */
/*                          Version 2.0, January 2004                       */
/*                       http://www.apache.org/licenses/                    */
/****************************************************************************/
import DS from 'ember-data';
import ENV from 'side-ember/config/environment';
import DataAdapterMixin from 'ember-simple-auth/mixins/data-adapter-mixin';
import App from 'side-ember/app';

export default DS.JSONAPIAdapter.extend(DataAdapterMixin, {
  host: ENV.host2,
  namespace: 'SWITCH/rest/asap',
  authorizer: 'authorizer:drf-token-authorizer'
});
