# Canary Telegraph


Useful CURL
curl -vX POST -d @test.json http://localhost:8080/lane/4/tests -H "Content-Type: application/json"
curl -vX PATCH -d "start" http://localhost:8080/lane/4 -H "Content-Type: application/json"

curl -vX POST -d @test_google.json http://localhost:8080/lane/4/tests -H "Content-Type: application/json"

curl -vX POST -d '/test/2' http://localhost:8080/lane/4/tests -H "Content-Type: application/json"
