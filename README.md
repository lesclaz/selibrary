selibrary
=========
## Una librería de SuitETECSA

selibrary fue creada para la Comunidad [SWL-X](https://swlx.info), para facilitar el desarrollo de
aplicaciones en Java, kotlin y/o Android que interactúen con el [Portal de Usuario](https://www.portal.nauta.cu/), el
[Portal Cautivo](https://secure.etecsa.net:8443/) de nauta y el [Portal Mi Cubacel](https://mi.cubacel.net),
ahorrándoles tiempo, esfuerzos, neuronas y código a los desarrolladores.
 
selibrary pretende ser no solo multiplataforma, sino también multilenguaje, échele un vistazo a
[PySELibrary](https://github.com/marilasoft/PySELibrary/); la misma librería escrita en Python.
Esta, la versión en Kotlin usa la librería [Jsoup](https://jsoup.org/) para el procesamiento de paginas web (los portales
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
* Añade, cambia y elimina números delplan amigos
* Solicita prestamo de saldo
* Obtiene información de varios paquetes de navegación.

MCPortal se encuentra en fase experimental, por lo que aún no maneja muchos de los errores que puedan
producirse en el proceso de cualquiera de la operaciones implementadas.


## Ejemplos:

### Iniciando sesión con UserPortal

```kotlin
import java.io.*
import java.util.*


fun downloadCaptcha(captcha: ByteArray) {
    try {
        var out: ByteArrayOutputStream? = null
        BufferedInputStream(ByteArrayInputStream(captcha)).use { `in` ->
            out = ByteArrayOutputStream()
            val buf = ByteArray(1024)
            var n: Int
            while (-1 != `in`.read(buf).also { n = it }) {
                out!!.write(buf, 0, n)
            }
            out!!.close()
        }
        val response = out!!.toByteArray()
        val ubicacion = "Captcha.png"
        FileOutputStream(ubicacion).use { fos -> fos.write(response) }
    } catch (ex: IOException) {
        ex.printStackTrace(System.out)
    }
}

fun main() {
    val userPortal = UserPortal()
    val cookies: Map<String, String>?
    try {
        userPortal.preLogin()
        cookies = userPortal.cookies
        userPortal.loadCAPTCHA(cookies!!)
        downloadCaptcha(userPortal.captchaImg!!)
        val userName = "user@nauta.com.cu"
        val password = "password"
        val captchaCode: String
        println("Introduzca el código de la imagen captcha: ")
        val keyMap = Scanner(System.`in`)
        captchaCode = keyMap.nextLine()
        userPortal.login(userName, password, captchaCode, cookies)
            println("Nombre de usuario: " + userPortal.userName)
            println("Fecha de bloque: " + userPortal.blockDate)
            println("Fecha de eliminacion: " + userPortal.delDate)
            println("Tipo de cuenta: " + userPortal.accountType)
            println("Tipo de servicio: " + userPortal.serviceType)
            println("Saldo disponible: " + userPortal.credit)
            println("Tiempo disponible: " + userPortal.time)
            println("Cuenta de correo: " + userPortal.mailAccount)
    } catch (e: IOException) {
        println("Hubieron errores al cargar...")
        e.printStackTrace()
    }
}
```

### Iniciando sesión con CaptivePortal

```kotlin
import java.io.IOException

fun main() {
    val captivePortal = CaptivePortal()
    try {
        captivePortal.preLogin()
        val userName = "user@nauta.com.cu"
        val password = "password"
        val dataMap: MutableMap<String, String> = HashMap()
        dataMap["wlanuserip"] = captivePortal.wLanUserIp
        dataMap["wlanacname"] = captivePortal.wLanAcName
        dataMap["wlanmac"] = captivePortal.wLanMac
        dataMap["firsturl"] = captivePortal.firstUrl
        dataMap["ssid"] = captivePortal.SSId
        dataMap["usertype"] = captivePortal.userType
        dataMap["gotopage"] = captivePortal.gotoPage
        dataMap["successpage"] = captivePortal.successPage
        dataMap["loggerId"] = captivePortal.loggerId
        dataMap["lang"] = captivePortal.lang
        dataMap["username"] = ""
        dataMap["password"] = ""
        dataMap["CSRFHW"] = captivePortal.CSRFHW
        val cookies: Map<String, String>? = captivePortal.cookies
        captivePortal.login(userName, password, cookies!!, dataMap)
        val sessionParameters: Map<String, Any?>? = captivePortal.sessionParameters
        val updateTimeUrl = sessionParameters!!["updateTimeUrl"].toString()
        println(captivePortal.updateAvailableTime(updateTimeUrl, cookies))
        try {
            Thread.sleep(5)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        captivePortal.logout((sessionParameters["logoutUrl"].toString()), cookies)
    } catch (e: IOException) {
        e.printStackTrace()
    }
}
```

### Resgistro con MCPortal

```kotlin
import java.io.IOException
import java.util.*

fun main() {
    val mcPortal = MCPortal()
    try {
        mcPortal.signUp("55555555", "Nomre", "Apellido", "user@email.com")
        println("Introduzca el código enviado a su teléfono: ")
        val keyMap = Scanner(System.`in`)
        val code: String = keyMap.nextLine()
        mcPortal.verifyCode(code, mcPortal.cookies)
        mcPortal.completeSignUp("password", mcPortal.cookies)
    } catch (e: IOException) {
        e.printStackTrace()
    }
}
```
