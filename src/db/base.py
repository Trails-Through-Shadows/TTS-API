from sqlalchemy.orm import as_declarative, declared_attr


@as_declarative()
class Base:
    id: int
    __name__: str

    # Generate __tablename__ automatically
    @declared_attr
    def __tablename__(cls) -> str:
        return cls.__name__


def get_class_by_tablename(tablename: str) -> Base | None:
    """Return class reference mapped to table.

    :param tablename: String with name of table.
    :return: Class reference or None.
    """
    for c in Base.registry._class_registry.values():
        if hasattr(c, "__tablename__") and c.__tablename__ == tablename:
            return c
    return None
