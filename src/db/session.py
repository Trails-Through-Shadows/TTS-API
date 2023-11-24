import os

from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker

from .init_db import db_init

# todo make config file or smthng
#   plus session checking (any type of login)

# throw if .env file not exists
if not os.path.exists("src/db/.env"):
    raise Exception("No .env file in src/db/ directory")

# read conn_params from .env file
conn_params = {}
with open("src/db/.env", "r") as f:
    for line in f:
        try:
            key, value = line.split("=")
            conn_params[key] = value.strip()
        except ValueError:
            continue

# create db url
db_url = ("mariadb+mariadbconnector://" +
          conn_params['user'] + ":" +
          conn_params['password'] + "@" +
          conn_params['host'] + ":" +
          conn_params["port"] + "/" +
          conn_params["database"])
print("DB url:  ", db_url)

# create engine and session
engine = create_engine(db_url, pool_pre_ping=True)
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)

# init engine
db_init(engine)

# create session
SessionLocal.configure(bind=engine)
session = SessionLocal()
