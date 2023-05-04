# usercenter-backend

## Features

- Register(Request type is post).
  - Account name cannot be less than four digits 
  - Account name cannot contain special characters(Regular expressions) 
  - Account name cannot contain sensitive words(Trie Tree)
  - Account cannot be duplicated
  - Password cannot be less than 8 digits 
  - Password and confirmation password must be the same
  - Passwords cannot be stored in plaintext in the database
- Login
  - Verify that the username and password are legitimate and correct
  - Save user login status(Session)
- User management(only for Admin): Query/Modify.