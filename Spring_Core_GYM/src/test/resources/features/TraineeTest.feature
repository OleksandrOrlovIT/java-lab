@tagTraineeFeature
Feature: Trainee controller

  Scenario: Register trainee without body
    When user register trainee without body
    Then the response status should be 400

  Scenario: Register trainee with invalid body
    When user register trainee without any information
    Then the response status should be 400
    And the response body should contain "Last name is required;First name is required"

  Scenario: Register trainee with valid body
    When user registers trainee with the following details:
    """
    {
        "address": "Address",
        "dateOfBirth": "2020-10-10",
        "firstName": "FirstName",
        "lastName": "LastName"
    }
    """
    Then the response status should be 200
    And the response body should contain "FirstName.LastName"

    Scenario: Get trainee without body
      Given the user logs in with username "testtrainee" and password "password" to get token
      When user get trainee without body
      Then the response status should be 400
      And the response body should not be null

    Scenario: Get trainee with empty body
      Given the user logs in with username "testtrainee" and password "password" to get token
      When user get trainee with empty body
      Then the response status should be 400
      And the response body should contain "username is required;BAD_REQUEST"

  Scenario: Get trainee with non existent user
    Given the user logs in with username "testtrainee" and password "password" to get token
    When user get trainee with username "asdasdsad"
    Then the response status should be 404
    And the exception response body should contain message "User not found with username = asdasdsad" and status "NOT_FOUND"

  Scenario: Get trainee with non logged user
    Given the user logs in with username "testtrainee" and password "password" to get token
    When user get trainee with username "testtrainee2"
    Then the response status should be 403
    And the exception response body should contain message "Access Denied" and status "FORBIDDEN"

  Scenario: Get trainee then successfully retrieves trainee
    Given the user logs in with username "testtrainee" and password "password" to get token
    When user get trainee with username "testtrainee"
    Then the response status should be 200
    And the response body should not be null

  Scenario: Update trainee without body
    Given the user logs in with username "updateTrainee" and password "password" to get token
    When user update trainee without body
    Then the response status should be 400
    And the response body should not be null

  Scenario: Update trainee with invalid body
    Given the user logs in with username "updateTrainee" and password "password" to get token
    When user update trainee with invalid body
    Then the response status should be 400
    And the response body should contain "firstName is required;lastName is required;username is required"

  Scenario: Update trainee with different logged trainee
    Given the user logs in with username "updateTrainee" and password "password" to get token
    When user update trainee with a body:
    """
    {
        "username": "testtrainee",
        "isActive": true,
        "firstName": "newValue",
        "lastName": "newValue",
        "address": "newValue",
        "dateOfBirth": "2020-10-10"
    }
    """
    Then the response status should be 403
    And the exception response body should contain message "Access Denied" and status "FORBIDDEN"


  Scenario: Update trainee with non existent trainee
    Given the user logs in with username "updateTrainee" and password "password" to get token
    When user update trainee with a body:
    """
    {
        "username": "asdasdasd",
        "isActive": true,
        "firstName": "newValue",
        "lastName": "newValue",
        "address": "newValue",
        "dateOfBirth": "2020-10-10"
    }
    """
    Then the response status should be 404
    And the exception response body should contain message "User not found with username = asdasdasd" and status "NOT_FOUND"

  Scenario: Update trainee with valid fields
    Given the user logs in with username "updateTrainee" and password "password" to get token
    When user update trainee with a body:
    """
    {
        "username": "updateTrainee",
        "isActive": true,
        "firstName": "newValue",
        "lastName": "newValue",
        "address": "newValue",
        "dateOfBirth": "2020-10-10"
    }
    """
    Then the response status should be 200
    And the trainee response should equal:
    """
    {
        "username": "updateTrainee",
        "firstName": "newValue",
        "lastName": "newValue",
        "dateOfBirth": "2020-10-10",
        "address": "newValue",
        "isActive": true,
        "trainers": []
    }
    """

  Scenario: Delete trainee without body
    Given the user logs in with username "testtrainee" and password "password" to get token
    When user delete trainee without body
    Then the response status should be 400
    And the response body should not be null

  Scenario: Delete trainee with invalid body
    Given the user logs in with username "testtrainee" and password "password" to get token
    When user delete trainee with invalid body
    Then the response status should be 400
    And the response body should contain "username is required;BAD_REQUEST"

  Scenario: Delete trainee with non existent trainee
    Given the user logs in with username "testtrainee" and password "password" to get token
    When user delete trainee with username "asdasd"
    Then the response status should be 404
    And the exception response body should contain message "User not found with username = asdasd" and status "NOT_FOUND"

  Scenario: Delete trainee with not logged trainee
    Given the user logs in with username "testtrainee" and password "password" to get token
    When user delete trainee with username "updateTrainee"
    Then the response status should be 403
    And the exception response body should contain message "Access Denied" and status "FORBIDDEN"

  Scenario: Delete trainee by username then success
    Given the user logs in with username "deleteTrainee" and password "password" to get token
    When user delete trainee with username "deleteTrainee"
    Then the response status should be 204

  Scenario: Update trainee's trainers without body
    Given the user logs in with username "updateTraineeTrainersUser" and password "password" to get token
    When user update trainee's trainers without body
    Then the response status should be 400
    And the response body should not be null

  Scenario: Update trainee's trainers with invalid body
    Given the user logs in with username "updateTraineeTrainersUser" and password "password" to get token
    When user update trainee's trainers with invalid body
    Then the response status should be 400
    And the response body should contain "username is required;trainers are required"

  Scenario: Update trainee's trainers with non existent trainee
    Given the user logs in with username "updateTraineeTrainersUser" and password "password" to get token
    When user update trainee's trainers with body:
    """
    {
      "username": "asd",
      "trainers": [
          {"username": "testtrainer"}
      ]
    }
    """
    Then the response status should be 404
    And the exception response body should contain message "User not found with username = asd" and status "NOT_FOUND"

  Scenario: Update trainee's trainers with non existent trainer
    Given the user logs in with username "updateTraineeTrainersUser" and password "password" to get token
    When user update trainee's trainers with body:
    """
    {
      "username": "updateTraineeTrainersUser",
      "trainers": [
          {"username": "asd"}
      ]
    }
    """
    Then the response status should be 404
    And the exception response body should contain message "No trainers found with the provided userNames" and status "NOT_FOUND"

  Scenario: Update trainee's trainers with non logged trainee
    Given the user logs in with username "updateTraineeTrainersUser" and password "password" to get token
    When user update trainee's trainers with body:
    """
    {
      "username": "testtrainee",
      "trainers": [
          {"username": "testtrainer"}
      ]
    }
    """
    Then the response status should be 403
    And the exception response body should contain message "Access Denied" and status "FORBIDDEN"

  Scenario: Update trainee's trainers then success
    Given the user logs in with username "updateTraineeTrainersUser" and password "password" to get token
    When user update trainee's trainers with body:
    """
    {
      "username": "updateTraineeTrainersUser",
      "trainers": [
          {"username": "testtrainer"}
      ]
    }
    """
    Then the response status should be 200
    And the response body should contain trainer with username "testtrainer"

  Scenario: Activate deactivate trainee without body
    Given the user logs in with username "activatedTrainee" and password "password" to get token
    When user activate or deactivate trainee without body
    Then the response status should be 400
    And the response body should not be null

  Scenario: Activate deactivate trainee with invalid body
    Given the user logs in with username "activatedTrainee" and password "password" to get token
    When user activate or deactivate trainee with invalid body
    Then the response status should be 400
    And the response body should contain "username is required"

  Scenario: Activate deactivate trainee with different logged trainee
    Given the user logs in with username "activatedTrainee" and password "password" to get token
    When user activate or deactivate trainee with username "testtrainee" and isActive "true"
    Then the response status should be 403
    And the exception response body should contain message "Access Denied" and status "FORBIDDEN"

  Scenario: Activate deactivate trainee with non existent trainee
    Given the user logs in with username "activatedTrainee" and password "password" to get token
    When user activate or deactivate trainee with username "asd" and isActive "true"
    Then the response status should be 404
    And the exception response body should contain message "User not found with username = asd" and status "NOT_FOUND"

  Scenario: Activate deactivate trainee with same isActive
    Given the user logs in with username "deactivatedTrainee" and password "password" to get token
    When user activate or deactivate trainee with username "deactivatedTrainee" and isActive "true"
    Then the response status should be 400
    And the response body should contain "Trainee is already active"

  Scenario: Activate deactivate trainee with different isActive
    Given the user logs in with username "activatedTrainee" and password "password" to get token
    When user activate or deactivate trainee with username "activatedTrainee" and isActive "false"
    Then the response status should be 200
