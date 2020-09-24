"""server URL Configuration

The `urlpatterns` list routes URLs to views. For more information please see:
    https://docs.djangoproject.com/en/3.1/topics/http/urls/
Examples:
Function views
    1. Add an import:  from my_app import views
    2. Add a URL to urlpatterns:  path('', views.home, name='home')
Class-based views
    1. Add an import:  from other_app.views import Home
    2. Add a URL to urlpatterns:  path('', Home.as_view(), name='home')
Including another URLconf
    1. Import the include() function: from django.urls import include, path
    2. Add a URL to urlpatterns:  path('blog/', include('blog.urls'))
"""
from django.contrib import admin
from django.urls import path
from django.conf.urls import url

from database import views

urlpatterns = [
    url(r'^$', views.home, name='Home'),

    # Return: Room ID
    url(r'^create', views.create_room, name='Create Room'),

    # Params: 
    #   ID: int
    # Return: ID if valid else -1
    url(r'^check', views.check_room_valid, name='Check Valid Room'),

    # Params: 
    #   Info: JSON string
    #   RoomID: int
    #   Type: 'mem' or 'loc'
    # Return: ID of new item
    url(r'^add', views.add_item, name='Add An Item'),

    # Params:
    #   ID: int
    # Return: ID of that item
    url(r'^remove', views.remove_item, name='Remove An Item'),

    # Params:
    #   RoomID: int
    #   Type: 'mem' or 'loc'
    # Return: JSON string
    url(r'^get', views.get_items, name='Get Items'),

    # Params:
    #   ID: int
    #   Type: 'mem' or 'loc'
    # Return: int
    url(r'^ver', views.get_ver, name='Get Version')
]
