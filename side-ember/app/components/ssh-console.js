/****************************************************************************/
/*                                Apache License                            */
/*                          Version 2.0, January 2004                       */
/*                       http://www.apache.org/licenses/                    */
/****************************************************************************/
import Ember from 'ember';

export default Ember.Component.extend({

  socketRef: null,

  actions: {
    hideSshConsole: function () {
      this.sendAction('hideSshConsole');
      var socket = this.get('socketRef');
      socket.disconnect();
    }
  },

  didRender() {
    this._super(...arguments);

    var ember_app = this;

    var vm_properties = yaml.load(this.get("component").get('properties'));

    // Connect to the socket.io server
    var socket = io.connect("http://"+ vm_properties.public_address + ":9090");

    //Set the socket reference in the component, so we can disconnect the socket when the component is destroyed
    this.set('socketRef', socket);

    // Wait for data from the server
    socket.on('output', function (data) {
      // Insert some line breaks where they belong
      data = data.replace("\n", "<br>");
      data = data.replace("\r", "<br>");
      // Append the data to our terminal
      $('.terminal').append(data);
    });

    // Listen for user input and pass it to the server
    $(document).on("keypress",function(e){
      var char = String.fromCharCode(e.which);
      socket.emit("input", char);
    });
  },

  willDestroyElement(params) {
    this._super(...arguments);
    this.send('hideSshConsole');
  },
});
