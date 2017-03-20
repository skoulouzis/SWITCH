/****************************************************************************/
/*                                Apache License                            */
/*                          Version 2.0, January 2004                       */
/*                       http://www.apache.org/licenses/                    */
/****************************************************************************/
import Ember from 'ember';
import config from './config/environment';

const Router = Ember.Router.extend({
  location: config.locationType
});

Router.map(function () {
  this.route('login');
  this.route('register');
  this.route('dashboard');
  this.route('switchapps', { path: '/applications' } , function () {
    this.route('new');

    this.route('edit', {
      path: ':switchapp_id/properties'
    });

    this.route('dashboard', {
      path: ':switchapp_id/dashboard'
    });

    this.route('tosca', {
      path: ':switchapp_id/tosca'
    });

    this.route('composer', {
      path: ':switchapp_id/composer'
    });
  });
  this.route('switchappinstances', { path: '/instances' } , function () {
    this.route('new');

    this.route('edit', {
      path: ':switchappinstance_id/properties'
    });

    this.route('dashboard', {
      path: ':switchappinstance_id/dashboard'
    });

    this.route('tosca', {
      path: ':switchappinstance_id/tosca'
    });

    this.route('composer', {
      path: ':switchappinstance_id/composer'
    });

    this.route('asap-composer', {
      path: ':switchappinstance_id/asap-composer'
    });
  });
  this.route('switchcomponents', { path: '/components' } , function () {
    this.route('new');

    this.route('edit', {
      path: ':switchcomponent_id/properties'
    });

    this.route('composer', {
      path: ':switchcomponent_id/composer'
    });
  });
  this.route('switchtemplates', { path: '/templates' } , function () {
    this.route('new');

    this.route('edit', {
      path: ':switchcomponenttype_id/properties'
    });
  });
  this.route('loading');
  this.route('settings');
  this.route('userprofile');
});

export default Router;
