import Ember from "ember";

export default Ember.Component.extend({
  content: null,
  selectedValue: null,

  init(attrs) {
    this._super(...arguments);
    var content = this.get('content');

    if (!content) {
      this.set('content', []);
    }

    var selectedValue = this.get('selectedValue');

    if (!selectedValue) {
      this.set('selectedValue', null);
    }
  },

  actions: {
    change() {
      const changeAction = this.get('action');
      const selectedEl = this.$('select')[0];
      const selectedIndex = selectedEl.selectedIndex;
      const content = this.get('content');

      if (selectedIndex > 0) {
        const selectedValue = content.objectAt(selectedIndex - 1);
        this.set('selectedValue', selectedValue);
        changeAction(selectedValue);
      } else {
        const selectedValue = null;
        this.set('selectedValue', selectedValue);
        changeAction(selectedValue);
      }
    }
  }
});
