import Ember from 'ember';

export default Ember.Controller.extend({
  session: Ember.inject.service('session'),
  sessionAccount: Ember.inject.service('session-account'),
  showRightBar: false,
  showLeftBar: false,
  showNotificationModal: false,
  showLoadingModal: false,
  waitingForResponse: true,
  showSshConsole: false,
  component: null,
  components: null,
  pollModel: null,
  newNotifications: Ember.A([]),

  filteredComponents: Ember.computed('components', 'searchTerm', function() {
    var keyword = this.get('searchTerm');
    var filtered = this.get('components');
    if (keyword) {
      keyword = keyword.toLowerCase().trim();
      filtered = this.get('components').filter((item) => item.get('title').toLowerCase().includes(keyword));
    }
    return filtered;
  }),

  actions: {
    invalidateSession() {
      this.get('session').invalidate();
    }
  }

});
