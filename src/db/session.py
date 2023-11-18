from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker

from .init_db import db_init

conn_params = {
    'user': "communist",
    'password': "yourPasswordIsMyPassword",
    'host': "49.13.93.112",
    'port': "3306",
    "database": "communistBachelor"
}
db_url = ("mariadb+mariadbconnector://" +
          conn_params['user'] + ":" +
          conn_params['password'] + "@" +
          conn_params['host'] + ":" +
          conn_params["port"] + "/" +
          conn_params["database"])
print("DB url:", db_url)

# db_url = "jdbc:mariadb://49.13.93.112:3306/communistBachelor" # this is from datagrip
# db_url = "mariadb+mariadbconnector://49.13.93.112:3306/communistBachelor"

engine = create_engine(db_url, pool_pre_ping=True)
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)

db_init(engine)

SessionLocal.configure(bind=engine)
session = SessionLocal()
