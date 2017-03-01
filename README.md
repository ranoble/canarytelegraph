# Canary Telegraph

## How to get it running.

1. Install postgres
2. Create a database
3. Build the app, using mvn package
4. Set up your environment, by copying server.yml.sample to server.yml and updating as appropriate.
5. Run the app, using java -jar [your_jar] server server.yml
6. Go to http://localhost:8080/swagger to look around the API
7. Read test_definitions.md to get an understanding of how it works.


## Useful CURL requests:

### Create a Lane: 

    curl -vX POST --data='{"laneBindings": {"canary": "1"}, "name": "first", "active": true}' -H "Content-Type: application/json" http://localhost:8080/lane/

### Create a Test:

    curl -vX POST -d @test_google.json http://localhost:8080/lane/4/tests -H "Content-Type: application/json"

### Add a Test to a Lane (Update Test ID and Lane ID as appropriate):

    curl -vX POST -d '/test/2' http://localhost:8080/lane/6/tests -H "Content-Type: application/json"


