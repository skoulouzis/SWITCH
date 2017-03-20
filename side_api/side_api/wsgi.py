##############################################################################
#                                Apache License                              #
#                          Version 2.0, January 2004                         #
#                       http://www.apache.org/licenses/                      #
##############################################################################
"""
WSGI config for side_api project.

It exposes the WSGI callable as a module-level variable named ``application``.

For more information on this file, see
https://docs.djangoproject.com/en/1.8/howto/deployment/wsgi/
"""

import os

from django.core.wsgi import get_wsgi_application

os.environ.setdefault("DJANGO_SETTINGS_MODULE", "side_api.settings")

application = get_wsgi_application()
