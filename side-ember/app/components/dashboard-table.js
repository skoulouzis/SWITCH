/****************************************************************************/
/*                                Apache License                            */
/*                          Version 2.0, January 2004                       */
/*                       http://www.apache.org/licenses/                    */
/****************************************************************************/
import Ember from 'ember';

export default Ember.Component.extend({
  didRender() {
    this._super(...arguments);
    this.$('#dashboard-table').DataTable();
  }
});
