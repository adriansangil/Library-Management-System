# Library Management System

A simple REST application for managing books, borrowers and branches for a library.

## Getting Started

You will need to deploy the project on a local server to try.

### Prerequisites

What things you need to install the software and how to install them

```
Tomcat 6, Java 8
```

### Sample CRUD Methods



* **GET** : http://localhost:8080/lms/books

 *Retrieves a list of books from the database*
 
* **POST** : http://localhost:8080/lms/books

 *Adds a new book to the database using json/xml*

* **DELETE** : http://localhost:8080/lms/books/{bookId}

 *Deletes a book with the corresponding {bookId} from the database*

* **PUT** : http://localhost:8080/lms/books/{bookId}

 *Updates a book with the corresponding {bookId} with new information passed into the body*



* For the list of all APIs please see code under Controller Folder. *


## Deployment

Add additional notes about how to deploy this on a live system

## Built With

* [Spring](https://spring.io/) - The web framework used
* [Maven](https://maven.apache.org/) - Dependency Management
* [MySQL](https://www.mysql.com/) - databased used

## Authors

* **Adrian Sangil**


## License



## Acknowledgments

* Hat tip to anyone who's code was used
* Inspiration
* etc
