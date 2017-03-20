/****************************************************************************/
/*                                Apache License                            */
/*                          Version 2.0, January 2004                       */
/*                       http://www.apache.org/licenses/                    */
/****************************************************************************/
/* jshint node: true */

module.exports = function (environment) {
  var ENV = {
    modulePrefix: 'side-ember',
    environment: environment,
    baseURL: '/',
    locationType: 'auto',
    host: 'https://somehost.com',
    session: 'session:custom',
    EmberENV: {
      FEATURES: {
        // Here you can enable experimental features on an ember canary build
        // e.g. 'with-controller': true
      },

      EXTEND_PROTOTYPES: {
        Date: false
      }
    },

    APP: {
      // Here you can pass flags/options to your application instance
      // when it is created
    }
  };

  if (environment === 'development') {
    // ENV.APP.LOG_RESOLVER = true;
    // ENV.APP.LOG_ACTIVE_GENERATION = true;
    // ENV.APP.LOG_TRANSITIONS = true;
    // ENV.APP.LOG_TRANSITIONS_INTERNAL = true;
    // ENV.APP.LOG_VIEW_LOOKUPS = true;
    ENV.host = 'http://127.0.0.1:8000';
    ENV.host2 = 'http://193.2.72.90:7070';
  }

  if (environment === 'test') {
    // Testem prefers this...
    ENV.baseURL = '/';
    ENV.locationType = 'none';

    // keep test console output quieter
    ENV.APP.LOG_ACTIVE_GENERATION = false;
    ENV.APP.LOG_VIEW_LOOKUPS = false;

    ENV.APP.rootElement = '#ember-testing';
  }

  if (environment === 'production') {

  }

  ENV['ember-simple-auth'] = {
    authenticationRoute: 'login',
    routeAfterAuthentication: 'index',
    routeIfAlreadyAuthenticated: 'index'
  };

  return ENV;
};
