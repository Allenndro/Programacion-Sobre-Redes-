# Comercio TCP

Chatbot de soporte y consultas de stock por consola. El servidor acepta una sesión a la vez y vuelve a esperar al siguiente cliente cuando termina. El código requiere JDK 17 y usa las bibliotecas de Java.

## Ejecutar en Spring Tool Suite

1. Importá la carpeta como **General → Existing Projects into Workspace**.
2. En el equipo servidor, ejecutá `Servidor.java` con **Run As → Java Application**. Ingresá la IPv4 local del equipo, el puerto (por defecto `7777`), una frase de canal de al menos 12 caracteres y una contraseña de acceso independiente.
3. En la segunda computadora, ejecutá `Cliente.java`. Ingresá la IPv4 del servidor, el mismo puerto, tu nombre y apellido, y la misma frase de canal. Los dos equipos deben poder comunicarse por TCP.
4. Usá `autenticar` e ingresá la contraseña de acceso cuando el cliente la pida. Para probar ambos programas en una misma PC, podés aceptar la IP predeterminada `127.0.0.1`.

En la consola integrada de Spring Tool Suite, la entrada de texto puede verse al escribir. Para la prueba que vas a capturar, podés ejecutar Java desde una terminal para que las contraseñas se ingresen de forma oculta. Desde la carpeta del proyecto en PowerShell, compilá una vez:

```powershell
New-Item -ItemType Directory -Force bin | Out-Null
$fuentes = (Get-ChildItem src/comerciotcp/*.java).FullName
javac --release 17 -encoding UTF-8 -d bin $fuentes
```

En el servidor, ejecutá `java -cp bin comerciotcp.Servidor`. En el cliente, ejecutá `java -cp bin comerciotcp.Cliente`. La presentación «Hola, mi nombre es Alejandro Alarcón» se imprime en ambas consolas al conectar si aceptás el nombre predeterminado.

## Comandos

| Entrada | Resultado |
|---|---|
| `autenticar` | Solicita y valida la contraseña de acceso. |
| `ayuda` | Muestra el menú de soporte. Las opciones 1, 2 y 3 tienen respuestas precargadas. |
| `ver stock de un producto` | Solicita el producto. También admite `ver stock de un producto teclado`. |
| `subir comprobante` | Solicita la ruta de un archivo de hasta 10 MB. El servidor lo guarda en `comprobantes/`. |
| `ver historial de chat` | Muestra hasta 30 entradas recientes de la sesión actual. |
| `salir` | Termina la sesión y libera la conexión. |

El stock y la carga de archivos requieren autenticación. Inventario de prueba: teclado (12), mouse (20), monitor (5), auriculares (0) y webcam (8).

## Prueba y entrega

Enviá «Hola, tengo un problema con mi pedido», probá las opciones del menú, autenticate, consultá stock, subí un comprobante de prueba, revisá el historial y salí. Sacá una captura de cada consola donde se vea tu nombre en el chat. Subí el código a GitHub y adjuntá las capturas en Classroom. Evitá que las capturas muestren las frases o contraseñas de acceso.
