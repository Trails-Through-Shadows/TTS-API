from sqlalchemy.engine import Engine

from src.db.base import Base


def db_init(engine: Engine):
    Base.metadata.create_all(bind=engine)
