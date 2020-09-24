from django.db import models

# Create your models here.
class Room(models.Model):
    ID = models.AutoField(primary_key=True, auto_created=True, editable=False)
    VER_MEM = models.IntegerField(default=0)
    VER_LOC = models.IntegerField(default=0)

    def __str__(self):
        return str(self.ID)


class Item(models.Model):
    NAME = models.CharField(max_length=255)
    ADDRESS = models.CharField(max_length=500)
    ROOM_INSTANCE = models.ForeignKey(Room, on_delete=models.DO_NOTHING)
    LONG = models.FloatField(default=0.0)
    LAT = models.FloatField(default=0.0)
    type_choices = [
        ('mem', 'Member'),
        ('loc', 'Location')
    ]
    TYPE = models.CharField(max_length=3, choices=type_choices, default='mem')

    def __str__(self):
        return str(self.NAME)