# Spring_Core_GYM

<h2>Requirements</h2>
<h3>Environment</h3>
In order to build application you need to add .env file and fill next information inside it.

KEY_STORE_PASSWORD=<br/>
TRUST_STORE_PASSWORD=<br/>

MONGODB_DB_NAME=<br/>
MONGO_DB_USERNAME=<br/>
MONGO_DB_PASSWORD=<br/>

RABBITMQ_USER=<br/>
RABBITMQ_PASSWORD=<br/>

POSTGRES_DB=<br/>
POSTGRES_USER=<br/>
POSTGRES_PASSWORD=<br/>

TOKEN_SIGNING_KEY=<br/>

<h3>Bash configuration deploy_microservices.sh</h3>
You need to add GITLAB_TOKEN="" in order to clone the app

then use next commands in linux type terminal </br>
chmod +x deploy_microservices.sh</br
./deploy_microservices.sh

<h2>Commands</h2>
<h3>Commands to test application</h2>

mvn clean install verify


