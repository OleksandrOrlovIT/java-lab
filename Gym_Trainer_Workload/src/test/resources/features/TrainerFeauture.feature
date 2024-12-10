Feature: Trainer controller scenarios

  Scenario: User sends change workload request without body
    When user sends change workload request without body
    Then the response status should be 400
    And the response body should contain "Required request body is missing"

  Scenario: User sends change workload request with invalid body
    When user sends change workload request with invalid body
    Then the response status should be 400
    And the response body should contain "trainingDurationMinutes is required;actionType is required;trainerUsername is required;trainingDate is required;trainerFirstName is required;trainerLastName is required"

  Scenario: User sends change workload request to add new workload
    When user sends change workload request with body:
    """
    {
      "trainerUsername": "username",
      "trainerFirstName": "firstName",
      "trainerLastName": "lastName",
      "trainerIsActive": "true",
      "trainingDate": "2020-10-10",
      "trainingDurationMinutes": "50",
      "actionType": "ADD"
    }
    """
    Then the response status should be 200
    And the response body should equal "Workload changed"

  Scenario: User sends change workload request to add and delete new workload
    When user sends change workload request with body:
    """
    {
      "trainerUsername": "username",
      "trainerFirstName": "firstName",
      "trainerLastName": "lastName",
      "trainerIsActive": "true",
      "trainingDate": "2020-10-10",
      "trainingDurationMinutes": "50",
      "actionType": "ADD"
    }
    """
    Then the response status should be 200
    And the response body should equal "Workload changed"
    When user sends change workload request with body:
    """
    {
      "trainerUsername": "username",
      "trainerFirstName": "firstName",
      "trainerLastName": "lastName",
      "trainerIsActive": "true",
      "trainingDate": "2020-10-10",
      "trainingDurationMinutes": "50",
      "actionType": "DELETE"
    }
    """
    Then the response status should be 200
    And the response body should equal "Workload changed"
