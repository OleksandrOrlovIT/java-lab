Feature: Monolith endpoints populate rabbitmq, microservice takes message and updates database accordingly

  Scenario: User creates Trainee, Trainer, Training and Microservice creates new TrainerWorkload
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
    When user sends request to microservice to get trainer summary with username "a.b"
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
              "durationMinutes": 10
            }
          ]
        }
      ]
    }
    """
    When user sends request to monolith to logout
    Then the response status should be 200

  Scenario: User creates Trainee, Trainer, 2 Trainings with different years and Microservice creates 2 new TrainerWorkloads
    When user sends request to monolith to create trainee with body:
    """
    {
      "firstName": "trainee",
      "lastName": "1",
      "dateOfBirth": "2024-11-21",
      "address": "string"
    }
    """
    Then the response status should be 200
    When user sends request to monolith to create trainer and get trainer password with body:
    """
    {
      "firstName": "trainer",
      "lastName": "1",
      "specializationId": 1
    }
    """
    Then the response status should be 200
    When user sends request to monolith to login as a trainer and get auth token with username = "trainer.1" and saved password
    Then the response status should be 200
    When authorized user sends request to monolith to create training with body:
    """
    {
      "traineeUsername": "trainee.1",
      "trainerUsername": "trainer.1",
      "trainingName": "string",
      "trainingDate": "2024-01-09",
      "trainingDurationMinutes": 10,
      "trainingTypeId": 1
    }
    """
    Then the response status should be 200
    When authorized user sends request to monolith to create training with body:
    """
    {
      "traineeUsername": "trainee.1",
      "trainerUsername": "trainer.1",
      "trainingName": "string",
      "trainingDate": "2020-10-01",
      "trainingDurationMinutes": 60,
      "trainingTypeId": 1
    }
    """
    Then the response status should be 200
    And wait for 5 seconds
    When user sends request to microservice to get trainer summary with username "trainer.1"
    Then the response status should be 200
    And the trainer summary should equal to body:
    """
    {
      "username": "trainer.1",
      "firstName": "trainer",
      "lastName": "1",
      "status": false,
      "years": [
        {
          "year": 2024,
          "months": [
            {
              "month": 1,
              "durationMinutes": 10
            }
          ]
        } ,
        {
          "year": 2020,
          "months": [
            {
              "month": 10,
              "durationMinutes": 60
            }
          ]
        }
      ]
    }
    """
    When user sends request to monolith to logout
    Then the response status should be 200

  Scenario: User creates Trainee, Trainer, 2 Trainings with same year and different month and Microservice creates 2 new TrainerWorkloads
    When user sends request to monolith to create trainee with body:
    """
    {
      "firstName": "trainee",
      "lastName": "2",
      "dateOfBirth": "2024-11-21",
      "address": "string"
    }
    """
    Then the response status should be 200
    When user sends request to monolith to create trainer and get trainer password with body:
    """
    {
      "firstName": "trainer",
      "lastName": "2",
      "specializationId": 1
    }
    """
    Then the response status should be 200
    When user sends request to monolith to login as a trainer and get auth token with username = "trainer.2" and saved password
    Then the response status should be 200
    When authorized user sends request to monolith to create training with body:
    """
    {
      "traineeUsername": "trainee.2",
      "trainerUsername": "trainer.2",
      "trainingName": "string",
      "trainingDate": "2024-01-09",
      "trainingDurationMinutes": 10,
      "trainingTypeId": 1
    }
    """
    Then the response status should be 200
    When authorized user sends request to monolith to create training with body:
    """
    {
      "traineeUsername": "trainee.2",
      "trainerUsername": "trainer.2",
      "trainingName": "string",
      "trainingDate": "2024-10-01",
      "trainingDurationMinutes": 60,
      "trainingTypeId": 1
    }
    """
    Then the response status should be 200
    And wait for 5 seconds
    When user sends request to microservice to get trainer summary with username "trainer.2"
    Then the response status should be 200
    And the trainer summary should equal to body:
    """
    {
      "username": "trainer.2",
      "firstName": "trainer",
      "lastName": "2",
      "status": false,
      "years": [
        {
          "year": 2024,
          "months": [
            {
              "month": 1,
              "durationMinutes": 10
            },
            {
              "month": 10,
              "durationMinutes": 60
            }
          ]
        }
      ]
    }
    """
    When user sends request to monolith to logout
    Then the response status should be 200

  Scenario: User creates Trainee, Trainer, 2 Trainings with same year and same month and Microservice creates 2 new TrainerWorkloads
    When user sends request to monolith to create trainee with body:
    """
    {
      "firstName": "trainee",
      "lastName": "3",
      "dateOfBirth": "2024-11-21",
      "address": "string"
    }
    """
    Then the response status should be 200
    When user sends request to monolith to create trainer and get trainer password with body:
    """
    {
      "firstName": "trainer",
      "lastName": "3",
      "specializationId": 1
    }
    """
    Then the response status should be 200
    When user sends request to monolith to login as a trainer and get auth token with username = "trainer.3" and saved password
    Then the response status should be 200
    When authorized user sends request to monolith to create training with body:
    """
    {
      "traineeUsername": "trainee.3",
      "trainerUsername": "trainer.3",
      "trainingName": "string",
      "trainingDate": "2024-01-09",
      "trainingDurationMinutes": 10,
      "trainingTypeId": 1
    }
    """
    Then the response status should be 200
    When authorized user sends request to monolith to create training with body:
    """
    {
      "traineeUsername": "trainee.3",
      "trainerUsername": "trainer.3",
      "trainingName": "string",
      "trainingDate": "2024-01-02",
      "trainingDurationMinutes": 60,
      "trainingTypeId": 1
    }
    """
    Then the response status should be 200
    And wait for 5 seconds
    When user sends request to microservice to get trainer summary with username "trainer.3"
    Then the response status should be 200
    And the trainer summary should equal to body:
    """
    {
      "username": "trainer.3",
      "firstName": "trainer",
      "lastName": "3",
      "status": false,
      "years": [
        {
          "year": 2024,
          "months": [
            {
              "month": 1,
              "durationMinutes": 70
            }
          ]
        }
      ]
    }
    """
    When user sends request to monolith to logout
    Then the response status should be 200

  Scenario: User creates Trainee, Trainer, adds Training and deletes it and Microservice has no data
    When user sends request to monolith to create trainee with body:
    """
    {
      "firstName": "trainee",
      "lastName": "4",
      "dateOfBirth": "2024-11-21",
      "address": "string"
    }
    """
    Then the response status should be 200
    When user sends request to monolith to create trainer and get trainer password with body:
    """
    {
      "firstName": "trainer",
      "lastName": "4",
      "specializationId": 1
    }
    """
    Then the response status should be 200
    When user sends request to monolith to login as a trainer and get auth token with username = "trainer.4" and saved password
    Then the response status should be 200
    When authorized user sends request to monolith to create training with body:
    """
    {
      "traineeUsername": "trainee.4",
      "trainerUsername": "trainer.4",
      "trainingName": "string",
      "trainingDate": "2024-01-09",
      "trainingDurationMinutes": 10,
      "trainingTypeId": 1
    }
    """
    Then the response status should be 200
    And wait for 5 seconds
    When user sends request to microservice to get trainer summary with username "trainer.4"
    Then the response status should be 200
    And the trainer summary should equal to body:
    """
    {
      "username": "trainer.4",
      "firstName": "trainer",
      "lastName": "4",
      "status": false,
      "years": [
        {
          "year": 2024,
          "months": [
            {
              "month": 1,
              "durationMinutes": 10
            }
          ]
        }
      ]
    }
    """
    When the user sends request to get trainings by trainer and date to get id of training with body:
    """
    {
      "username": "trainer.4"
    }
    """
    Then the response status should be 200
    When authorized user sends request to monolith to delete training with savedTrainingId
    Then the response status should be 204
    And wait for 5 seconds
    When user sends request to microservice to get trainer summary with username "trainer.4"
    Then the response status should be 404
    When user sends request to monolith to logout
    Then the response status should be 200

  Scenario: User creates Trainee, Trainer, adds 2 Trainings with the same month and deletes one of them and Microservice has right calculations
    When user sends request to monolith to create trainee with body:
    """
    {
      "firstName": "trainee",
      "lastName": "5",
      "dateOfBirth": "2024-11-21",
      "address": "string"
    }
    """
    Then the response status should be 200
    When user sends request to monolith to create trainer and get trainer password with body:
    """
    {
      "firstName": "trainer",
      "lastName": "5",
      "specializationId": 1
    }
    """
    Then the response status should be 200
    When user sends request to monolith to login as a trainer and get auth token with username = "trainer.5" and saved password
    Then the response status should be 200
    When authorized user sends request to monolith to create training with body:
    """
    {
      "traineeUsername": "trainee.5",
      "trainerUsername": "trainer.5",
      "trainingName": "string",
      "trainingDate": "2024-01-09",
      "trainingDurationMinutes": 10,
      "trainingTypeId": 1
    }
    """
    Then the response status should be 200
    When authorized user sends request to monolith to create training with body:
    """
    {
      "traineeUsername": "trainee.5",
      "trainerUsername": "trainer.5",
      "trainingName": "string",
      "trainingDate": "2024-01-11",
      "trainingDurationMinutes": 50,
      "trainingTypeId": 1
    }
    """
    Then the response status should be 200
    And wait for 5 seconds
    When user sends request to microservice to get trainer summary with username "trainer.5"
    Then the response status should be 200
    And the trainer summary should equal to body:
    """
    {
      "username": "trainer.5",
      "firstName": "trainer",
      "lastName": "5",
      "status": false,
      "years": [
        {
          "year": 2024,
          "months": [
            {
              "month": 1,
              "durationMinutes": 60
            }
          ]
        }
      ]
    }
    """
    When the user sends request to get trainings by trainer and date to get id of training with body:
    """
    {
      "username": "trainer.5",
      "startDate": "2024-01-01",
      "endDate": "2024-01-10",
      "traineeUsername": "trainee.5"
    }
    """
    Then the response status should be 200
    When authorized user sends request to monolith to delete training with savedTrainingId
    Then the response status should be 204
    And wait for 5 seconds
    When user sends request to microservice to get trainer summary with username "trainer.5"
    Then the response status should be 200
    And the trainer summary should equal to body:
    """
    {
      "username": "trainer.5",
      "firstName": "trainer",
      "lastName": "5",
      "status": false,
      "years": [
        {
          "year": 2024,
          "months": [
            {
              "month": 1,
              "durationMinutes": 50
            }
          ]
        }
      ]
    }
    """
    When user sends request to monolith to logout
    Then the response status should be 200

  Scenario: User creates Trainee, Trainer, adds 2 Trainings with different month and deletes one of them and Microservice has right calculations
    When user sends request to monolith to create trainee with body:
    """
    {
      "firstName": "trainee",
      "lastName": "6",
      "dateOfBirth": "2024-11-21",
      "address": "string"
    }
    """
    Then the response status should be 200
    When user sends request to monolith to create trainer and get trainer password with body:
    """
    {
      "firstName": "trainer",
      "lastName": "6",
      "specializationId": 1
    }
    """
    Then the response status should be 200
    When user sends request to monolith to login as a trainer and get auth token with username = "trainer.6" and saved password
    Then the response status should be 200
    When authorized user sends request to monolith to create training with body:
    """
    {
      "traineeUsername": "trainee.6",
      "trainerUsername": "trainer.6",
      "trainingName": "string",
      "trainingDate": "2024-01-09",
      "trainingDurationMinutes": 10,
      "trainingTypeId": 1
    }
    """
    Then the response status should be 200
    When authorized user sends request to monolith to create training with body:
    """
    {
      "traineeUsername": "trainee.6",
      "trainerUsername": "trainer.6",
      "trainingName": "string",
      "trainingDate": "2024-11-11",
      "trainingDurationMinutes": 50,
      "trainingTypeId": 1
    }
    """
    Then the response status should be 200
    And wait for 5 seconds
    When user sends request to microservice to get trainer summary with username "trainer.6"
    Then the response status should be 200
    And the trainer summary should equal to body:
    """
    {
      "username": "trainer.6",
      "firstName": "trainer",
      "lastName": "6",
      "status": false,
      "years": [
        {
          "year": 2024,
          "months": [
            {
              "month": 1,
              "durationMinutes": 10
            },
            {
              "month": 11,
              "durationMinutes": 50
            }
          ]
        }
      ]
    }
    """
    When the user sends request to get trainings by trainer and date to get id of training with body:
    """
    {
      "username": "trainer.6",
      "startDate": "2024-01-01",
      "endDate": "2024-01-10",
      "traineeUsername": "trainee.6"
    }
    """
    Then the response status should be 200
    When authorized user sends request to monolith to delete training with savedTrainingId
    Then the response status should be 204
    And wait for 5 seconds
    When user sends request to microservice to get trainer summary with username "trainer.6"
    Then the response status should be 200
    And the trainer summary should equal to body:
    """
    {
      "username": "trainer.6",
      "firstName": "trainer",
      "lastName": "6",
      "status": false,
      "years": [
        {
          "year": 2024,
          "months": [
            {
              "month": 11,
              "durationMinutes": 50
            }
          ]
        }
      ]
    }
    """
    When user sends request to monolith to logout
    Then the response status should be 200

  Scenario: User creates Trainee, Trainer, adds 2 Trainings with different years and deletes one of them and Microservice has right calculations
    When user sends request to monolith to create trainee with body:
    """
    {
      "firstName": "trainee",
      "lastName": "7",
      "dateOfBirth": "2024-11-21",
      "address": "string"
    }
    """
    Then the response status should be 200
    When user sends request to monolith to create trainer and get trainer password with body:
    """
    {
      "firstName": "trainer",
      "lastName": "7",
      "specializationId": 1
    }
    """
    Then the response status should be 200
    When user sends request to monolith to login as a trainer and get auth token with username = "trainer.7" and saved password
    Then the response status should be 200
    When authorized user sends request to monolith to create training with body:
    """
    {
      "traineeUsername": "trainee.7",
      "trainerUsername": "trainer.7",
      "trainingName": "string",
      "trainingDate": "2024-01-09",
      "trainingDurationMinutes": 10,
      "trainingTypeId": 1
    }
    """
    Then the response status should be 200
    When authorized user sends request to monolith to create training with body:
    """
    {
      "traineeUsername": "trainee.7",
      "trainerUsername": "trainer.7",
      "trainingName": "string",
      "trainingDate": "2000-11-11",
      "trainingDurationMinutes": 50,
      "trainingTypeId": 1
    }
    """
    Then the response status should be 200
    And wait for 5 seconds
    When user sends request to microservice to get trainer summary with username "trainer.7"
    Then the response status should be 200
    And the trainer summary should equal to body:
    """
    {
      "username": "trainer.7",
      "firstName": "trainer",
      "lastName": "7",
      "status": false,
      "years": [
        {
          "year": 2024,
          "months": [
            {
              "month": 1,
              "durationMinutes": 10
            }
          ]
        },
        {
          "year": 2000,
          "months": [
            {
              "month": 11,
              "durationMinutes": 50
            }
          ]
        }
      ]
    }
    """
    When the user sends request to get trainings by trainer and date to get id of training with body:
    """
    {
      "username": "trainer.7",
      "startDate": "2024-01-01",
      "endDate": "2024-01-10",
      "traineeUsername": "trainee.7"
    }
    """
    Then the response status should be 200
    When authorized user sends request to monolith to delete training with savedTrainingId
    Then the response status should be 204
    And wait for 5 seconds
    When user sends request to microservice to get trainer summary with username "trainer.7"
    Then the response status should be 200
    And the trainer summary should equal to body:
    """
    {
      "username": "trainer.7",
      "firstName": "trainer",
      "lastName": "7",
      "status": false,
      "years": [
        {
          "year": 2000,
          "months": [
            {
              "month": 11,
              "durationMinutes": 50
            }
          ]
        }
      ]
    }
    """
    When user sends request to monolith to logout
    Then the response status should be 200
