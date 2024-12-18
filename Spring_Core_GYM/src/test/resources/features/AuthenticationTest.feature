@tagAuthenticationFeature
Feature: Authentication controller

  Scenario: Successful login
    When the user logs in with username "encryptedUser" and password "password"
    Then the response status should be 200
    And the response body should not be null

  Scenario: Login with wrong password
    When the user logs in with username "encryptedUser" and password "asdasd"
    Then the response status should be 401
    And the response body should not be null

  Scenario: Login with wrong username
    When the user logs in with username "sdasdasd" and password "asdasd"
    Then the response status should be 401
    And the response body should not be null

  Scenario: Login with null fields
    When the user logs in with null fields
    Then the response status should be 400
    And the response body should contain "password is required;username is required"

  Scenario: Change login with non-authorized user
    Given the user logs in with username "changePasswordUser" and password "password" to get token
    When user sends changeLoginDto with username "testUser" oldPassword "password" newPassword "newPassword"
    Then the response status should be 403
    And the exception response body should contain message "Access Denied" and status "FORBIDDEN"

  Scenario: Change login with wrong oldPassword
    Given the user logs in with username "changePasswordUser2" and password "password" to get token
    When user sends changeLoginDto with username "changePasswordUser2" oldPassword "asdasdasd" newPassword "newPasswor"
    Then the response status should be 401
    And the exception response body should contain message "Wrong password = asdasdasd" and status "UNAUTHORIZED"

  Scenario: Change login with wrong username
    Given the user logs in with username "changePasswordUser" and password "password" to get token
    When user sends changeLoginDto with username "someNonExistentUserUser" oldPassword "password" newPassword "newPasswor"
    Then the response status should be 404
    And the exception response body should contain message "User not found with username = someNonExistentUserUser" and status "NOT_FOUND"

  Scenario: Change login with valid data
    Given the user logs in with username "changePasswordUser" and password "password" to get token
    When user sends changeLoginDto with username "changePasswordUser" oldPassword "password" newPassword "newPasswor"
    Then the response status should be 200
    And the response body should equal "You successfully changed password"

  Scenario: Change login without body
    Given the user logs in with username "testUser" and password "password" to get token
    When user sends changeLoginDto without body
    Then the response status should be 400
    And the response body should not be null

  Scenario: Change login without body
    Given the user logs in with username "testUser" and password "password" to get token
    When user sends changeLoginDto with username "" oldPassword "" newPassword ""
    Then the response status should be 400
    And the response body should contain "username is required;oldPassword is required;newPassword is required"

  Scenario: Logout with valid data
    Given the user logs in with username "testUser" and password "password" to get token
    When user sends request to logout
    Then the response status should be 200
    And the response body should equal "Logged out successfully."

  Scenario: Login wrong 4 times
    Given the user logs in with bad credentials username "asdasdasd" password "password" with 3 times
    When the user logs in with username "asdasdsadas" and password "password"
    Then the response status should be 429
    And the exception response body should contain message "Too many attempts" and status "TOO_MANY_REQUESTS"
