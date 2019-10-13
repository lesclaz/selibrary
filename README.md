selibrary
=========
## Una librería de SuitETECSA

selibrary fue creada para la [Comunidad Android de Cuba](https://jorgen.cubava.cu/), para facilitar el desarrollo de
aplicaciones en Java y/o Android que interactúen con el [Portal de Usuario](https://www.portal.nauta.cu/),
y el [Portal Cautivo](https://secure.etecsa.net:8443/) de nauta; así como el
[Portal Mi Cubacel](https://mi.cubacel.net), ahorrándoles tiempo, esfuerzos, neuronas y código a los desarrolladores.
 
selibrary pretende ser no solo multiplataforma, sino también multilenguaje, échele un vistazo a
[PySELibrary](https://github.com/marilasoft/PySELibrary/); la misma librería escrita en Python.
Esta, la versión en Java usa la librería [Jsoup](https://jsoup.org/) para el procesamiento de paginas web (los portales
de [ETECSA](http://www.etecsa.cu)), mientras que la versión en Python usa
[BeautifulSoup4](http://www.crummy.com/software/BeautifulSoup/bs4/).

Por el momento selibrary a logrado implementar 10 funciones que representan el 100% de
las operaciones que permite realizar el [Portal de Usuario](https://www.portal.nauta.cu/) nauta en las cuentas no
asociadas a Nauta Hogar, estas son:
* Iniciar sesión.
* Obtener la información de la cuenta.
* Recargar la cuenta.
* Transferir saldo a otra cuenta nauta.
* Cambiar la contraseña de la cuenta.
* Cambiar la contraseña de la cuenta de correo asociada.
* Obtener el histórico de conexiones por meses.
* Obtener el histórico de recargas por meses.
* Obtener el histórico de transferencias por meses.
* Cerrar sesión.

Aún falta por implementar:
* Pagar servicio de Nauta Hogar (`en cuentas asociadas a este servicio`).

Mientras que la clase CaptivePortal, la encargada de interactuar con el 
[Portal Cautivo](https://secure.etecsa.net:8443/) de nauta, provee las siguientes funciones:
* Iniciar sesión.
* Actualizar tiempo disponible.
* Cerrar sesión.
* Obtener información del usuario.
* Acceder a los términos de uso.

La clase MCPortal es la encargada de interactuar con el [Portal Mi Cubacel](https://mi.cubacel.net),
y hasta el momento solo es capaz de realizar las siguientes operaciones:
* Inicia sesión.
* Recupera la información siguiente:
    * Número de teléfono.
    * Saldo.
    * Fecha de expiración del saldo.
    * Fecha en la que se utilizó el servicio `Adelanta Saldo` (si aún debe el saldo adelantado).
    * Saldo por pagar (si aún debe el saldo adelantado).
    * Números asociados al servicio 'Plan Amigo' (de existir estos).
    * Estado de la tarifa por consumo.
* Cambia estado de la tarifa por consumo.
* Recupera y compra productos (`paquetes`) (`la compra de paquetes no ha sido probada aún.`)
* Restablece contraseña olvidada.
* Registra un usuario nuevo en el portal.

MCPortal se encuentra en fase experimental, por lo que aún no maneja muchos de los errores que puedan
producirse en el proceso de cualquiera de la operaciones implementadas.


## Ejemplos:

### Iniciando sesión con UserPortal

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
            cookies = userPortal.getCookies();
            userPortal.loadCAPTCHA(cookies);
            downloadCaptcha(userPortal.getCaptchaImg());
            String userName = "user@nauta.com.cu";
            String password = "password";
            String captchaCode;
            System.out.println("Introduzca el código de la imagen captcha: ");
            @SuppressWarnings("resource")
            Scanner keyMap = new Scanner(System.in);
            captchaCode = keyMap.nextLine();
            userPortal.login(userName, password, captchaCode, cookies);
            if (userPortal.getStatus().get("function").equals("login") &&
            userPortal.getStatus().get("status").equals("success")) {
                System.out.println("Nombre de usuario: " + userPortal.getUserName());
                System.out.println("Fecha de bloque: " + userPortal.getBlockDate());
                System.out.println("Fecha de eliminacion: " + userPortal.getDelDate());
                System.out.println("Tipo de cuenta: " + userPortal.getAccountType());
                System.out.println("Tipo de servicio: " + userPortal.getServiceType());
                System.out.println("Saldo disponible: " + userPortal.getCredit());
                System.out.println("Tiempo disponible: " + userPortal.getTime());
                System.out.println("Cuenta de correo: " + userPortal.getMailAccount());
            } else if (userPortal.getStatus().get("function").equals("login") &&
            userPortal.getStatus().get("status").equals("error")) {
                System.out.println("Se han encontrado errores al iniciar sesión: ");
                for (Object error : (List) userPortal.getStatus().get("msg")) {
                    System.out.println(error + ".");
                }
                System.exit(0);
            } else {
                System.out.println("Se ha producido un error desconocido!");
            }
        } catch (IOException e) {
            System.out.println("Hubieron errores al cargar...");
            e.printStackTrace();
        }
    }
}

```

### Iniciando sesión con CaptivePortal

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

### Resgistro con MCPortal

```java
import cu.marilasoft.selibrary.MCPortal;

import java.io.IOException;
import java.util.Scanner;

public class Test {

    public static void main(String[] args) {
        MCPortal mcPortal = new MCPortal();
        try {
            mcPortal.singUp("55555555", "Nomre", "Apellido", "user@email.com");
            System.out.println("Introduzca el código enviado a su teléfono: ");
            @SuppressWarnings("resource")
            Scanner keyMap = new Scanner(System.in);
            String code = keyMap.nextLine();
            mcPortal.completeSingUp(code, "password", mcPortal.getCookies());
            if (mcPortal.getStatus().get("status").equalsIgnoreCase("success")) {
                System.out.print("SUCCESS :: ");
            } else {
                System.out.print("ERROR :: ");
            }
            System.out.println(mcPortal.getStatus().get("msg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

```
