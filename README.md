# Compilador
Autor: Edison Chung   
Institución: Escuela Politécnica Nacional  
Descripción: Compilador programado en Java, cuya entrada es el archivo input.txt y produce una salida en assembly.asm  
Modo de uso: Crear un archivo input.txt con el código a ser analizado y colocarlo en una carpeta junto con Compiladorv1.0.jar
y el archivo handles.txt  
-- O -- Importar la carpeta completa Compilador como un proyecto de Eclipse, y modificar el archivo input.txt ya incluido.
Al ejecutar el compilador se genera un archivo assembly.asm que contiene el código traducido al lenguaje ensamblador
descrito en el libro Engineering a Compiler de Keith D. Cooper y Linda Torczon.
Además se generan:
output.csv - tokens encontrados con su respectivo token class
simbolos.csv - variables globales y funciones encontradas junto con su retorno
-- NOTA: Si se usa el ejecutable directamente, no habrá consola donde mostrar los errores.  
Código fuente realizado en el IDE Eclipse Java Mars, utilizando JDK 1.8.

