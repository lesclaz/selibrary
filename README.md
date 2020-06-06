selibrary
=========
## Una librería de SuitETECSA

selibrary es una librería pensada para la comunidad [SWL-X](https://swlx.info), para facilitar el desarrollo de
aplicaciones en Java, kotlin y/o Android que interactúen con el [Portal de Usuario](https://www.portal.nauta.cu/), el
[Portal Cautivo](https://secure.etecsa.net:8443/) de nauta y el [Portal Mi Cubacel](https://mi.cubacel.net). Bien es 
sabido que con solo un cambio que realice ETECSA en uno o varios de sus portales podría significar varias horas de 
trabajo por parte de desarrolladores de apps como ETK, Útiles o QVaCall, por lo que selibrary pudiera llegar a ser
esa librería que termine ahorrándoles tiempo, esfuerzos, neuronas y código a los desarrolladores.
 
selibrary pretende ser no solo multiplataforma, sino también multi-lenguaje, échele un vistazo a
[PySELibrary](https://github.com/marilasoft/PySELibrary/); la misma librería escrita en Python.
Esta, la versión en Kotlin usa la librería [Jsoup](https://jsoup.org/) para el procesamiento de páginas web (los portales
de [ETECSA](http://www.etecsa.cu)), mientras que la versión en Python usa
[BeautifulSoup4](http://www.crummy.com/software/BeautifulSoup/bs4/).

Después de varias versiones e implementaciones de la librería hemos caído en cuenta que al menos para la versión en kotlin
es más factible la utilización de interfaces que la de clases, y por tal motivo hemos decidido convertir en interfaces las
clases `UserPortalClient`, `NautaClient` y `CubacelClient` encargadas de interactuar con el 
[Portal de Usuario](https://www.portal.nauta.cu/), el [Portal Cautivo](https://secure.etecsa.net:8443/) y el
[Portal Mi Cubacel](https://mi.cubacel.net) respectivamente.

Por el momento selibrary ha logrado implementar de forma `estable` 10 funciones que representan el 100% de
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

El [Portal Cautivo](https://secure.etecsa.net:8443/) de nauta, también es manejado por selibrary y hasta la fecha
 realiza las siguientes operaciones de forma `estable`:
* Iniciar sesión.
* Actualizar tiempo disponible.
* Cerrar sesión.
* Obtener información del usuario.
* Acceder a los términos de uso.

El [Portal Mi Cubacel](https://mi.cubacel.net) toma una importancia especial en selibrary, ya que por medio de este
 se puede interactuar con los principales servicios ofrecidos por ETECSA (paquetes de `Datos`, `Voz`, `SMS`, 
`Plan Amigos`, `Adelanta Saldo`, etc.), esta sección de la librería se encuentra en constante desarrollo y hasta
la fecha puede realizar las siguientes operaciones:
* Iniciar sesión. (`estable`)
* Recuperar la información siguiente:
    * Número de teléfono. (`estable`)
    * Saldo. (`estable`)
    * Fecha de expiración del servicio. (`estable`)
    * Bono. (`estable`)
    * Fecha de expiración del bono. (`estable`)
    * Fecha en la que se utilizó el servicio `Adelanta Saldo` (si aún debe saldo). (`estable`)
    * Saldo por pagar (si aún debe saldo). (`estable`)
    * Números asociados al servicio 'Plan Amigo' (de existir estos). (`estable`)
    * Estado de la tarifa por consumo. (`estable`)
    * Estado de los paquetes comprados. (`estable`)
* Cambia estado de la tarifa por consumo. (`estable`)
* Recupera y compra productos (`paquetes`). (`estable`)
* Restablece contraseña olvidada. (`estable`)
* Registra un usuario nuevo en el portal. (`estable`)
* Añade, cambia y elimina números del plan amigos. (`por probar`)
* Solicita préstamo de saldo. (`por probar`)

## Ventajas de selibrary

* Ahorra tiempo, esfuerzos, neuronas y código a los desarrolladores.
* Es de código abierto, por lo que cualquier persona puede colaborar, ya sea con una idea, una corrección y/o una
funcionalidad nueva.
* Es multiplataforma.
* Al realizar las operaciones directamente mediante el portal, presenta menor margen de error a la hora de comprar
paquetes, solicitar préstamo de saldo, etc.
* Obtiene mayor cantidad de información con menos ordenes.
* Mayor facilidad a la hora de crear una app para gestionar servicios de ETECSA.

## Desventaja de selibrary

* Cualquier cambio en el/los portal/es puede(`según su magnitud`) anular una función de la librería o dejar una 
sección inservible(`sección que interactúe con el/los portal/es modificado/s`).

## Usando selibrary:

selibrary es de uso fácil y de pocas ordenes. A continuación se muestran ejemplos sencillo de como usar cada una de las
interfaces de esta librería.

### Iniciando sesión con UserPortalClient

```kotlin
import java.util.*

fun main() {
    Run().start()
}

class Run : UserPortalClient {

    fun start() {
        preLogin()
        loadCAPTCHA(cookies)
        downloadCaptcha("Captcha.png")
        val captchaCode: String
        println("Introduzca el código de la imagen captcha: ")
        val keyMap = Scanner(System.`in`)
        captchaCode = keyMap.nextLine()
        login("user.name@nauta.com.cu", "Password", captchaCode, cookies)
        println(userName)
        println(credit)
        println(blockDate)
        println(mailAccount)
        logout(cookies)
    }
}
```

### Iniciando sesión con NautaClient

```kotlin

fun main() {
    Run().start()
}

class Run : NautaClient {

    fun start() {
        preLogin()
        dataMap["username"] = "user.name@nauta.com.cu"
        dataMap["password"] = "password"
        login() //Inicia sesion
        println(getUserTime())
        Thread.sleep(60000) // Espera un minuto
        logout()
        println(getUserTime())
    }
}
```

### Registro con CubacelClient

```kotlin
import java.util.*

fun main() {
    class Task : CubacelClient

    val task = Task

    task.signUp("55555555", "Nombre", "Apellido", "user@mail.com")
    val code: String
    println("Introduzca el código que le envió Cubacel: ")
    val keyMap = Scanner(System.`in`)
    code = keyMap.nextLine()
    task.verifyCode(code, cookies)
    task.completeSignUp("Password", cookies)
}
```
