Feature: Trainer controller scenarios

  Scenario: User sends change workload request without body
    When user sends change workload request without body
    Then the response status should be 400
    And the response body should contain "Required request body is missing"

  Scenario: User sends change workload request with invalid body
    When user sends change workload request with invalid body
    Then the response status should be 400
    And the response body should contain "trainingDurationMinutes is required;actionType is required;trainerUsername is required;trainingDate is required;trainerFirstName is required;trainerLastName is required"

  Scenario: User sends change workload request to add new workload and checks result
    When user sends change workload request with body:
    """
    {
      "trainerUsername": "username",
      "trainerFirstName": "firstName",
      "trainerLastName": "lastName",
      "trainerActive": "true",
      "trainingDate": "2020-10-10",
      "trainingDurationMinutes": "50",
      "actionType": "ADD"
    }
    """
    Then the response status should be 200
    And the response body should equal "Workload changed"
    When user sends request to get trainer summary with username "username"
    Then the response status should be 200
    And the trainer summary should equal to body:
    """
     {
      "username": "username",
      "firstName": "firstName",
      "lastName": "lastName",
      "status": true,
      "years": [
        {
          "year": 2020,
          "months": [
            {
              "month": 10,
              "durationMinutes": 50
            }
          ]
        }
      ]
    }
    """

  Scenario: User sends change workload request to add, check trainer and delete new workload
    When user sends change workload request with body:
    """
    {
      "trainerUsername": "username",
      "trainerFirstName": "firstName",
      "trainerLastName": "lastName",
      "trainerActive": "true",
      "trainingDate": "2020-10-10",
      "trainingDurationMinutes": "50",
      "actionType": "ADD"
    }
    """
    Then the response status should be 200
    And the response body should equal "Workload changed"
    When user sends request to get trainer summary with username "username"
    Then the response status should be 200
    And the trainer summary should equal to body:
    """
    {
      "username": "username",
      "firstName": "firstName",
      "lastName": "lastName",
      "status": true,
      "years": [
        {
          "year": 2020,
          "months": [
            {
              "month": 10,
              "durationMinutes": 50
            }
          ]
        }
      ]
    }
    """
    When user sends change workload request with body:
    """
    {
      "trainerUsername": "username",
      "trainerFirstName": "firstName",
      "trainerLastName": "lastName",
      "trainerActive": "true",
      "trainingDate": "2020-10-10",
      "trainingDurationMinutes": "50",
      "actionType": "DELETE"
    }
    """
    Then the response status should be 200
    And the response body should equal "Workload changed"
    When user sends request to get trainer summary with username "username"
    Then the response status should be 404
    And the exception response body should contain message "Trainer doesn't exist with username = username" and status "NOT_FOUND"

  Scenario: User sends change workload request delete non existent workload
    When user sends change workload request with body:
    """
    {
      "trainerUsername": "username",
      "trainerFirstName": "firstName",
      "trainerLastName": "lastName",
      "trainerActive": "true",
      "trainingDate": "2020-10-10",
      "trainingDurationMinutes": "50",
      "actionType": "DELETE"
    }
    """
    Then the response status should be 404
    And the exception response body should contain message "Trainer doesn't exist with username = username" and status "NOT_FOUND"

  Scenario: User sends change workload request without request param
    When user sends request to get trainer summary without request param
    Then the response status should be 400
    And the response body should contain "Required request parameter 'username' for method parameter type String is not present"

  Scenario: User sends change workload request with invalid body
    When user sends request to get trainer summary with username "username"
    Then the response status should be 404
    And the exception response body should contain message "Trainer doesn't exist with username = username" and status "NOT_FOUND"
