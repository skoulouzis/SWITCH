import { moduleForModel, test } from 'ember-qunit';

moduleForModel('switchdocument', 'Unit | Model | switchdocument', {
  // Specify the other units that are required for this test.
  needs: []
});

test('it exists', function(assert) {
  //let model = this.subject();
  let store = this.store();
  let documents = store.findAll('switchdocument');
  //assert.ok(!!model);
  console.log ("Number of documnents = " + documents.length());
  assert(documents.length() === 16);
});
