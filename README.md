# FileCreator-1
Procesa por consola un fichero csv y conviértelo a xml. Aspectos a destacar:
- Debe ser una aplicación de consola var args.
- Utiliza la metodología TRADICIONAL para generar el xml (sin libreria), a través
de nodos y estructura árbol.
- Procesa el fichero csv utilizando librerias que faciliten la tarea.
- Reto: ¿Puedes hacer que lea CUALQUIER fichero csv?
## Archivos de prueba
El directorio `src/main/resources/CSV` contiene archivos CSV de prueba que se pueden utilizar para unit testing.
## Unit testing
Los archivos de prueba deben ser copiados a la carpeta de usuario. O se debe cambiar la variable RESOURCE_DIRECTORY en el archivo `FileCreatorTests.java` en la carpeta de test.
## Ayuda
````
usage: file-creator-1 
    --first-line-name           indica si la primera linea del archivo son las columnas
    -i,--input <arg>            archivo de entrada
    -l,--line-separator <arg>   separador de lineas ('\n' por defecto
    --lowercase-tags            genera los tags del xml en minúscula
    -o,--output <arg>           archivo de salida
    -s,--separator <arg>        separador de valores (',' por defecto)
    -t,--text-separator <arg>   separador de texto ('"' por defecto)
```