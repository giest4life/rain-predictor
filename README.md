# rain-predictor
A simple web application for weather information

## Setup
This application uses `maven 3.5.0` (other `3.x` versions should work fine as well) for depedency and build management and it is is strongly recommended.
* Clone the repoistory using the git or download the repository as a zip file
## Configuration
This application loads its configuration from a tokenized file `app.properties`. At build time, maven filters the tokens, enclosed by delimitters `@`, and replaces them with values defined in properties file from the `config` folder in the project root. The naming convention for these properties files is `$[env}.properties` where `env` is a property defined in the `pom.xml`. The the `env` property is defined per profile. The name of the profile is also the name of the environment variable. There are two profiles defined:

  * `dev` which is active by default
  * `prod` which can be selected using the `-P` flag when providing maven commands; e.g. `mvn clean package -P prod`

The properties files inside the `config` folder are chcked into source control and it is not recommended to store any sensitive properties such as database passwords and API keys. These properties can be defined in files in the `private` folder, directly inside the `config` folder. The file names in the `config/private` folder should have the same format as non-private properties files

## Testing
TBD
## Deployment
TBD
