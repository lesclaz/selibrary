selibrary
=========
## Una librería de SuitETECSA

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

La clase MCPortal es la encargada de interactuar con el [Portal Mi Cubacel](https://mi.cubacel.net),
y hasta el momento solo es capaz de logearse y obtener alguna informacion del usuario.
* Inicia session.
* Recupera la informacion siguiente:
    * Numero de telefono.
    * Saldo.
    * Fecha de expiracion del saldo.
    * Fecha en la que se utilizo el servicio 'Adelanta Saldo' (si aun debe el saldo adelantado).
    * Saldo por pagar (si aun debe el saldo adelantado).
    * Numeros asociados al servicio 'Plan Amigo' (de existir estos).


## Ejemplos:

### Iniciando session con UserPortal

```java
import cu.marilasoft.selibrary.UserPortal;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Test {

    private static void downloadCaptcha(byte[] captcha) {
        try {
            ByteArrayOutputStream out;
            try (InputStream in = new BufferedInputStream(new ByteArrayInputStream(captcha))) {
                out = new ByteArrayOutputStream();
                byte[] buf = new byte[1024];
                int n;
                while (-1 != (n = in.read(buf))) {
                    out.write(buf, 0, n);
                }
                out.close();
            }
            byte[] response = out.toByteArray();
            String ubicacion = "Captcha.png";
            try (FileOutputStream fos = new FileOutputStream(ubicacion)) {
                fos.write(response);
            }
        } catch (IOException ex) {
            ex.printStackTrace(System.out);
        }
    }

    public static void main(String[] args) {
        UserPortal userPortal = new UserPortal();
        Map<String, String> cookies;
        try {
            userPortal.preLogin();
            cookies = userPortal.cookies();
            userPortal.loadCAPTCHA(cookies);
            downloadCaptcha(userPortal.captchaImg());
            String userName = "usuario@nauta.com.cu";
            String password = "password";
            String captchaCode;
            System.out.println("Introduzca el codigo de la imagen captcha: ");
            @SuppressWarnings("resource")
            Scanner teclado = new Scanner(System.in);
            captchaCode = teclado.nextLine();
            int loged = userPortal.login(userName, password, captchaCode, cookies);
            if (loged == 0) {
                System.out.println("Nombre de usuario: " + userPortal.userName());
                System.out.println("Fecha de bloque: " + userPortal.blockDate());
                System.out.println("Fecha de eliminacion: " + userPortal.delDate());
                System.out.println("Tipo de cuenta: " + userPortal.accountType());
                System.out.println("Tipo de servicio: " + userPortal.serviceType());
                System.out.println("Saldo disponible: " + userPortal.credit());
                System.out.println("Tiempo disponible: " + userPortal.time());
                System.out.println("Cuenta de correo: " + userPortal.mailAccount());
            } else {
                System.out.println("Se han encontrado errores al iniciar session: ");
                for (Object error : (List) userPortal.status().get("msg")) {
                    System.out.println(error + ".");
                }
                System.exit(0);
            }
        } catch (IOException e) {
            System.out.println("Hubieron errores al cargar...");
            e.printStackTrace();
        }
    }
}
```

### Iniciando session con CaptivePortal

```java
import cu.marilasoft.selibrary.CaptivePortal;

import java.io.IOException;
import java.util.Map;

public class Test {

    public static void main(String[] args) {
        CaptivePortal captivePortal = new CaptivePortal();
        try {
            captivePortal.preLogin();
            String userName = "user@nauta.com.cu";
            String password = "password";
            Map<String, String> cookies = captivePortal.cookies();
            int loged = captivePortal.login(userName, password, cookies);
            if (loged == 0) {
                Map<String, Object> sessionParameters = captivePortal.sessionParameters();
                String udpateTimeUrl = (String) sessionParameters.get("updateTimeUrl");
                System.out.println(captivePortal.updateAvailableTime(udpateTimeUrl, cookies));
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                int logout = captivePortal.logout((String) sessionParameters.get("logoutUrl"), cookies);
                if (logout == 0) {
                    System.out.println("SUCCESS: " + captivePortal.status().get("msg"));
                } else {
                    System.out.println("ERROR: " + captivePortal.status().get("msg"));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

### Iniciando session con MCPortal

```java
import cu.marilasoft.selibrary.MCPortal;

import java.io.IOException;

public class Test {

    public static void main(String[] args) {
        MCPortal mcPortal = new MCPortal();
        try {
            mcPortal.login("55555555", "password");
            System.out.println(mcPortal.credit());
            System.out.println(mcPortal.phoneNumber());
            System.out.println(mcPortal.expire());
            System.out.println(mcPortal.date());
            System.out.println(mcPortal.payableBalance());
            System.out.println(mcPortal.phoneNumberOne());
            System.out.println(mcPortal.phoneNumberTwo());
            System.out.println(mcPortal.phoneNumberTree());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```
