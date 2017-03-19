import DS from 'ember-data';
import ENV from 'side-ember/config/environment';
import DataAdapterMixin from 'ember-simple-auth/mixins/data-adapter-mixin';

export default DS.JSONAPIAdapter.extend(DataAdapterMixin, {
  host: ENV.host,
  namespace: 'api',
  authorizer: 'authorizer:drf-token-authorizer'
});
