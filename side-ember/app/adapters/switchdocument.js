import Ember from 'ember';
import DS from 'ember-data';
import ENV from 'side-ember/config/environment';
import DataAdapterMixin from 'ember-simple-auth/mixins/data-adapter-mixin';
import FormDataAdapterMixin from 'ember-cli-form-data/mixins/form-data-adapter';

export default DS.JSONAPIAdapter.extend(DataAdapterMixin, FormDataAdapterMixin, {
  host: ENV.host,
  namespace: 'api',
  authorizer: 'authorizer:drf-token-authorizer',

  // Overwrite to change the request types on which Form Data is sent
  formDataTypes: ['POST', 'PUT', 'PATCH'],

  ajaxOptions: function(url, type, options){
    var data;

    if (options && 'data' in options){
      data = options.data;
    }

    var hash = this._super.apply(this, arguments);

    if(typeof FormData === 'function' && data && this.formDataTypes.includes(type)){
      var formData, root;

      formData = new FormData();

      var objData = data.data;
      root = Ember.keys(objData)[0];

      Ember.keys(objData[root]).forEach(function(key){
        if(Ember.isPresent(objData[root][key])){
          formData.append(key, objData[root][key]);
        }
      });

      hash.processData = false;
      hash.contentType = false;
      hash.data = formData;
    }

    return hash;
  }
});
