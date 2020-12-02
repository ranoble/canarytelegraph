# Canary Telegraph

## How to get it running.

1. Install postgres (you will need the hstore type)
2. Create a database
3. Build the app, using mvn package
4. Set up your environment, by copying server.yml.sample to server.yml and updating as appropriate.
5. Run the app, using java -jar [your_jar] server server.yml
6. Go to http://localhost:8080/swagger to look around the API
7. Read Basics, below, and test_definitions.md to get an understanding of how it works.
8. Register Tests
9. Register Lanes
10. Add tests to the Lanes, and start them up.
11. Check /status for results, an error code 412 means some of your tests are failing.

## Basics

Canary uses two core ideas, that of a Lane, and that of a Test. 

### Lanes 

 - A Lane is a long running thread that runs Tests. 
 - A Lane can have variables bound to it, using it's bindings, and should only be able to make changes to a predefined set of objects in your system. 
 - It is advisable that Lanes' bindings are mutually exclusive.

Lets use an example. You want to test that updating a user's last name works in one service, and that this is propogated to another related service. You would create a test user on the system, and add the user identifer as a binding to the lane. This binding would then be used in a test. 

Lanes run simultaneously, and if another test in another lane can pdate the same user, then you will start getting false negatives in your tests.

### Tests

Tests on the other hand have bindings of their own. In both Lanes and tests, you can use simple Regular Expressions to generate random data for testing.

Tests have a number of Steps. Steps are performed in order, and can generate test Results.

See: test_definitions.md for more on tests.  

## Useful CURL requests:

### Create a Lane: 

    curl -vX POST --data '{"laneBindings": {"canary": "1"}, "name": "first", "active": true}' -H "Content-Type: application/json" http://localhost:8080/lane/

### Create a Test:

    curl -vX POST -d @test_google.json http://localhost:8080/test -H "Content-Type: application/json"

### Add a Test to a Lane (Update Test ID and Lane ID as appropriate):

    curl -vX POST -d '/test/2' http://localhost:8080/lane/6/tests -H "Content-Type: application/json"


