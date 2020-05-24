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
clases `UserPortal`, `CaptivePortal` y `MCPortal` encargadas de interactuar con el 
[Portal de Usuario](https://www.portal.nauta.cu/), el [Portal Cautivo](https://secure.etecsa.net:8443/) y el
[Portal Mi Cubacel](https://mi.cubacel.net) respectivamente.

Por el momento selibrary ha logrado implementar 10 funciones que representan el 100% de
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

El [Portal Cautivo](https://secure.etecsa.net:8443/) de nauta, también es manejada por selibrary y hasta la fecha
 realiza las siguientes operaciones:
* Iniciar sesión.
* Actualizar tiempo disponible.
* Cerrar sesión.
* Obtener información del usuario.
* Acceder a los términos de uso.

El [Portal Mi Cubacel](https://mi.cubacel.net) toma una importancia especial en selibrary, ya que por medio de este
 se puede interactuar con los principales servicios ofrecidos por ETECSA (paquetes de `Datos`, `Voz`, `SMS`, 
`Plan Amigos`, `Adelanta Saldo`, etc.), esta sección de la librería se encuentra en constante desarrollo y hasta
la fecha puede realizar las siguientes operaciones:
* Iniciar sesión.
* Recuperar la información siguiente:
    * Número de teléfono.
    * Saldo.
    * Fecha de expiración del servicio.
    * Bono.
    * Fecha de expiración del bono.
    * Fecha en la que se utilizó el servicio `Adelanta Saldo` (si aún debe el saldo adelantado).
    * Saldo por pagar (si aún debe el saldo adelantado).
    * Números asociados al servicio 'Plan Amigo' (de existir estos).
    * Estado de la tarifa por consumo.
    * Estado de algunos de los paquetes de navegación.
* Cambia estado de la tarifa por consumo.
* Recupera y compra productos (`paquetes`)
* Restablece contraseña olvidada.
* Registra un usuario nuevo en el portal.
* Añade, cambia y elimina números del plan amigos.
* Solicita préstamo de saldo.

## Ventajas de selibrary

* Ahorra tiempo, esfuerzos, neuronas y código a los desarrolladores.
* Es de código abierto.
* Es multiplataforma.
* Cualquier persona puede colaborar, ya sea con una idea, una corrección y/o una funcionalidad nueva.
* Menor margen de error a la hora de comprar paquetes de navegación (Esto fue demostrado cuando ETECSA adiciono
los paquetes LTE, mientras apps como ETK, QVaCall y Útiles compraban paquetes erróneos, selibrary sin cambio
alguno mostraba estos nuevos paquetes y no sufrió cambio alguno en el resultado de las compras).
* Al mostrar la información directamente desde el portal, cualquier cambio en la descripción, precio o duración
de los paquetes serán reflejados en la app sin necesidad de cambio alguno.

## Desventaja de selibrary

* Cualquier cambio en el/los portal/es puede(`según su magnitud`) anular una función de la librería o dejar una 
sección inservible(`sección que interactúe con el/los portal/es modificado/s`).

## Ejemplos:

selibrary es de uso fácil y de pocas ordenes. A continuación se muestran ejemplos de como usar cada una de las
secciones de esta librería.

### Iniciando sesión con UserPortal

```kotlin
import java.util.*

fun main() {
    Run().start()
}

class Run : UserPortal {

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

### Iniciando sesión con CaptivePortal

```kotlin

fun main() {
    Run().start()
}

class Run : CaptivePortal {

    fun start() {
        preLogin()
        val user = "user.name@nauta.com.cu"
        val password = "password"
        val dataMap: MutableMap<String, String> = HashMap()
        dataMap["wlanuserip"] = wLanUserIp
        dataMap["wlanacname"] = wLanAcName
        dataMap["wlanmac"] = wLanMac
        dataMap["firsturl"] = firstUrl
        dataMap["ssid"] = SSId
        dataMap["usertype"] = userType
        dataMap["gotopage"] = gotoPage
        dataMap["successpage"] = successPage
        dataMap["loggerId"] = loggerId
        dataMap["lang"] = lang
        dataMap["username"] = ""
        dataMap["password"] = ""
        dataMap["CSRFHW"] = CSRFHW
        login(user, password, cookies, dataMap) //Inicia sesion
        println(updateAvailableTime(sessionParameters!!["updateTimeUrl"].toString(), cookies))
        Thread.sleep(60000) // Espera un minuto
        logout(sessionParameters!!["logoutUrl"].toString(), cookies)
        println(updateAvailableTime(sessionParameters!!["updateTimeUrl"].toString(), cookies))
    }
}
```

### Registro con MCPortal

```kotlin
import java.util.*

fun main() {
    class Task : MCPortal

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

## A tener en cuenta:

Creemos que se pueden presentar situaciones que aún no hemos experimentado por lo que no hemos podido identificar todas
las excepciones que puedan llegar a dispararse en la comunicación con los portales. Usted es libre de usar o no selibrary
pero aclaramos que hasta la fecha y según nuestra experiencia, ninguna excepción no manejada pondrá en peligro el saldo 
del usuario; cualquier excepción disparada en una operación significa que esa operación no se llegó a efectuar y nada más.
