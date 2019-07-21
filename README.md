# selibrary: Libreria SuitETECSA
Es una libreria creada para facilitar el desarrollo de aplicaciones en JAVA y/o Android que interactuen con el [portal de usuario de nauta](https://www.portal.nauta.cu/), interactuando con este por medio del raspado web, y para esto usa la libreria [Jsoup](https://jsoup.org/).
selibrary se encuentra en fase beta, por lo que todas sus funcionalidades aún no se implementan, por el momento solo se puede interactuar con el [Portal de Usuario](https://www.portal.nauta.cu/) de nauta para realizar las siguientes acciones:
* Iniciar session en el Portal.
* Acceder a la información de la cuenta logeada.
* Recargar la cuenta logeada.
* Obtener informacion de las ultimas conexiones (`funcionalidad en desarrollo`).
## Ejemplo
Ejemplo de inicio de session usando selibrary.
```java
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
int loginStatus = session.login(userName, password, captchaCode);
if (loginStatus == 0) {
  // imprime el saldo de la cuanta logeada
  System.out.print(session.credit());
} else {
  for (String error : session.errors {
    // imprime los errores encontrados
    System.out.println(error);
  }
}
```
    
## Funciones de la Clase UserPortal
Una lista de las funciones que proporciona la clase `UserPortal` para interactuar con el [portal de usuario de nauta](https://www.portal.nauta.cu).
| Funcion | Tipo | Parametros | Descripcion |
|---------|------|------------|-------------|
| initialice | `void` | No | Hace una conexión´inicial con el [portal](https://www.portal.nauta.cu/) para optener la infomacion necesaria para iniciar session|
| loadCAPTCHA | `void` | cookies (Map) (`optional`) | Descarga la imagen CAPTCHA y la almacena en forma de bytes |
| login | `int` | userName (String), password (String), captchaCode (String), cookies (Map) (`opcional`) | Devuelve un entero cero (0) si todo va bien y uno (1) cuando haigan errores en el proceso |
| reload_userInfo | `void` | cookies (Map) (`optional`)| Recarga la informacion de la cuenta logeada |
| userName | `String` | No | Devulve el nombre de usuario de la cuenta logeada |
| blockDate | `String` | No | Devuelve la fecha de bloqueo de la cuenta logeada |
| delDate | `String` | No | Devuelve la feha de eliminación de la cuenta logeada |
| accountType | `String` | No | Devuelve el tipo de la cuenta logeada |
| serviceType | `String` | No | Devuelve el tipo de servicio que posee la cuenta logeada |
| credit | `String` | No | Devuelve el saldo de la cuenta logeada|
| time | `String` | No | Devulve el tiempo disponible en la cuenta logeada |
| mailAccount | `String` | No | Devuelve la cuenta de correo asociada a la cuenta logeada, de existir esta |