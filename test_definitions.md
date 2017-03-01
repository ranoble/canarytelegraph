Test Definitions
================

Example:
 
    bindings:
     - lastname: '[ab]{4,6}c'
    
    steps:
      - type: http
        name: Update user
        url: http://localhost/people
        auth:
          type: basic
          name: guest
          password: guest
        method: PUT
        payload: '{"id": ${canary_id}, "firstName": "John", "lastName": "${lastname}"}'
        headers:
          - Content-Type: application/json
        confirm:
          - field: status
            name: Confirm request status code
            operation: equals
            value: 200
          - field: body
            name: Confirm request lastName updated
            operation: contains
            value: '"lastName": "${lastname}"'
      - type: delay
        name: Wait for 60 minutes
        length: 60
        metric: minute
      - type: http
        name: Check user update
        url: http://localhost/people/${canary_id}
        auth:
          type: basic
          name: guest
          password: guest
        method: GET
        headers:
         - Content-Type: application/json
        confirm:
          - field: status
            name: Confirm result status code
            operation: equals
            value: 200
          - field: body
            name: Confirm lastName updated
            operation: contains
            value: '"lastName": "${lastname}"'

A test in Canary is a YAML definition, which defines 2 core items.

Bindings
--------

First it defines Bindings, bindings are either literals, or simple regular expressions.
If a binding is a regular expression, each time a test is run, the expression will be reversed so that a random string matching the rule is generated.
The rules for what can be use can be found here: https://code.google.com/archive/p/xeger/

A binding is a variable that can be used anywhere in a test, or test step, and will be fixed through the life of that test.
You can use these to set variables in a test, and check that they have been applied in a confirmation.

lastname above is a binding, and it is used in the payload of the HTTP steps and the confirmations, using the ${} substitution syntax.

Steps
-----

Steps are actions which are followed sequentially as they are defined. They are pluggable components, so that we can accomodate a number 
of different actions.

At the time of writing we only have 2 step types, "http", for http and https requests and delay.
Each Step can have a list of confirmations, the actions of which are bound to the request.

### Delay: Type = delau

    - type: delay
      name: Wait for 60 minutes
      length: 60
      metric: minute
        
Define a wait period. metrics can be seconds, minutes, hours or days.
No confirmations are currently supported for the delay type.

### HTTP

      - type: http
        name: Update user
        url: http://localhost/people
        auth:
          type: basic
          name: guest
          password: guest
        method: PUT
        payload: '{"id": ${canary_id}, "firstName": "John", "lastName": "${lastname}"}'
        headers:
          - Content-Type: application/json
        confirm:
          - field: status
            name: Confirm request status code
            operation: equals
            value: 200
          - field: body
            name: Confirm request lastName updated
            operation: contains
            value: '"lastName": "${lastname}"'

Run a HTTP or HTTPS request, and test the result.

This requires that you define a name, URL and method, GET, POST, PATCH, OPTIONS, HEAD, DELETE
Payload, headers, and auth (only basic auth at present) are optional, depending on your use case.

The HTTP step type currently supports the tests, equals and contains, and can check a status code (field: status) and the body (field: body) against the value provided.


