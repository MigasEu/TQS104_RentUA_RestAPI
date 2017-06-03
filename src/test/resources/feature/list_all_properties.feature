Feature: Properties List
  Allow the student to be able to check all rooms/houses that are for rent in the "store".

Scenario: Enters the properties store
  Given a House located in the district of 'Aveiro' with the price per semester '500' 
  And a Room located in the district of 'Aveiro' with the price per semester '300'
  When the student enters the properties store
  Then 2 properties should have been found
    And House 1 should have the district 'Aveiro'
    And House 2 should have the district 'Aveiro'