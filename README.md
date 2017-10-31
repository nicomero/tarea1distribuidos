# Tarea 1 Sistemas Distribuidos

Nicolás Alarcón 201473522-7

Rodrigo Elicer  201473539-1

|MÁQUINAS|IP|
|----|----|
|dist07.inf.santiago.usm.cl|10.6.40.157|
|dist08.inf.santiago.usm.cl|10.6.40.158|
|dist09.inf.santiago.usm.cl|10.6.40.159|

## INSTRUCCIONES DE COMPILACIÓN Y EJECUCIÓN

### En cada maquina ejecutar el comando:
```
$ make
```
### Luego ejecutar el comando:
```
$ java <nombre_clase>
```
Los nombres de las clases pueden ser:

|.class|Descripción|
|----|----|
|mainServer|Servidor Central|
|districtServer|Representa a los distritos|
|cliente|Representa a los jugadores|

## EJEMPLO PARA EJECUTAR EN VARIAS MÁQUINAS
#### Primero habrá que conectarse a la red del DI
```
ssh <nombre.apellido>@ssh2.inf.utfsm.cl
```
#### Una vez dentro, habrá que conectarse a las distintas máquinas.
```
ssh root@distXX.inf.santiago.usm.cl
```

* Abrir mainServer en dist07.inf.santiago.usm.cl
```
AGREGAR DISTRITO
[Servidor Central] Nombre Distrito:
Trost
[Servidor Central] IP Multicast:
230.0.0.1
[Servidor Central] Puerto Multicast:
5555
[Servidor Central] IP Peticiones:
10.6.40.158
[Servidor Central] Puerto Peticiones:
6789
¿Desea seguir agregando distritos?[y/n]
n
```

* Abrir districtServer en dist08.inf.santiago.usm.cl
```
[Distrito] Ingresar IP Servidor Central:
10.6.40.157
[Distrito] Ingresar Puerto Servidor Central:
4445
[Distrito] Nombre Distrito:
Trost
[Distrito Trost] IP Multicast:
230.0.0.1
[Distrito Trost] Puerto Multicast:
5555
[Servidor Trost] IP Peticiones:
10.6.40.158
[Distrito Trost] Puerto Peticiones:
6789
```

* Abrir cliente en dist09.inf.santiago.usm.cl
```
[Cliente] Ingresar IP Servidor Central:
10.6.40.157
[Cliente] Ingresar Puerto Servidor Central:
4445
[Cliente] Introducir Nombre de Distrito a Investigar:
Trost
```

## EJEMPLO PARA EJECUTAR EN UNA SOLA MÁQUINA

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
[Distrito] Ingresar IP Servidor Central:
127.0.0.1
[Distrito] Ingresar Puerto Servidor Central:
4445
[Distrito] Nombre Distrito:
Trost
[Distrito Trost] IP Multicast:
230.0.0.1
[Distrito Trost] Puerto Multicast:
5555
[Servidor Trost] IP Peticiones:
127.0.0.1
[Distrito Trost] Puerto Peticiones:
6789
```

* Abrir districtServer 2
```
[Distrito] Ingresar IP Servidor Central:
127.0.0.1
[Distrito] Ingresar Puerto Servidor Central:
4445
[Distrito] Nombre Distrito:
Panchitolandia
[Distrito Panchitolandia] IP Multicast:
231.0.0.1
[Distrito Panchitolandia] Puerto Multicast:
5556
[Servidor Panchitolandia] IP Peticiones:
127.0.0.1
[Distrito Panchitolandia] Puerto Peticiones:
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
