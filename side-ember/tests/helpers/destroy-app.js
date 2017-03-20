/****************************************************************************/
/*                                Apache License                            */
/*                          Version 2.0, January 2004                       */
/*                       http://www.apache.org/licenses/                    */
/****************************************************************************/
import Ember from 'ember';

export default function destroyApp(application) {
  Ember.run(application, 'destroy');
  server.shutdown();
}
