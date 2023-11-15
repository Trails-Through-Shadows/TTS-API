## Planning:

- Before diving into code, map out the resources your API will provide. For instance, in a bookstore API, resources might encompass Books, Authors, and Orders.

## Setting Up the Development Environment:

- Opt for an environment tailored to your chosen tech stack. For instance, if you're using Node.js, consider Express.js as a framework and Postman for testing.

## Defining Endpoints:

### For each resource, define the CRUD operations. For a Books resource, you might have:

- POST /books to add a new book.
- GET /books to fetch all books.
- GET /books/:id to retrieve a specific book.
- PUT /books/:id to update a book's details.
- DELETE /books/:id to remove a book.

## Implementing CRUD Operations:

- Develop the backend logic for each endpoint, ensuring robust error handling.

## Setting Up Authentication and Authorization:

- Implement security measures, such as JWT, to ensure that only authorized users can access certain parts of your API.

## Testing:

- Before deployment, rigorously test each endpoint. Consider using automated testing tools for efficiency.

## Documentation:

- Properly document your API using tools like Swagger, ensuring it's accessible and understandable for other developers.

<br><br><br>

# Evergereen best practices

## Name Your Endpoints Appropriately

- Adhere to conventions like:

Using nouns instead of verbs (e.g., "https://api.example.com/users" instead of “https://api.example.com/getUsers").

Using lowercase ASCII characters.

Separating words with hyphens (e.g., "https://api.example.com/user-ids" instead of “https://api.example.com/user_ids").
