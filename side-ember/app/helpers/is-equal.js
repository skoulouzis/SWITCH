/****************************************************************************/
/*                                Apache License                            */
/*                          Version 2.0, January 2004                       */
/*                       http://www.apache.org/licenses/                    */
/****************************************************************************/
import Ember from "ember";

export default Ember.Helper.helper(function([leftSide, rightSide]) {
  return leftSide === rightSide;
});
