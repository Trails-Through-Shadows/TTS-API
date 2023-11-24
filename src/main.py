from fastapi import FastAPI

from .db.base import get_class_by_tablename
from .db.session import session

from pydantic import BaseModel, Field
from fastapi_filter.contrib.sqlalchemy import Filter
from fastapi import Query
from src.models.effect import Effect, EffectRange, EffectType
from fastapi_filter import FilterDepends, with_prefix
from typing import Annotated
from sqlalchemy import Column, ForeignKey, Integer, String, event, select

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


# todo this is temporary and just for testing how Filter works
class EffectFilter(Filter):
    type: EffectType | None = None
    type__neq: EffectType | None = None
    range__lt: int | None = None
    range__gte: int | None = None  # Field(Query(description="range cannot be lesser than 0", default=0, ge=0)) # this is just testing how Query works
    order_by: list[str] = ["strength", "duration"]

    class Constants(Filter.Constants):
        model = Effect


@app.get("/db/test/Effect")
def test(id_in: Annotated[int, None] = None,
         filter_in: Annotated[EffectFilter, None] = FilterDepends(
             with_prefix("Effect", EffectFilter),
             by_alias=True)
         ):
    if id_in is not None:
        return session.query(Effect).filter_by(id=id_in).all()

    if filter_in is None:
        filter_in = EffectFilter()

    query = select(Effect)
    query = filter_in.filter(query)
    query = filter_in.sort(query)
    result = session.query(Effect).from_statement(query).all()
    return result
