from django.shortcuts import render

# Create your views here.


from django.shortcuts import render
from rest_framework import viewsets, generics

# Create your views here.
from .models import ItensCentro,Itens,Centro
from .serializers import ItensSerializer,ItensCentroSerializer,CentroSerializer


class ItensViewSet(viewsets.ModelViewSet):
    #authentication_classes = [SessionAuthentication, BasicAuthentication]
    #permission_classes = [DjangoModelPermissionsOrAnonReadOnly]
    #permission_classes = [permissions.AllowAny]

    queryset = Itens.objects.all()
    serializer_class = ItensSerializer
    http_method_names = ['get', 'post', 'put', 'path','delete']
class ItensCentroViewSet(viewsets.ModelViewSet):
    #authentication_classes = [SessionAuthentication, BasicAuthentication]
    #permission_classes = [DjangoModelPermissionsOrAnonReadOnly]
    #permission_classes = [permissions.AllowAny]

    queryset = ItensCentro.objects.all()
    serializer_class = ItensCentroSerializer
    http_method_names = ['get', 'post', 'put', 'path','delete']

class CentroViewSet(viewsets.ModelViewSet):
    #authentication_classes = [SessionAuthentication, BasicAuthentication]
    #permission_classes = [DjangoModelPermissionsOrAnonReadOnly]
    #permission_classes = [permissions.AllowAny]

    queryset = Centro.objects.all()
    serializer_class = CentroSerializer
    http_method_names = ['get', 'post', 'put', 'path','delete']