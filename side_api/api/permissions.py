##############################################################################
#                                Apache License                              #
#                          Version 2.0, January 2004                         #
#                       http://www.apache.org/licenses/                      #
##############################################################################
from rest_framework import permissions


class BelongsToUser(permissions.BasePermission):
    def has_object_permission(self, request, view, obj):
        return request.user == obj.user


class AppBelongsToUser(permissions.BasePermission):
    def has_object_permission(self, request, view, obj):
        return request.user == obj.app.user