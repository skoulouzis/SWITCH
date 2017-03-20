/****************************************************************************/
/*                                Apache License                            */
/*                          Version 2.0, January 2004                       */
/*                       http://www.apache.org/licenses/                    */
/****************************************************************************/
import DS from 'ember-data';
import ENV from 'side-ember/config/environment';
import DataAdapterMixin from 'ember-simple-auth/mixins/data-adapter-mixin';
import KnowledgeBaseAdapter from './knowledgebase';

export default KnowledgeBaseAdapter.extend(DataAdapterMixin, {

});
