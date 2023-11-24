# About

## Technology

- python flask restful
    - nvm prob fastAPI
    - https://fastapi.tiangolo.com/tutorial/first-steps/


- for parsing using pydantic
    - https://docs.pydantic.dev/latest/why/

- framework comparison
    - https://www.linkedin.com/pulse/choosing-right-framework-building-api-django-vs-flask-majid-sheikh#:~:text=Django%20is%20well%2Dsuited%20for,ORM%2C%20Django%20has%20you%20covered.


- json all in

## Inspirative projects

- https://github.com/tiangolo/full-stack-fastapi-postgresql
    - under construction
    - old fast api

## How to run

1. Create virtual environment
    - `python3 -m venv .venv`
    - `source .venv/bin/activate`
2. Install dependencies
    - `pip install -r requirements.txt`
3. Create .env file
    - `cp .env.example .env`
    - update .env file with your credentials
4. Run app
    - `uvicorn src.main:app --reload`