import enum
from sqlalchemy import Column, Integer, Enum
from src.db.base import Base


class EffectType(int, enum.Enum):
    PUSH = 1
    PULL = 2
    FORCED_MOVEMENT_IMMUNITY = 3
    POISON = 4
    POISON_IMMUNITY = 5
    FIRE = 6
    FIRE_IMMUNITY = 7
    BLEED = 8
    BLEED_IMMUNITY = 9
    DISARM = 10
    DISARM_IMMUNITY = 11
    STUN = 12
    STUN_IMMUNITY = 13
    CONFUSION = 14
    CONFUSION_IMMUNITY = 15
    CHARM = 16
    CHARM_IMMUNITY = 17
    FEAR = 18
    FEAR_IMMUNITY = 19
    INVISIBILITY = 20
    SHIELD = 21
    BONUS_HEALTH = 22
    BONUS_DAMAGE = 23
    BONUS_MOVEMENT = 24

    def __repr__(self):
        return self.name


class EffectRange(enum.Enum):
    SELF = 1
    ONE = 2
    ALL = 3

    def __repr__(self):
        return self.name


class Effect(Base):
    """
    Effect model

    "id"        INT [not null, increment]
    "type"      EffectType [not null]
    "duration"  INT [not null]
    "range"     EffectRange [not null]
    "strength"  INT
    """
    id = Column(Integer, primary_key=True)
    type = Column(Enum(EffectType), nullable=False)
    duration = Column(Integer, nullable=False)
    range = Column(Enum(EffectRange), nullable=False)
    strength = Column(Integer, nullable=True)

    def __repr__(self):
        return f"<Effect(id={self.id}, type={self.type}, duration={self.duration}, range={self.range}, strength={self.strength})>"

    def __str__(self):
        return (f"Effect "
                f"  id={self.id}, "
                f"  type={self.type.value}, "
                f"  duration={self.duration}, "
                f"  range={self.range.value}, "
                f"  strength={self.strength} ")
