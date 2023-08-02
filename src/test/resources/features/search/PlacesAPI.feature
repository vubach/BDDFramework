@PlacesAPI
Feature: Google Places API testing demo

@Find_Place
  Scenario Outline: Find Place API - Status code verification
    Given tester prepare "<api_key>"
     When tester create a new "GET" request to find place with "<request_params>"
     Then response status code should be "200"
      And the place search response should match with "<expected_status>" and there are place_ids in response
  
    Examples: 
      | test_id | title                                                               | api_key | request_params                       | expected_status | 
      | 1       | Verify find place form text with valid parameter: name, textquery   | VALID   | input=KMS , inputtype=textquery      | OK              | 
      | 21      | Verify find place form text with invalid parameter:name,phonenumber | VALID   | input=KMS , inputtype=phonenumber    | INVALID_REQUEST | 
      | 28      | Search place with no results found                                  | VALID   | input=#@!()(#@ , inputtype=textquery | ZERO_RESULTS    | 
      | 29      | Search place with wrong API Key                                     | INVALID | input=KMS , inputtype=textquery      | REQUEST_DENIED  | 
  
  @Place_Detail
  Scenario Outline: Place Detail API - Status code verification
    Given tester prepare "<api_key>"
     When tester create a new "GET" request to find place with "<request_params>"
     And tester create a new "GET" request to find place details with placeid got from place search
     Then response status code should be "200"
      And the place detail response should match with phone "<expected_phone>" and address "<expected_address>"
  
    Examples: 
      | test_id | title                                               | api_key | request_params                  | expected_phone | expected_address                                                     |
      | 31      | Verify place details with valid parameter: place_id | VALID   | input=KMS , inputtype=textquery | 028 3811 9977  | 2 Tản Viên, Phường 2, Tân Bình, Thành phố Hồ Chí Minh 70000, Vietnam |
  
  
