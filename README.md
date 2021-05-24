# Mind Map Webservice API
This API provides REST API endpoints to create a mind map and store its data in a mongoDB.
It's built using Spring boot framework.

## Requirements
* Java 8
* Maven 3
* Docker
* Docker compose

# Run
run the run.sh file located at the root of the project to build and start the API
```
sh run.sh
```
The api will respond on the url http://localhost:8080

# APi Endpoints
* Create a mind map: POST /map
* Read a mind map: GET /map/{mind-map-id}
* Pretty print of the map: GET /map/{mind-map-id}/pretty
* Add a leaf to the map: POST /map/{mind-map-id}/leaf
* Read a leaf: GET /map/{mind-map-id}/leaf/{leaf-id}

The whole API documentation can be found at http://localhost:8080/swagger-ui.html

# Test coverage

A jacoco library is added to the project. To get the test coverage metrics, 
open the index.html file in the folder:
```
target/jacoco-report
```
