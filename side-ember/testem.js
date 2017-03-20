/****************************************************************************/
/*                                Apache License                            */
/*                          Version 2.0, January 2004                       */
/*                       http://www.apache.org/licenses/                    */
/****************************************************************************/
/*jshint node:true*/
module.exports = {
  "framework": "qunit",
  "test_page": "tests/index.html?hidepassed",
  "disable_watching": true,
  "launch_in_ci": [
    "PhantomJS"
  ],
  "launch_in_dev": [
    "PhantomJS",
    "Chrome"
  ]
};
