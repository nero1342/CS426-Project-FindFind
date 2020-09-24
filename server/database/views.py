from django.shortcuts import render
from django.http import HttpResponse
from .models import Room, Item

import json

# Create your views here.
def home(request):
    return HttpResponse('Authorized by Khoa N. A. Nguyen and E-Ro Nguyen')


def create_room(request):
    if request.method == 'GET':
        # Add new room to table Room in database
        new_room = Room()
        new_room.save()
        return HttpResponse(new_room.ID)
    else:
        return HttpResponse('Method not supported')


def check_room_valid(request):
    if request.method == 'GET':
        # Check if there exists such a room with that ID or not
        id_sent = int(request.GET['ID'])
        rooms = Room.objects.filter(ID=id_sent)
        if len(rooms) == 0:
            return HttpResponse(-1)
        else:
            return HttpResponse(id_sent)
    else:
        return HttpResponse('Method not supported')


def add_item(request):
    if request.method == 'GET':
        # Get the JSON information of user entering the room
        json_str = str(request.GET['Info'])
        info = json.loads(json_str)
        location = info['locationInfo']['location']

        # Get type: Member or Place
        tp = str(request.GET['Type'])

        # Get the room instance
        room_id = int(request.GET['RoomID'])
        room = Room.objects.get(ID=room_id)
        # Update room version
        if tp == 'mem':
            room.VER_MEM += 1
        else:
            room.VER_LOC += 1
        room.save()

        # Add data of user to table Item in database
        new_item = Item(
            NAME=info['name'],
            ADDRESS=info['address'],
            ROOM_INSTANCE=room,
            LONG=location['longitude'],
            LAT=location['latitude'],
            TYPE=tp
        )
        new_item.save()
        return HttpResponse(new_item.id)
    else:
        return HttpResponse('Method not supported')


def remove_item(request):
    if request.method == 'GET':
        # Remove the given ID from table Item in database
        id_sent = int(request.GET['ID'])
        i = Item.objects.get(pk=id_sent)
        room_id = i.ROOM_INSTANCE.ID
        tp = i.TYPE
        i.delete()
        # Update room version
        room = Room.objects.get(ID=room_id)
        if tp == 'mem':
            room.VER_MEM += 1
        else:
            room.VER_LOC += 1
        room.save()
        
        # Remove the room from table Room in database if no members there
        members = Item.objects.filter(ROOM_INSTANCE__ID=room_id, TYPE='mem')
        if (len(members) == 0):
            Item.objects.filter(ROOM_INSTANCE__ID=room_id).delete()
            Room.objects.get(ID=room_id).delete()
        return HttpResponse(id_sent)
    else:
        return HttpResponse('Method not supported')


def get_items(request):
    if request.method == 'GET':
        # Get room ID and type of items for retrieving 
        room_id = int(request.GET['RoomID'])
        tp = str(request.GET['Type'])

        # Retrieve items 
        items = Item.objects.filter(ROOM_INSTANCE__ID=room_id, TYPE=tp)
        res = []
        for item in items:
            res.append({
                'address': item.ADDRESS,
                'locationInfo': {
                    'desc': item.ADDRESS,
                    'location': {
                        'latitude': item.LAT,
                        'longitude': item.LONG
                    }
                },
                'name': item.NAME,
                'id': item.id
            })
        return HttpResponse(json.dumps(res))
    else:
        return HttpResponse('Method not supported')


def get_ver(request):
    if request.method == 'GET':
        # Get room ID
        room_id = int(request.GET['RoomID'])
        tp = str(request.GET['Type'])

        # Retrieve version
        room = Room.objects.get(ID=room_id)
        res = room.VER_MEM if tp == 'mem' else room.VER_LOC
        return HttpResponse(res)
    else:
        return HttpResponse('Method not supported')