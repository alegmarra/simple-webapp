simple-webapp
================

Documentación Taller de Programación 2: Prueba de concepto para la interacción entre la capa de Usuario y Presentación

# Requerimientos y deploy:
	
		sudo apt-get update
		sudo apt-get upgrade
        sudo apt-get install maven
        sudo apt-get install tomcat7
        sudo apt-get install tomcat7-admin 
	
	
Si no se instala correctamente el tomcat desde el repositorio, bajarlo de aca: http://tomcat.apache.org/download-70.cgi y completar los archivos faltantes

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


- Deploy manual:

Dentro de la carpeta root de la aplicacion, ejecutar (se toman los paths por defecto):
	
		mvn clean package && sudo cp ./target/simple-webapp.war /usr/share/tomcat7/webapps/

Hacer el deploy del war en tomcat


# Mensajes y respuestas:

Hacer un request a un webService desde otro webService: 


	// Dentro de la url se establece el GET request
 	CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(consumedWSurl);
        HttpResponse response = httpClient.execute(httpGet);
        
        // Queda a la espera de una respuesta del WS
        HttpEntity entity = response.getEntity();
 	
 	
Responder un request desde el webService:


	@GET
    	@Path("testJSON")
    	@Produces(MediaType.APPLICATION_JSON)
    	public User getUserJSON(@QueryParam("userkey")String userkey) {
        
            if( !users.containsKey(userkey)){
            
            	// Si hay un error, se retorna el mismo en forma estandar de respuesta http
            	
            	// Status = "404 Not Found"
            	ResponseBuilder builder = Response.status(Response.Status.NOT_FOUND);
                
                // Reason = mensaje descriptivo del error
                builder.entity("Requested user not found on server");
                Response response = builder.build();
                    
                throw new WebApplicationException(response);
            }
        
            // Si el usuario existe se retorna como mensaje JSON.
            return users.get(userkey);
    	}
 	
 
# Sobre las anotaciones Java

@GET, @POST tipo de método permitido

@Path appendea la string al path del contexto.
Ejemplo, donde el path resultante es userController/testJSON:

	@Path("/userController")
	public class UserController {
	    @Path("testJSON")  //
	    public User getUserJSON(@QueryParam("userkey")String userkey) {
	        return users.get(userkey);
	    }
	}


El primer elemento de la URL de los request (en este caso, '/') sale de acá: src/main/webapp/WEB-INF/web.xml,
del nodo
    <servlet-mapping>
        <servlet-name>Jersey Web Application</servlet-name>
        <url-pattern>/*</url-pattern>
    </servlet-mapping>
    
No hay una magia detrás que tome el nombre de la clase y lo transforme en path :/

Si a un método no se le pone @Path (siendo que a la clase sí), ese método se va a llamar cuando se hagan un
request al path vacío (con /userController, si fuera de esa clase)
Si a dos métodos no se les pone @Path (siendo que a la clase sí), rompe todo, 500 - Internal server error,
porque sólo uno puede tener el mismo path.

Que una clase herede de otra no afecta el path, es decir, sigue siendo la base '/' o lo que hubiera sido definido
en web.xml.
Ejemplo:
Para llamar al siguiente método, el path es /usercontrollerhijo/myPath

	@Path("/userControllerHijo")
	public class UserControllerHijo extends UserController {

	    @GET
	    @Produces(MediaType.TEXT_PLAIN)
	    @Path("myPath")
	    public String foo() {
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


@Produces define el tipo de respuesta que manda (JSON, XML, binario...).
Poniendo @Produces(MediaType.APPLICATION_JSON) a nivel clase abstracta controller quedaría definido que
 devolvemos siempre JSON.
Un mismo método puede tener @Produces de varios tipos, (por ejemplo xml y json) y que decida qué retornar
dependiendo qué venga en el header del request

@Consumes sirve para definir qué tipo de datos se aceptan en los request (no confundir: no tiene que ver con consumir
o no un ws ajeno)


# Vistas
 
 Se decidió no trabajar con templates del lado del servidor, por lo que este expone unicamente una api REST y se consumen los datos como un webservice. Las vistas, entonces, se manejaran con client-side templates, quedando fuera del scope de esta poc. 

 A continuación algo de info al respecto:
 
 		http://codebrief.com/2012/01/the-top-10-javascript-mvc-frameworks-reviewed/
 		http://engineering.linkedin.com/frontend/leaving-jsps-dust-moving-linkedin-dustjs-client-side-templates
 		http://engineering.linkedin.com/frontend/client-side-templating-throwdown-mustache-handlebars-dustjs-and-more
 		http://www.bymichaellancaster.com/blog/basic-overview-of-client-side-templating/
 
 La idea del templating es hacer GET/POST al server, recibiendo y enviando JSON únicamente o xml si es necesario.

