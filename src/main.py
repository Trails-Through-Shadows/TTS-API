from fastapi import FastAPI

from .db.base import get_class_by_tablename
from .db.session import session

app = FastAPI()


@app.get("/db/{table}")
def get_from_database(table: str, id: int | None = None):
    table_obj = get_class_by_tablename(table)
    if id is None:
        obj = session.query(table_obj).all()
    else:
        obj = session.query(table_obj).filter_by(id=id).all()

    return obj
