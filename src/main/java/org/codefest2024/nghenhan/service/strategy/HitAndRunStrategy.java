package org.codefest2024.nghenhan.service.strategy;

import org.codefest2024.nghenhan.service.socket.data.*;
import org.codefest2024.nghenhan.service.usecase.*;
import org.codefest2024.nghenhan.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class HitAndRunStrategy implements Strategy {
    private final FindBadge findBadge = new FindBadge();
    private final KeepDistance keepDistance = new KeepDistance();
    private final UseSkill useSkill = new UseSkill();
    private final Dodge dodge = new Dodge();
    private final RandomRun randomRun = new RandomRun();
    private final FarmBrick farmBrick = new FarmBrick();
    private final OptimalFarmBox optimalFarmBox = new OptimalFarmBox();
    private final CollectSpoils collectSpoils = new CollectSpoils();
    private final CollectWeapon collectWeapon = new CollectWeapon();
    private final StunAndBomb stunAndBomb = new StunAndBomb();
    private final PowerFarmStrategy powerFarmStrategy = new PowerFarmStrategy();

    @Override
    public List<Order> find(GameInfo gameInfo) {
        MapInfo mapInfo = gameInfo.map_info;
        Player player = mapInfo.player;
        Player enemy = mapInfo.enemy;
        Player child = mapInfo.child;
        Player enemyChild = mapInfo.enemyChild;

        if(!mapInfo.playerIsMarried){
            return powerFarmStrategy.find(gameInfo);
        }

        if (player != null) {
            if (!player.hasTransform) {
                return findBadge.find(gameInfo, player);
            }
//            else if (enemy != null && !enemy.hasTransform) {
//                return farmBrick.farmBrick(mapInfo, player);
//            }
            else {
                List<Order> orders = playerStrategy(mapInfo, player, child, enemy, enemyChild);
                if (child != null) {
                    orders = new ArrayList<>(orders);
                    orders.addAll(playerStrategy(mapInfo, child, player, enemy, enemyChild));
                }
                return orders;
            }
        }

        return List.of();
    }

    private List<Order> playerStrategy(MapInfo mapInfo, Player player, Player teammate, Player enemy, Player enemyChild) {

        if (!player.isChild && player.eternalBadge > 0 && teammate == null && enemy != null) {
            return List.of(new Action(Action.MARRY_WIFE));
        }

        List<Order> useSkillOrders = useSkill.useMountainSkillDirect(mapInfo, player, teammate, enemy, enemyChild);
        if (!useSkillOrders.isEmpty()) {
            System.out.println("--Use skill--");
            useSkillOrders.forEach(System.out::println);
            return useSkillOrders;
        }

        List<Order> stunAndBombOrders = stunAndBomb.findAndBomb(mapInfo, player, Utils.filterNonNull(enemy, enemyChild));
        if (!stunAndBombOrders.isEmpty()) {
            System.out.println("--Stun and Bomb--");
            stunAndBombOrders.forEach(System.out::println);
            return stunAndBombOrders;
        }

        List<Order> keepEnemiesDistanceOrders = keepDistance.keepEnemiesDistance(mapInfo, player, Utils.filterNonNull(enemy, enemyChild));
        if (!keepEnemiesDistanceOrders.isEmpty()) {
            System.out.println("--Keep enemy distance--");
            keepEnemiesDistanceOrders.forEach(System.out::println);
            return keepEnemiesDistanceOrders;
        }

        List<Order> dodgeBombsOrders = dodge.findAndKeepDistance(mapInfo, player, teammate, enemy, enemyChild);
        if (!dodgeBombsOrders.isEmpty()) {
            System.out.println("--Dodge--");
            dodgeBombsOrders.forEach(System.out::println);
            return dodgeBombsOrders;
        }


//        List<Order> dodgeBombsOrders = dodge.find(mapInfo, player);
//        if (!dodgeBombsOrders.isEmpty()) {
//            return dodgeBombsOrders;
//        }

        List<Order> collectWeaponOrders = collectWeapon.find(mapInfo, player, enemy, enemyChild);
        if (!collectWeaponOrders.isEmpty()) {
            System.out.println("--Collect weapon-");
            useSkillOrders.forEach(System.out::println);
            return collectWeaponOrders;
        }

//        List<Order> keepTeammateDistanceOrders = keepDistance.keepTeammateDistance(mapInfo, player, teammate);
//        if (!keepTeammateDistanceOrders.isEmpty()) {
//            System.out.println("--Keep teammate distance--");
//            keepTeammateDistanceOrders.forEach(System.out::println);
//            return keepTeammateDistanceOrders;
//        }

        if(!mapInfo.playerIsMarried){
            List<Order> collectSpoilOrders = collectSpoils.findVer2(mapInfo, player);
            if (!collectSpoilOrders.isEmpty()) {
                return collectSpoilOrders;
            }
        }

        List<Order> godFarmOrders = optimalFarmBox.findVer2(mapInfo, player);
        if (!godFarmOrders.isEmpty()) {
            return godFarmOrders;
        }

        List<Order> farmBrickOrders = farmBrick.farmBrick(mapInfo, player, Utils.filterNonNull(enemy, enemyChild));
        if (!farmBrickOrders.isEmpty()) {
            System.out.println("--Farm brick--");
            farmBrickOrders.forEach(System.out::println);
            return farmBrickOrders;
        }

        List<Order> randomRunOrders = randomRun.find(mapInfo, player);
        if (!randomRunOrders.isEmpty()) {
            System.out.println("--Run ramdom--");
            randomRunOrders.forEach(System.out::println);
            return randomRunOrders;
        }

        return List.of();
    }
}
