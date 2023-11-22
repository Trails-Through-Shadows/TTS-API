from fastapi import FastAPI

from .db.base import get_class_by_tablename
from .db.session import session

app = FastAPI()


@app.get("/db/{table}")
def get_from_database(table: str, id_in: int | None = None):
    # table_obj = get_class_by_tablename(table)
    # print(type(table_obj))
    # table_obj = getattr(__import__("src.models." + str.lower(table) + "." + str.lower(table), fromlist=[table]), table)

    table_obj = get_class_by_tablename(table)
    if table_obj is None:
        return "Not found"
    print("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", table_obj)
    if id_in is None:
        obj = session.query(table_obj).all()
    else:
        obj = session.query(table_obj).filter_by(id=id_in).all()

    return obj if obj is not None else "Not found in database"
