from sqlalchemy.orm import as_declarative, declared_attr


@as_declarative()
class Base:
    """
    Base class for all models.

    This class provides:
    - id: int
    - __tablename__: str (generated automatically)
    """
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
    # find object with tablename in packages
    from itertools import chain

    def get_all_subclasses(cls) -> list:
        """
        Get all subclasses of input class recursively.

        // todo make this asynchronous

        :param cls: Class to get subclasses.
        :return: List of subclasses.
        """
        return list(
            chain.from_iterable(
                [list(chain.from_iterable([[x], get_all_subclasses(x)])) for x in cls.__subclasses__()])
        )

    for c in get_all_subclasses(Base):
        if hasattr(c, '__tablename__') and c.__tablename__ == tablename:
            # print("Found class:", c.__name__) // todo something like debug output
            return c
    return None
