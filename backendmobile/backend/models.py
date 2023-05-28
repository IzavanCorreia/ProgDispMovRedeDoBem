from django.db import models
from django.contrib.auth.models import User

# Create your models here.


class Itens(models.Model):
    nome = models.CharField(max_length=50)


class Centro(models.Model):
    nome = models.CharField(max_length=50)
    latitude = models.DecimalField(max_digits=10, decimal_places=5, default=0)
    longitude = models.DecimalField(max_digits=10, decimal_places=5, default=0)
    usuario = models.ForeignKey(User, models.CASCADE)
    CentroOuPessoa = models.BooleanField(default=False)

    #False é uma pessoa, True é um centro


class ItensCentro(models.Model):
    centro = models.ForeignKey(Centro, models.CASCADE)
    itens = models.ForeignKey(Itens, models.CASCADE)
