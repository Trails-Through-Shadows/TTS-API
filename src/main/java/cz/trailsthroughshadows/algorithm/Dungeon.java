//package cz.trailsthroughshadows.algorithm;
//
//import cz.trailsthroughshadows.algorithm.entity.Entity;
//import cz.trailsthroughshadows.algorithm.location.LocationImpl;
//import cz.trailsthroughshadows.algorithm.util.List;
//import cz.trailsthroughshadows.api.table.action.Action;
//import cz.trailsthroughshadows.api.table.action.attack.Attack;
//import cz.trailsthroughshadows.api.table.action.movement.Movement;
//import cz.trailsthroughshadows.api.table.action.restorecards.RestoreCards;
//import cz.trailsthroughshadows.api.table.action.skill.Skill;
//import cz.trailsthroughshadows.api.table.action.summon.Summon;
//import cz.trailsthroughshadows.api.table.action.summon.SummonAction;
//import cz.trailsthroughshadows.api.table.effect.model.Effect;
//import cz.trailsthroughshadows.api.table.enemy.model.Enemy;
//import cz.trailsthroughshadows.api.table.playerdata.character.model.Character;
//import cz.trailsthroughshadows.api.table.schematic.hex.model.Hex;
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import lombok.extern.slf4j.Slf4j;
//
//import java.util.ArrayList;
//import java.util.Collection;
//
//@Getter
//@AllArgsConstructor
//@Slf4j
//public class Dungeon {
//
//    private final ArrayList<Enemy> enemies = new ArrayList<>();
//    private final ArrayList<Character> characters = new ArrayList<>();
//    private final ArrayList<Summon> summons = new ArrayList<>();
//    private final LocationImpl location;
//
//    public String entityToString(Entity entity) {
//        StringBuilder res = new StringBuilder();
//        res.append("%s %s".formatted(entity.getClass().getSimpleName(), entity.getName()));
//
//        if (entity instanceof Character character)
//            res.append(" (%s)".formatted(character.getPlayerName()));
//
//        Hex hex = entity.getHex();
//        if (hex != null)
//            res.append(" [%d]".formatted(entity.getHex().getKey().getId()));
//        else res.append(" [no hex]");
//
//        return res.toString();
//    }
//
//    public boolean isAlly(Entity e1, Entity e2) {
//        if ((e1 instanceof Character || e1 instanceof Summon) && (e2 instanceof Character || e2 instanceof Summon)) {
//            return true;
//        }
//        return e1 instanceof Enemy && e2 instanceof Enemy;
//    }
//
//    public void applyEffect(Entity entity, Effect effect) {
//        log.info("\t\tApplying effect {} to {}", effect.toString(), entityToString(entity));
//
//        entity.getActiveEffects().add(effect);
//        // todo logic
//    }
//
//    public void damageEntity(Entity entity, int damage) {
//        log.info("\t\tDamaging {} for {}", entityToString(entity), damage);
//
//        switch (entity) {
//            case Character character -> log.info("\t\t\tCharacter can't be damaged yet"); // TODO
//            case Enemy enemy -> enemy.setBaseHealth(enemy.getBaseHealth() - damage);
//            case Summon summon -> summon.setHealth(summon.getHealth() - damage);
//            case null, default -> throw new IllegalStateException("Unexpected value: " + entity);
//        }
//
//        if (entity instanceof Enemy enemy) {
//            if (enemy.getBaseHealth() <= 0) {
//                log.info("\t\t{} died", entityToString(entity));
//                enemies.remove(enemy);
//            }
//        }
//        if (entity instanceof Summon summon) {
//            if (summon.getHealth() <= 0) {
//                log.info("\t\t{} died", entityToString(entity));
//                summons.remove(summon);
//            }
//        }
//    }
//
//    public void moveCharacter(Character character, Hex hex) {
//        character.setHex(hex);
//    }
//
//    public void moveEnemy(Enemy enemy, Hex hex) {
//        enemy.setHex(hex);
//    }
//
//    public java.util.List<Entity> calculateTarget(Entity entity, Effect.EffectTarget target) {
//        return calculateTarget(entity, target, 0);
//    }
//
//    public java.util.List<Entity> calculateTarget(Entity entity, Effect.EffectTarget target, int range) {
//        java.util.List<Entity> targets = new ArrayList<>();
//        Hex hex = entity.getHex();
//
//        log.info("Calculating target for " + entity + " at " + hex + " with range " + range + " and target " + target);
//
//        switch (target) {
//            case SELF:
//                targets.add(entity);
//                break;
//            case ALL_ALLIES:
//                targets.addAll(characters);
//                targets.addAll(summons);
//                break;
//            case ALL_ENEMIES:
//                targets.addAll(enemies);
//                break;
//            case ALL:
//                targets.addAll(List.union(characters, enemies, summons).stream()
//                        .filter(ent -> location.getDistance(hex, ent.getHex()) <= range)
//                        .toList());
//                break;
//            case ONE:
//                List.union(characters, enemies, summons).stream()
//                        .filter(ent -> location.getDistance(hex, ent.getHex()) <= range)
//                        .min((ent1, ent2) -> location.getDistance(hex, ent1.getHex()) - location.getDistance(hex, ent2.getHex()))
//                        .ifPresent(targets::add);
//                break;
//            default:
//                throw new IllegalStateException("Unexpected value: " + target);
//        }
//
//        return targets.stream().sorted((ent1, ent2) -> location.getDistance(hex, ent1.getHex()) - location.getDistance(hex, ent2.getHex())).toList();
//    }
//
//    public void evaluateMovement(Entity entity, Movement movement) {
//        log.info("Evaluating movement {} ({}) for {}", movement.getType(), movement.getRange(), entityToString(entity));
//
//        // todo i dont want to do movement
//    }
//
//    public void evaluateSummon(Entity entity, Summon summon, int range) {
//        log.info("\tEvaluating summon {} for {} with range {}", entityToString(summon), entityToString(entity), range);
//
//        // todo
//    }
//
//    public void evaluateSummons(Entity entity, Collection<SummonAction> summons) {
//        log.info("Evaluating {} summons for {}", summons.size(), entityToString(entity));
//
//        for (SummonAction summonAction : summons) {
//            evaluateSummon(entity, summonAction.getSummon(), summonAction.getRange());
//        }
//    }
//
//    public void evaluateSkill(Entity entity, Skill skill) {
//        log.info("Evaluating skill {} for {}", skill.toString(), entityToString(entity));
//
//        for (Effect effect : skill.getEffects()) {
//            java.util.List<Entity> targets = calculateTarget(entity, effect.getTarget(), skill.getRange());
//            log.info("\tSkill: {} targets with range {}", targets.size(), skill.getRange());
//            for (Entity target : targets) {
//                applyEffect(target, effect);
//            }
//        }
//    }
//
//    public void evaluateAttack(Entity entity, Attack attack) {
//        log.info("Evaluating attack {} for {}", attack.toString(), entityToString(entity));
//
//        for (int i = 0; i < attack.getNumAttacks(); i++) {
//            java.util.List<Entity> targets = calculateTarget(entity, attack.getTarget(), attack.getRange());
//            log.info("\tAttack {}: {} targets with range {}", i + 1, targets.size(), attack.getRange());
//            for (Entity target : targets) {
//                if (isAlly(entity, target)) {
//                    log.info("\t\t{} and {} are friends", entityToString(entity), entityToString(target));
//                    continue;
//                }
//
//                damageEntity(target, attack.getDamage());
//                for (Effect effect : attack.getEffects()) {
//                    applyEffect(target, effect);
//                }
//            }
//        }
//    }
//
//    public void evaluateRestoreCards(Entity entity, RestoreCards restoreCards) {
//        log.info("Evaluating restoreCards {} ({}) for {}", restoreCards.getNumCards(), restoreCards.getTarget(), entityToString(entity));
//        int remaining = restoreCards.getNumCards();
//
//        for (Entity target : calculateTarget(entity, restoreCards.getTarget())) {
//            if (!isAlly(entity, target)) continue;
//
//            log.info("\tRestoring cards for {}", entityToString(target));
////            for (Action action : target.getActions()) {
////                if (remaining == 0) {
////                    return;
////                }
////
////                if (action.getDiscarded() && action.getDiscard() != Action.Discard.PERMANENT) {
////                    action.setDiscarded(false);
////                    log.info("\t\tRestored {} for {}", action.getTitle(), entityToString(target));
////                    remaining -= 1;
////                }
////            }
//        }
//    }
//
//    public void evaluateAction(Entity entity, Action action) {
//        log.info("Evaluating action {} for {}", action.getTitle(), entityToString(entity));
//        log.info("\tAction: {}", action);
//        if (action.getMovement() != null) {
//            evaluateMovement(entity, action.getMovement());
//        }
//        if (action.getSummonActions() != null) {
//            evaluateSummons(entity, action.getSummonActions());
//        }
//        if (action.getSkill() != null) {
//            evaluateSkill(entity, action.getSkill());
//        }
//        if (action.getAttack() != null) {
//            evaluateAttack(entity, action.getAttack());
//        }
//        if (action.getRestoreCards() != null) {
//            evaluateRestoreCards(entity, action.getRestoreCards());
//        }
//
//        if (action.getDiscard() != Action.Discard.NEVER) {
//            action.setDiscarded(true);
//        }
//    }
//}
