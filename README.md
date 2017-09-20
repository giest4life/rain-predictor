# rain-predictor
A simple web application for weather information

## Setup
This application uses `maven 3.5.0` (other `3.x` versions should work fine as well) for dependency and build management and it is is strongly recommended.
* Clone the repository using the git or download the repository as a zip file
## Configuration
This application loads its configuration from `app.properties`. At build time, maven filters the tokens, enclosed by delimitters `@`, and replaces them with values defined in properties file from the `config` folder in the project root. The naming convention for these properties files is `${env}.properties` where `env` is a property defined in the `pom.xml`. The the `env` property is defined per profile. The name of the profile is also the name of the environment variable. There are two profiles defined:

  * `dev` which is active by default
  * `prod` which can be selected using the `-P` flag when providing maven commands; e.g. `mvn clean package -P prod`

The properties files inside the `config` folder are checked into source control and it is not recommended to store any sensitive properties such as database passwords and API keys. These properties can be defined in files in the `private` folder, directly inside the `config` folder. The file names in the `config/private` folder should have the same format as non-private properties files

## Testing
TBD
## Deployment
### Deployment Using Maven and Tomcat 7/8
This is the simplest way to deploy this application is to use maven integration with Tomcat 7/8 via the `tomcat7-maven-plugin`.
`mvn tomcat7:redeploy` will deploy the WAR file to the server with id `dev-tomcat`--the id of the server is simply `${env}-tomcat`. The server details should be defined in your `settings.xml` file and must not be exposed via source control. Here is an example of a simple server configuration:
```
    ...
    <server>
      <id>dev-tomcat</id>
      <username>tomcat</username>
      <password>password</password>
    </server>
    ...
```
For more information on maven integration with Tomcat please read the [plugin's documentation](http://tomcat.apache.org/maven-plugin-2.2/tomcat7-maven-plugin/)
### Deployment Using Maven to other Java App Servers
Your application server may already have integration with Maven via plugin and you should refer to your server's documentation for the details. For example, if you are using Jetty, you can use the [Jetty plugin](http://www.eclipse.org/jetty/documentation/9.3.x/jetty-maven-plugin.html)
### Deployment Using the WAR File
A WAR file can be generated for the project using `mvn package` and you can use that to deploy the application to your server of choice.