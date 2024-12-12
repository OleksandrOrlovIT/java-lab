Feature: Monolith endpoints populate rabbitmq, microservice takes message and updates database accordingly

  Scenario: User creates Trainee, Trainer, Training and Microservice updates database
    When user sends request to monolith to create trainee with body:
    """
    {
      "firstName": "asd",
      "lastName": "asd",
      "dateOfBirth": "2024-11-21",
      "address": "string"
    }
    """
    Then the response status should be 200
    When user sends request to monolith to create trainer and get trainer password with body:
    """
    {
      "firstName": "a",
      "lastName": "b",
      "specializationId": 1
    }
    """
    Then the response status should be 200
    When user sends request to monolith to login as a trainer and get auth token with username = "a.b" and saved password
    Then the response status should be 200
    When authorized user sends request to monolith to create training with body:
    """
    {
      "traineeUsername": "asd.asd",
      "trainerUsername": "a.b",
      "trainingName": "string",
      "trainingDate": "2024-01-09",
      "trainingDurationMinutes": 10,
      "trainingTypeId": 1
    }
    """
    Then the response status should be 200
    And wait for 5 seconds
    When user sends request to microservice to get trainer summary with username "username"
    Then the response status should be 200
    And the trainer summary should equal to body:
    """
    {
      "username": "a.b",
      "firstName": "a",
      "lastName": "b",
      "status": false,
      "years": [
        {
          "year": 2024,
          "months": [
            {
              "month": 1,
              "duration": 10
            }
          ]
        }
      ]
    }
    """

