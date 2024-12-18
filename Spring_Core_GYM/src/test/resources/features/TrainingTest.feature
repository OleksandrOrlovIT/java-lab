@tagTrainingFeature
Feature: Training controller

  Scenario: Get training types then access denied
    When the user sends request to get trainingTypes
    Then the response status should be 403

  Scenario:  Get training types then successfully retrieves trainingTypes
    Given the user logs in with username "testtrainer" and password "password" to get token
    When  the user sends request to get trainingTypes
    Then the response status should be 200
    And the response body contain trainingType with name "testTrainingType1"

  Scenario:  Get trainings by trainee and date without body
    Given the user logs in with username "testtrainer" and password "password" to get token
    When  the user sends request to get trainings by trainee and date without body
    Then the response status should be 400
    And the response body should contain "Required request body is missing;BAD_REQUEST"

  Scenario:  Get trainings by trainee and date with invalid body
    Given the user logs in with username "testtrainer" and password "password" to get token
    When  the user sends request to get trainings by trainee and date with invalid body
    Then the response status should be 400
    And the response body should contain "username is required;BAD_REQUEST"

  Scenario:  Get trainings by trainee and date with nonexistent trainee
    Given the user logs in with username "testtrainer" and password "password" to get token
    Given the user gets all trainingTypes to get a specializationId
    When  the user sends request to get trainings by trainee and date with body:
    """
    {
      "username": "asdasdasd",
      "trainerUsername": "testtrainer"
    }
    """
    Then the response status should be 404
    And the exception response body should contain message "User not found with username = asdasdasd" and status "NOT_FOUND"

  Scenario:  Get trainings by trainee and date with different logged trainee
    Given the user logs in with username "testtrainee" and password "password" to get token
    Given the user gets all trainingTypes to get a specializationId
    When  the user sends request to get trainings by trainee and date with body:
    """
    {
      "username": "testtrainee2",
      "startDate": "2024-10-01",
      "endDate": "2024-11-01",
      "trainerUsername": "testtrainer"
    }
    """
    Then the response status should be 403
    And the exception response body should contain message "Access Denied" and status "FORBIDDEN"

  Scenario:  Get trainings by trainee and date returns no training
    Given the user logs in with username "testtrainee" and password "password" to get token
    Given the user gets all trainingTypes to get a specializationId
    When  the user sends request to get trainings by trainee and date with body:
    """
    {
      "username": "testtrainee",
      "startDate": "2024-11-01",
      "endDate": "2024-11-01",
      "trainerUsername": "testtrainer"
    }
    """
    Then the response status should be 200
    And the response body should contain trainings with size 0

  Scenario:  Get trainings by trainee and date returns 2 trainings
    Given the user logs in with username "testtrainee" and password "password" to get token
    Given the user gets all trainingTypes to get a specializationId
    When  the user sends request to get trainings by trainee and date with body:
    """
    {
      "username": "testtrainee",
      "startDate": "2024-10-01",
      "endDate": "2024-11-01",
      "trainerUsername": "testtrainer"
    }
    """
    Then the response status should be 200
    And the response body should contain trainings with size 2

  Scenario:  Get trainings by trainer and date without body
    Given the user logs in with username "testtrainee" and password "password" to get token
    When  the user sends request to get trainings by trainer and date without body
    Then the response status should be 400
    And the response body should not be null

  Scenario:  Get trainings by trainer and date with invalid body
    Given the user logs in with username "testtrainee" and password "password" to get token
    When  the user sends request to get trainings by trainer and date with invalid body
    Then the response status should be 400
    And the response body should contain "username is required;BAD_REQUEST"

  Scenario:  Get trainings by trainer and date with non existent trainer
    Given the user logs in with username "testtrainee" and password "password" to get token
    When  the user sends request to get trainings by trainer and date with body:
    """
    {
      "username": "asd"
    }
    """
    Then the response status should be 404
    And the exception response body should contain message "User not found with username = asd" and status "NOT_FOUND"

  Scenario:  Get trainings by trainer and date with different Trainer
    Given the user logs in with username "testtrainer" and password "password" to get token
    When  the user sends request to get trainings by trainer and date with body:
    """
    {
      "username": "testtrainer2",
      "startDate": "2024-10-01",
      "endDate": "2024-11-01",
      "traineeUsername": "testtrainee"
    }
    """
    Then the response status should be 403
    And the exception response body should contain message "Access Denied" and status "FORBIDDEN"

  Scenario:  Get trainings by trainer and date returns no trainings
    Given the user logs in with username "testtrainer2" and password "password" to get token
    When  the user sends request to get trainings by trainer and date with body:
    """
    {
      "username": "testtrainer2",
      "startDate": "2024-11-01",
      "endDate": "2024-11-01",
      "traineeUsername": "testtrainee2"
    }
    """
    Then the response status should be 200
    And the response body should contain trainings with size 0

  Scenario:  Get trainings by trainer and date returns 2 trainings
    Given the user logs in with username "testtrainer" and password "password" to get token
    When  the user sends request to get trainings by trainer and date with body:
    """
    {
      "username": "testtrainer",
      "startDate": "2024-10-01",
      "endDate": "2024-11-01",
      "traineeUsername": "testtrainee"
    }
    """
    Then the response status should be 200
    And the response body should contain trainings with size 2

  Scenario: Create training without body
    Given the user logs in with username "testtrainer" and password "password" to get token
    When  the user sends request to create training without body
    Then the response status should be 400
    And the response body should not be null

  Scenario: Create training with invalid body
    Given the user logs in with username "testtrainer" and password "password" to get token
    When  the user sends request create training with invalid body
    Then the response status should be 400
    And the response body should contain "trainingDurationMinutes is required;trainingName is required;trainingDate is required;traineeUsername is required;trainerUsername is required"

  Scenario: Create training with different logged user
    Given the user logs in with username "testtrainer" and password "password" to get token
    Given the user gets all trainingTypes to get a specializationId
    When  the user sends request to create training with body:
    """
    {
      "trainingName": "SomeTrainingName",
      "trainingDurationMinutes": 50,
      "trainingDate": "2024-10-01",
      "traineeUsername": "testtrainee",
      "trainerUsername": "testtrainer2"
    }
    """
    Then the response status should be 403
    And the exception response body should contain message "Access Denied" and status "FORBIDDEN"

  Scenario: Create training with non existent trainee
    Given the user logs in with username "testtrainer" and password "password" to get token
    Given the user gets all trainingTypes to get a specializationId
    When  the user sends request to create training with body:
    """
    {
      "trainingName": "SomeTrainingName",
      "trainingDurationMinutes": 50,
      "trainingDate": "2024-10-01",
      "traineeUsername": "NONEXISTENTTRAINEE",
      "trainerUsername": "testtrainer"
    }
    """
    Then the response status should be 404
    And the exception response body should contain message "Trainee not found NONEXISTENTTRAINEE" and status "NOT_FOUND"

  Scenario: Create training with logged trainee then success
    Given the user logs in with username "testtrainee" and password "password" to get token
    Given the user gets all trainingTypes to get a specializationId
    When  the user sends request to create training with body:
    """
    {
      "trainingName": "SomeTrainingName",
      "trainingDurationMinutes": 50,
      "trainingDate": "2024-10-01",
      "traineeUsername": "testtrainee",
      "trainerUsername": "TrainerForCreateTraining"
    }
    """
    Then the response status should be 200

  Scenario: Create training with logged trainer then success
    Given the user logs in with username "testtrainer" and password "password" to get token
    Given the user gets all trainingTypes to get a specializationId
    When  the user sends request to create training with body:
    """
    {
      "trainingName": "SomeTrainingName",
      "trainingDurationMinutes": 50,
      "trainingDate": "2024-10-01",
      "traineeUsername": "traineeForCreateTraining",
      "trainerUsername": "testtrainer"
    }
    """
    Then the response status should be 200

  Scenario: Create training with non existent trainingTypeId
    Given the user logs in with username "testtrainer" and password "password" to get token
    When  the user sends request to create training with wrong trainingTypeId and body:
    """
    {
      "trainingTypeId": "-1",
      "trainingName": "SomeTrainingName",
      "trainingDurationMinutes": 50,
      "trainingDate": "2024-10-01",
      "traineeUsername": "traineeForCreateTraining",
      "trainerUsername": "testtrainer"
    }
    """
    Then the response status should be 404
    And the exception response body should contain message "TrainingType not found with id = -1" and status "NOT_FOUND"

  Scenario: Delete training without trainingId
    Given the user logs in with username "testtrainer" and password "password" to get token
    When the user sends request to delete training without trainingId
    Then the response status should be 400
    And the response body should not be null

  Scenario: Delete training with non existent training id
    Given the user logs in with username "testtrainer" and password "password" to get token
    When the user sends request to delete training with trainingId -1
    Then the response status should be 404
    And the exception response body should contain message "Training not found with id = -1" and status "NOT_FOUND"

  Scenario: Delete training with different trainer
    Given the user logs in with username "testtrainer" and password "password" to get token
    Given the user gets a first training by trainer name "testtrainer"
    Given the user logs in with username "testtrainer2" and password "password" to get token
    When the user sends request to delete training with saved trainingId
    Then the response status should be 403
    And the exception response body should contain message "Logged user doesn't have access to the training" and status "FORBIDDEN"

  Scenario: Delete training then success
    Given the user logs in with username "testtrainer" and password "password" to get token
    Given the user gets a first training by trainer name "testtrainer"
    When the user sends request to delete training with saved trainingId
    Then the response status should be 204
