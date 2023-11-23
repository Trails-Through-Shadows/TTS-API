from fastapi import FastAPI

from .db.base import get_class_by_tablename
from .db.session import session

app = FastAPI()


@app.get("/db/{table}")
def get_from_database(table: str, id_in: int | None = None) -> str:
    """
    Get all objects or one if ``id_in`` is specified from table in database.
    Return string with objects or error message.

    :param table: Name of table in database.
    :param id_in: ID of object in table.
    """

    table_obj = get_class_by_tablename(table)
    if table_obj is None:
        return "Not found table in database"
    if id_in is None:
        obj = session.query(table_obj).all()
    else:
        obj = session.query(table_obj).filter_by(id=id_in).all()

    return obj if obj is not None else "There are no objects in this table"
