from rest_framework import serializers

from .models import Itens,ItensCentro,Centro


class ItensSerializer(serializers.ModelSerializer):
    class Meta:
        model = Itens
        fields = '__all__'
class ItensCentroSerializer(serializers.ModelSerializer):
    class Meta:
        model = ItensCentro
        fields = '__all__'
class CentroSerializer(serializers.ModelSerializer):
    class Meta:
        model = Centro
        fields = '__all__'
