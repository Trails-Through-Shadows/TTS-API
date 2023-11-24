from fastapi import FastAPI

import asyncio

from .db.base import get_class_by_tablename
from .db.session import session

from pydantic import BaseModel, Field
from fastapi_filter.contrib.sqlalchemy import Filter
from fastapi import Query
from src.models.effect import Effect, EffectRange, EffectType
from fastapi_filter import FilterDepends, with_prefix
from typing import Annotated
from sqlalchemy import Column, ForeignKey, Integer, String, event, select

from .models.effect.effect import EffectParam

app = FastAPI()


@app.get("/db/{table}")
async def get_from_database(table: str, id_in: int | None = None) -> str:
    """
    Get all objects or one if ``id_in`` is specified from table in database.
    Return string with objects or error message.

    :param table: Name of table in database.
    :param id_in: ID of object in table.
    """
    table_obj = await get_class_by_tablename(table)
    if table_obj is None:
        return "Not found table in database"
    if id_in is None:
        obj = session.query(table_obj).all()
    else:
        obj = session.query(table_obj).filter_by(id=id_in).all()

    return str(obj)


@app.delete("/db/{table}")
def delete_from_database(table: str, id_in: list[int]):
    """
    Delete all objects in list or one if ``id`` is specified from table in database.
    Return string with objects or error message.

    :param table: Name of table in database.
    :param id_in: ID of object in table.
    """
    table_obj = get_class_by_tablename(table)
    if table_obj is None:
        return "Not found table in database"
    else:
        obj = session.query(table_obj).filter_by(id=id_in).all()

    # delete from database
    # for o in obj:
    #     session.delete(o)
    # session.commit()

    return obj


@app.get("/db/test/Effect")
def test(id_in: Annotated[int, None] = None):
    if id_in is not None:
        return session.query(Effect).filter_by(id=id_in).all()
