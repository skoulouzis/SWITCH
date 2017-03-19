import DS from 'ember-data';

export default DS.Store.extend({
  init: function  () {
    return this._super.apply(this, arguments);
  },

  /*
  findAll: function() {
    var objQuery = {};
    //In case of calling findAll with param 'reload:true,' we should include it in the query
    if (arguments.length > 1){
      objQuery = arguments[1];
    }
    objQuery['page_size'] = "max";
    return this.query(arguments[0],objQuery);
  },
  */

  query: function() {
    var objQuery = {};
    if (arguments.length > 1){
      objQuery = arguments[1];
    }
    if (!objQuery['page_size']){
      objQuery['page_size'] = "max";
    }
    return this._super(arguments[0],objQuery);
  },
});
