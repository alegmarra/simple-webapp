simple_service_webapp
================

Taller de Programación 2: Prueba de concepto para la interacción entre la capa de Usuario y Presentación

# Documentación: POC client-server tp taller 2 

- Requerimientos:
	
		sudo apt-get update
		sudo apt-get upgrade
        sudo apt-get install maven
        sudo apt-get install tomcat7
        sudo apt-get install tomcat7-admin 
	
	


Agregar usuarios en el conf file "tomcat-users.xml", ie:
	
    <role rolename="admin-gui"/>
    <user username="admin" password="admin" roles="admin-gui"/>

- Recursos:

	
		http://www.mkyong.com/webservices/jax-rs/jersey-hello-world-example/
		http://blog.bdoughan.com/2010/08/creating-restful-web-service-part-55.html
		http://maven.apache.org/guides/getting-started/maven-in-five-minutes.html
		http://www.techzoo.org/how-to/restful-java-web-service-with-xml-response.html
		http://www.techzoo.org/how-to/how-to-create-simple-rest-web-service-using-jersey.html
		http://www.techzoo.org/tutorials/java-tutorials/consuming-restful-web-service-using-jquery-ajax-client.html
		https://jersey.java.net/
		https://jersey.java.net/documentation/latest/index.html
		http://www.w3schools.com/xml/xml_http.asp
		http://www.w3fools.com
		http://hc.apache.org/httpcomponents-client-ga/
	


- Eclipse: 
	http://www.eclipse.org/downloads/packages/eclipse-standard-431/keplersr1

	- plugins desde eclipse_marketplace o "install new software":
	
  		m2eclipse
  		Web XML, Jave EE,... (algunos paquetes se pueden sacar)

	- Si da error al importar el projecto (click derecho->import->maven project), hacer en approot/ :
		  
			mvn eclipse:clean
	
	- Setearlo como DWM:
  
	  http://blog.teamextension.com/maven-as-eclipse-dynamic-web-module-556


Si se quiere hacer el deploy manualmente:

Dentro de la carpeta root de la aplicacion, ejecutar:
	
		mvn clean package && sudo cp ./target/simple-service-webapp.war /usr/share/tomcat7/webapps/

Hacer el deploy del war en tomcat


Para hacer el deploy a heroku:
  https://devcenter.heroku.com/articles/war-deployment
  
  
  
 - Y las vistas??
 
 Se decidió no trabajar con JSP ni templates del lado del servidor, por lo que este expone unicamente una api REST. Las vistas, entonces, se manejaran con client-side templates, quedando fuera del scope de esta poc. 

 A continuación algo de info al respecto:
 
 		http://codebrief.com/2012/01/the-top-10-javascript-mvc-frameworks-reviewed/
 		http://engineering.linkedin.com/frontend/leaving-jsps-dust-moving-linkedin-dustjs-client-side-templates
 		http://engineering.linkedin.com/frontend/client-side-templating-throwdown-mustache-handlebars-dustjs-and-more
 		http://www.bymichaellancaster.com/blog/basic-overview-of-client-side-templating/
 
 La idea del templating es hacer GET/POST al server, recibiendo y enviando JSON únicamente o xml si es necesario.

 


 - Ejemplo online:
 
		http://simple-service-webapp.herokuapp.com/



# Sobre las anotaciones Java

@GET, @POST tipo de método permitido

@Path appendea la string al path del contexto.
Ejemplo, donde el path resultante es userController/testJSON:

	@Path("/")
	public class UserController {
	    @Path("testJSON")  //
	    public User getUserJSON(@QueryParam("userkey")String userkey) {
	        return users.get(userkey);
	    }
	}


Sería interesante poder poner en cada clase un nombre al path y en una clase abstracta controller poner
@Path("/" + Controller.path), siendo Controller.path definida como public static String.
Si no se pone @Path a la clase, los métodos de UserController no son accesibles a través de ws, o al menos
no sé cómo es el path.

El primer elemento de la URL de los request (userController) sale de acá: src/main/webapp/WEB-INF/web.xml.
No hay una magia detrás que tome el nombre de la clase y lo transforme en path :/

Si a un método no se le pone @Path (siendo que a la clase sí), ese método se va a llamar cuando se hagan un
request al path vacío (con /userController, claro)
Si a dos métodos no se les pone @Path (siendo que a la clase sí), rompe todo, 500 - Internal server error

Que una clase herede de otra no afecta el path, es decir, sigue siendo la base /userController.
Ejemplo:
Para llamar al siguiente método, el path es /userController/tuHermana/lalala

	@Path("/tuHermana")
	public class UserControllerSeba extends UserController {

	    @GET
	    @Produces(MediaType.TEXT_PLAIN)
	    @Path("lalala")
	    public String lalala() {
	        return "Got it from inherited class!";
	    }
	}

De la misma manera que antes, si no se pone el @Path en la clase, no es accesible. Y si dejamos métodos sin path,
como heredó el método pathless de la clase padre, rompe todo con [HTTP 500](http://httpcats.herokuapp.com/500).

@PathParam usado para parámetros obligatorios, hace referencia a una parte de la URL que te sirve como variable.
Ejemplo tomando la última parte del path como la variable userkey:

	@Path("testXML/{userkey}")
	public User getUserXML(@PathParam("userkey") String userkey) {
	   	return users.get(userkey);
	}


@QueryParam toma un parámetro de la query string


@Produces informa el tipo de respuesta que manda (JSON, XML, binario...).
Poniendo @Produces(MediaType.APPLICATION_JSON) a nivel clase abstracta controller quedaría definido que
 devolvemos siempre JSON.
Un mismo método puede tener @Produces de varios tipos, (por ejemplo xml y json) y que decida que retornar
dependiendo qué venga en el header del request

@Consumes sirve para definir qué tipo de datos se aceptan, no tiene que ver con consumir
o no un ws
