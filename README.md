# Canary Telegraph


Useful CURL:

Create a Lane: 

curl -vX POST --data='{"laneBindings": {"canary": "1"}, "name": "first", "active": true}' -H "Content-Type: application/json" http://localhost:8080/lane/

Create a Test:

curl -vX POST -d @test_google.json http://localhost:8080/lane/4/tests -H "Content-Type: application/json"

Add a Test to a Lane (Update Test ID and Lane ID as appropriate):

curl -vX POST -d '/test/2' http://localhost:8080/lane/6/tests -H "Content-Type: application/json"


OLD COMMANDS


curl -vX POST -d '{"laneBindings": {"canary": "1"}, "name": "first", "active": true}'  -H "Content-Type: application/json" http://localhost:8080/lane/

curl -vX POST -d '/test/2' http://localhost:8080/lane/6/tests -H "Content-Type: application/json"

curl -vX PATCH -d "start" http://localhost:8080/lane/4 -H "Content-Type: application/json"

curl -vX POST -d @test_google.json http://localhost:8080/lane/4/tests -H "Content-Type: application/json"

curl -vX POST -d '/test/2' http://localhost:8080/lane/4/tests -H "Content-Type: application/json"
