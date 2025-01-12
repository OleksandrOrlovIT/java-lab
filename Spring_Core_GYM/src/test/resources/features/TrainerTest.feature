@tagTrainerFeature
Feature: Trainer controller

  Scenario: Register trainer without body
    When user register trainer without body
    Then the response status should be 400

  Scenario: Register trainer with invalid body
    When user register trainer without any information
    Then the response status should be 400
    And the response body should contain "Last name is required;First name is required"

  Scenario: Register trainer with valid body
    Given the user logs in with username "testtrainer1" and password "password" to get token
    Given the user gets all trainingTypes to get a specializationId
    When user registers trainer with the following details:
    """
    {
        "firstName": "FirstName",
        "lastName": "LastName"
    }
    """
    Then the response status should be 200
    And the response body should contain "FirstName.LastName"

  Scenario: Get trainer without body
    Given the user logs in with username "testtrainer1" and password "password" to get token
    When user get trainer without body
    Then the response status should be 400
    And the response body should not be null

  Scenario: Get trainer with empty body
    Given the user logs in with username "testtrainer1" and password "password" to get token
    When user get trainer with empty body
    Then the response status should be 400
    And the response body should contain "username is required;BAD_REQUEST"

  Scenario: Get trainer with non existent user
    Given the user logs in with username "testtrainer1" and password "password" to get token
    When user get trainer with username "asdasd"
    Then the response status should be 404
    And the exception response body should contain message "User not found with username = asdasd" and status "NOT_FOUND"

  Scenario: Get trainer with non logged user
    Given the user logs in with username "testtrainer1" and password "password" to get token
    When user get trainer with username "testtrainer2"
    Then the response status should be 403
    And the exception response body should contain message "Access Denied" and status "FORBIDDEN"

  Scenario: Get trainer then successfully retrieves trainer
    Given the user logs in with username "testtrainer1" and password "password" to get token
    When user get trainer with username "testtrainer1"
    Then the response status should be 200
    And the response body should not be null

  Scenario: Update trainer without body
    Given the user logs in with username "updateTrainer" and password "password" to get token
    When user update trainer without body
    Then the response status should be 400
    And the response body should not be null

  Scenario: Update trainer with invalid body
    Given the user logs in with username "updateTrainer" and password "password" to get token
    When user update trainer with invalid body
    Then the response status should be 400
    And the response body should contain "firstName is required;lastName is required;username is required"

  Scenario: Update trainer with different logged trainer
    Given the user logs in with username "updateTrainer" and password "password" to get token
    Given the user gets all trainingTypes to get a specializationId
    When user update trainer with a body:
    """
    {
        "username": "testtrainer1",
        "active": true,
        "firstName": "newValue",
        "lastName": "newValue"
    }
    """
    Then the response status should be 403
    And the exception response body should contain message "Access Denied" and status "FORBIDDEN"


  Scenario: Update trainer with non existent trainer
    Given the user logs in with username "updateTrainer" and password "password" to get token
    Given the user gets all trainingTypes to get a specializationId
    When user update trainer with a body:
    """
    {
        "username": "asdasdasd",
        "active": true,
        "firstName": "newValue",
        "lastName": "newValue"
    }
    """
    Then the response status should be 404
    And the exception response body should contain message "User not found with username = asdasdasd" and status "NOT_FOUND"

  Scenario: Update trainer with valid fields
    Given the user logs in with username "updateTrainer" and password "password" to get token
    Given the user gets all trainingTypes to get a specializationId
    When user update trainer with a body:
    """
    {
        "username": "updateTrainer",
        "active": true,
        "firstName": "newValue",
        "lastName": "newValue"
    }
    """
    Then the response status should be 200
    And the trainer response should equal:
    """
    {
        "username": "updateTrainer",
        "firstName": "newValue",
        "lastName": "newValue",
        "active": true,
        "trainees": []
    }
    """

  Scenario: Get trainers without trainee without body
    Given the user logs in with username "testtrainee1" and password "password" to get token
    When user get trainers without trainee without body
    Then the response status should be 400
    And the response body should not be null

  Scenario: Get trainers without trainee with invalid body
    Given the user logs in with username "testtrainee1" and password "password" to get token
    When user get trainers without trainee with invalid body
    Then the response status should be 400
    And the response body should contain "username is required;BAD_REQUEST"

  Scenario: Get trainers without trainee with non existent trainee
    Given the user logs in with username "testtrainee1" and password "password" to get token
    When user get trainers without trainee with username "asdasd"
    Then the response status should be 404
    And the exception response body should contain message "User not found with username = asdasd" and status "NOT_FOUND"

  Scenario: Get trainers without trainee with different logged trainee
    Given the user logs in with username "testtrainee1" and password "password" to get token
    When user get trainers without trainee with username "testtrainee2"
    Then the response status should be 403
    And the exception response body should contain message "Access Denied" and status "FORBIDDEN"

  Scenario: Get trainers without trainee then success
    Given the user logs in with username "testtrainee2" and password "password" to get token
    When user get trainers without trainee with username "testtrainee2"
    Then the response status should be 200
    And the response body should contain trainers list with more than size 2

  Scenario: Activate deactivate trainer without body
    Given the user logs in with username "deactivatedTrainer" and password "password" to get token
    When user activate or deactivate trainer without body
    Then the response status should be 400
    And the response body should not be null

  Scenario: Activate deactivate trainer with invalid body
    Given the user logs in with username "deactivatedTrainer" and password "password" to get token
    When user activate or deactivate trainer with invalid body
    Then the response status should be 400
    And the response body should contain "username is required"

  Scenario: Activate deactivate trainer with different logged trainer
    Given the user logs in with username "deactivatedTrainer" and password "password" to get token
    When user activate or deactivate trainer with username "activatedTrainer" and active "true"
    Then the response status should be 403
    And the exception response body should contain message "Access Denied" and status "FORBIDDEN"

  Scenario: Activate deactivate trainer with non existent trainer
    Given the user logs in with username "deactivatedTrainer" and password "password" to get token
    When user activate or deactivate trainer with username "asd" and active "true"
    Then the response status should be 404
    And the exception response body should contain message "User not found with username = asd" and status "NOT_FOUND"

  Scenario: Activate deactivate trainer with same active
    Given the user logs in with username "deactivatedTrainer" and password "password" to get token
    When user activate or deactivate trainer with username "deactivatedTrainer" and active "false"
    Then the response status should be 400
    And the response body should contain "Trainer is already deactivated"

  Scenario: Activate deactivate trainer with different active
    Given the user logs in with username "activatedTrainer" and password "password" to get token
    When user activate or deactivate trainer with username "activatedTrainer" and active "false"
    Then the response status should be 200
