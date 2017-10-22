# tarea1distribuidos
localhost: 127.0.0.1
multicast: 230.0.0.1

Nicolas Alarcon
Rodrigo Elicer

* Abrir mainServer
```
AGREGAR DISTRITO
[Servidor Central] Nombre Distrito:
Trost
[Servidor Central] IP Multicast:
230.0.0.1
[Servidor Central] Puerto Multicast:
5555
[Servidor Central] IP Peticiones:
127.0.0.1
[Servidor Central] Puerto Peticiones"
6789
¿Desea seguir agregando distritos?[y/n]
y
AGREGAR DISTRITO
[Servidor Central] Nombre Distrito:
Panchitolandia
[Servidor Central] IP Multicast:
231.0.0.1
[Servidor Central] Puerto Multicast:
5556
[Servidor Central] IP Peticiones:
127.0.0.1
[Servidor Central] Puerto Peticiones"
6788
¿Desea seguir agregando distritos?[y/n]
n
```

* Abrir districtServer 1
```
[Distrito] Nombre Distrito:
Trost
[Distrito Trost] IP Multicast:
230.0.0.1
[Distrito Trost] Puerto Multicast:
5555
[Servidor Central] IP Peticiones:
127.0.0.1
[Distrito Trost] Puerto Peticiones:
6789
```

* Abrir districtServer 2
```
[Distrito] Nombre Distrito:
Panchitolandia
[Distrito Trost] IP Multicast:
231.0.0.1
[Distrito Trost] Puerto Multicast:
5556
[Servidor Central] IP Peticiones:
127.0.0.1
[Distrito Trost] Puerto Peticiones:
6788
```

* Abrir cliente 1
```
[Cliente] Ingresar IP Servidor Central:
127.0.0.1
[Cliente] Ingresar Puerto Servidor Central:
4445
[Cliente] Introducir Nombre de Distrito a Investigar:
Trost
```

* Abrir cliente 2
```
[Cliente] Ingresar IP Servidor Central:
127.0.0.1
[Cliente] Ingresar Puerto Servidor Central:
4445
[Cliente] Introducir Nombre de Distrito a Investigar:
Panchitolandia
```
