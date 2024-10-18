This is a wallet application built with Spring Boot, utilizing MVC, JPA, Security, JWT, and testing frameworks.

The application uses H2 as its database and Postman as its UI.

The flow of the application is as follows:
1- you jave to register first
**** API (POST) : http://127.0.0.1:8080/users/register ****
![image](https://github.com/user-attachments/assets/6488c081-4845-4a90-a06e-0c1332256fe4)


2- you have to login to get your TOKEN, after you got it use it in every other requests header
**** API (POST) : http://127.0.0.1:8080/users/login ****
![image](https://github.com/user-attachments/assets/aff5dc7f-78b8-49a4-9b21-b4299ea0d18b)

POINT: now you have access to other services.

3- you can get you personal information
**** API (GET) : http://127.0.0.1:8080/user ****
![image](https://github.com/user-attachments/assets/26c487bd-ead4-4e8d-905b-e5936f3d9276)

4- update (edit) your phone number, email or password
**** API (PUT) : http://127.0.0.1:8080/update/phoneNumber ****
![image](https://github.com/user-attachments/assets/ba252c6b-ef33-4401-a565-fc9db846bea4)
same for email and password.

5- add money to your account (charge)
**** API (POST) : http://127.0.0.1:8080/transaction/add-money ****
![image](https://github.com/user-attachments/assets/becf4558-7761-4d76-96b8-b63b9fa21e70)

6- transfer money to another existing account (de-charge)
**** API (POST) : http://127.0.0.1:8080/transaction/transfer-money ****
![image](https://github.com/user-attachments/assets/e89b831c-6aa9-4efa-b0c7-ba41a39cd79e)

7- get the list of all of your transactions
**** API (GET) : http://127.0.0.1:8080/transaction ****
![image](https://github.com/user-attachments/assets/023688e3-625b-40fa-baa3-8e0b63db015c)

8- delete your account
**** API (DELETE) : http://127.0.0.1:8080/delete-account ****
![image](https://github.com/user-attachments/assets/5469cba6-b2f2-45f8-bfa6-fe1da8dd5a6c)







