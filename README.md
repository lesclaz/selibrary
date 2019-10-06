selibrary
=========
## La libreria de SuitETECSA

selibrary fue creada para la [Comunidad Android de Cuba](https://jorgen.cubava.cu/), para facilitar el desarrollo de
aplicaciones en Java y/o Android que interactuen con el [Portal de Usuario](https://www.portal.nauta.cu/),
y el [Portal Cautivo](https://secure.etecsa.net:8443/) de nauta; asi como el
[Portal Mi Cubacel](https://mi.cubacel.net), ahorrandoles tiempo, esfuerzos, neuronas y codigo a los desarrolladores.
 
selibrary pretende ser no solo multiplataforma, sino tambien multilenguaje, echele un vistazo a
[Pyselibrary](https://gitlab.home.asr/marilasoft/Pyselibrary/); la misma libreria escrita en Python.
Esta, la version en Java usa la libreria [Jsoup](https://jsoup.org/) para el procesamiento de paginas web (los portales
de [ETECSA](http://www.etecsa.cu)), mientras que la version en Python usa
[BeautifulSoup4](http://www.crummy.com/software/BeautifulSoup/bs4/).

Por el momento selibrary a logrado implementar 10 funciones que representan el 100% de
las operaciones que permite realizar el [Portal de Usuario](https://www.portal.nauta.cu/) nauta en las cuentas no
asociadas a Nauta Hogar, estas son:
* Iniciar session.
* Obtener la información de la cuenta.
* Recargar la cuenta.
* Transferir saldo a otra cuenta nauta.
* Cambiar la contraseña de la cuenta.
* Cambiar la contraseña de la cuenta de correo asociada.
* Obtener el histórico de conexiones por meses.
* Obtener el histórico de recargas por meses.
* Obtener el histórico de transferencias por meses.
* Cerrar session.

Aún falta por implementar:
* Pagar servicio de Nauta Hogar (`en cuentas asociadas a este servicio`).

Mientras que la clase CaptivePortal, la encargada de interactuar con el 
[Portal Cautivo](https://secure.etecsa.net:8443/) de nauta, provee las siguientes funciones:
* Iniciar Session.
* Actualizar tiempo disponible.
* Cerrar session.
* Obtener informacion del usuario.
* Acceder a los terminos de uso.


## Ejemplos:

### Iniciando session con UserPortal

```java
...
import cu.marilasoft.selibrary.UserPortal;
...

    UserPortal session = new UserPortal();
    String userName = "usuario@nauta.com.cu";
    String password = "contraseña";
    ...
    // descarga la imagen CAPTCHA desde session.captchaImg
    ...
    String captchaCode;
    // pidiendo y almacenando el codigo CAPTCHA
    System.out.println("Introduzca el codigo de la imagen captcha: ");
    Scanner teclado = new Scanner (System.in);
    captchaCode = teclado.nextLine();
    // la funfion login devuelve un entero, cero (0) si todo salio bien y uno (1)
    // si hubieron errores
    try {
        int loginStatus = session.login(userName, password, captchaCode);
        if (loginStatus == 0) {
          // imprime el saldo de la cuanta logeada
          System.out.print(session.credit());
        } else {
          for (String error : session.errors {
            // imprime los errores encontrados
            for(Object error : session.status.get("msg")) {
                System.out.println(error);
            }
          }
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
```

### Iniciando session con CaptivePortal

```java
...
import cu.marilasoft.selibrary.CaptivePortal;
...

CaptivePortal session = new CaptivePortal();
String userName = "usuario@nauta.com.cu";
String password = "contraseña";
    try {
            captivePortal.preLogin();
            cookies = captivePortal.cookies();
            Map<String, Object> sessionParameters;
            int login = loginPortal.login(userName,
                    password,
                    cookies);
            if (login != 1) {
                sessionParameters = loginPortal.sessionParameters();
                System.out.println(loginPortal.updateAvailableTime(
                        (String) sessionParameters.get("updateTimeUrl"),
                        cookies));
                Thread.sleep(6);
                int logout = loginPortal.logout((String) sessionParameters.get("logoutUrl"), cookies);
                if (login != 1) {
                    System.out.println((String) loginPortal.status().get("msg"));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
```
