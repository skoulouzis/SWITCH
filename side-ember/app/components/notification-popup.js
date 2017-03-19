import Ember from 'ember';

export default Ember.Component.extend({
  didInsertElement: function() {
    this._super(...arguments);
    var message = this.get('item').get('message');
    var title = this.get('item').get('title');
    //alert("hello world");

    $.notify({
      message: "<b>" + title + "</b><br>" + message
    },{
      // settings
      offset: 50,
      spacing: 10,
      z_index: 1031,
      delay: 8000,
      timer: 1000,
      url_target: '_blank',
      mouse_over: null,
      animate: {
        enter: 'animated fadeInDown',
        exit: 'animated fadeOutUp'
      },
      type: 'danger',
      newest_on_top: false
    });
  }
});
