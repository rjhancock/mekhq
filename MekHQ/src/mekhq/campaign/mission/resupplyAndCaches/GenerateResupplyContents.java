/*
 * Copyright (C) 2024-2025 The MegaMek Team. All Rights Reserved.
 *
 * This file is part of MekHQ.
 *
 * MekHQ is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License (GPL),
 * version 3 or (at your option) any later version,
 * as published by the Free Software Foundation.
 *
 * MekHQ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * A copy of the GPL should have been included with this project;
 * if not, see <https://www.gnu.org/licenses/>.
 *
 * NOTICE: The MegaMek organization is a non-profit group of volunteers
 * creating free software for the BattleTech community.
 *
 * MechWarrior, BattleMech, `Mech and AeroTech are registered trademarks
 * of The Topps Company, Inc. All Rights Reserved.
 *
 * Catalyst Game Labs and the Catalyst Game Labs logo are trademarks of
 * InMediaRes Productions, LLC.
 */
package mekhq.campaign.mission.resupplyAndCaches;

import megamek.codeUtilities.ObjectUtility;
import megamek.common.Compute;
import megamek.common.annotations.Nullable;
import megamek.logging.MMLogger;
import mekhq.campaign.Campaign;
import mekhq.campaign.finances.Money;
import mekhq.campaign.mission.AtBContract;
import mekhq.campaign.mission.enums.AtBMoraleLevel;
import mekhq.campaign.mission.resupplyAndCaches.Resupply.ResupplyType;
import mekhq.campaign.parts.Part;
import mekhq.campaign.parts.enums.PartQuality;
import mekhq.campaign.universe.Faction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.lang.Math.min;
import static mekhq.campaign.mission.resupplyAndCaches.Resupply.RESUPPLY_AMMO_TONNAGE;
import static mekhq.campaign.mission.resupplyAndCaches.Resupply.RESUPPLY_ARMOR_TONNAGE;
import static mekhq.campaign.unit.Unit.getRandomUnitQuality;

/**
 * This class is responsible for generating resupply contents for various drop types,
 * such as parts, armor, and ammunition, based on specified parameters. It calculates
 * the items to be included, considering available resources, negotiator skills, and
 * item value constraints.
 */
public class GenerateResupplyContents {
    private static final MMLogger logger = MMLogger.create(GenerateResupplyContents.class);

    private static final Money HIGH_VALUE_ITEM = Money.of(250000);
    /**
     * Some parts are weightless, so we include a nominal weight for those.
     * <p>
     * This minimum value can be hand waved as being the weight of the container the part is in.
     */
    public static final double RESUPPLY_MINIMUM_PART_WEIGHT = 0.1;


    /**
     * Enum representing different types of drops that can be generated during resupply.
     * - `DROP_TYPE_PARTS`: Represents parts for resupply.
     * - `DROP_TYPE_ARMOR`: Represents armor for resupply.
     * - `DROP_TYPE_AMMO`: Represents ammunition for resupply.
     */
    public enum DropType {
        DROP_TYPE_PARTS, DROP_TYPE_ARMOR, DROP_TYPE_AMMO
    }

    /**
     * Generates the resupply contents based on the given {@link Resupply}, the specified
     * {@link DropType}, and whether player convoys should be used.
     *
     * @param resupply        The resupply object containing relevant details like
     *                        pools of parts, armor, ammo, and negotiator skill.
     * @param dropType        The type of drop to generate (parts, armor, or ammunition).
     * @param usePlayerConvoys Indicates whether player convoy cargo capacity should be applied.
     */
    static void getResupplyContents(Resupply resupply, DropType dropType, boolean usePlayerConvoys) {
        double targetCargoTonnage = resupply.getTargetCargoTonnage();
        if (usePlayerConvoys) {
            final int targetCargoTonnagePlayerConvoy = resupply.getTargetCargoTonnagePlayerConvoy();
            final double playerCargoCapacity = resupply.getTotalPlayerCargoCapacity();

            targetCargoTonnage = min(targetCargoTonnagePlayerConvoy, playerCargoCapacity);
        }

        List<Part> partsPool = resupply.getPartsPool();
        List<Part> armorPool = resupply.getArmorPool();
        List<Part> ammoBinPool = resupply.getAmmoBinPool();

        final int negotiatorSkill = resupply.getNegotiatorSkill();

        List<Part> droppedItems = new ArrayList<>();

        double availableSpace = switch (dropType) {
            case DROP_TYPE_PARTS -> targetCargoTonnage * resupply.getFocusParts();
            case DROP_TYPE_ARMOR -> targetCargoTonnage * resupply.getFocusArmor();
            case DROP_TYPE_AMMO -> targetCargoTonnage * resupply.getFocusAmmo();
        };

        if (availableSpace == 0) {
            return;
        }

        List<Part> relevantPartsPool = switch(dropType) {
            case DROP_TYPE_PARTS -> partsPool;
            case DROP_TYPE_ARMOR -> armorPool;
            case DROP_TYPE_AMMO -> ammoBinPool;
        };

        double currentLoad = 0;
        while ((currentLoad < availableSpace) && (!relevantPartsPool.isEmpty())) {
            Part potentialPart = switch(dropType) {
                case DROP_TYPE_PARTS -> getRandomDrop(partsPool, negotiatorSkill);
                case DROP_TYPE_ARMOR -> getRandomDrop(armorPool, negotiatorSkill);
                case DROP_TYPE_AMMO -> getRandomDrop(ammoBinPool, negotiatorSkill);
            };

            // If we failed to get a potential part, it likely means the pool is empty.
            // Even if the pool isn't empty, it's highly unlikely we'll get a successful pull on
            // future iterations, so we end generation early.
            if (potentialPart == null) {
                resupply.getConvoyContents().addAll(droppedItems);
                calculateConvoyWorth(resupply);
                logger.info("Encountered null part while getting resupply contents. Aborting early.");
                return;
            }

            boolean partFetched = false;

            // For particularly valuable items, we roll a follow-up die to see if the item
            // is actually picked, or if the supplier substitutes it with another item.
            if (potentialPart.getUndamagedValue().isGreaterThan(HIGH_VALUE_ITEM)) {
                if (Compute.d6(1) == 6) {
                    partFetched = true;
                }

                // For really expensive items, the player only has one chance per distinct part.
                switch (dropType) {
                    case DROP_TYPE_PARTS -> partsPool.removeAll(Collections.singleton(potentialPart));
                    case DROP_TYPE_ARMOR -> armorPool.removeAll(Collections.singleton(potentialPart));
                    case DROP_TYPE_AMMO -> ammoBinPool.removeAll(Collections.singleton(potentialPart));
                }
            } else {
                partFetched = true;
            }

            if (partFetched) {
                double partWeight = switch (dropType) {
                    case DROP_TYPE_PARTS -> {
                        partsPool.remove(potentialPart);
                        double tonnage = potentialPart.getTonnage();
                        yield tonnage == 0 ? RESUPPLY_MINIMUM_PART_WEIGHT : tonnage;
                    }
                    case DROP_TYPE_ARMOR -> {
                        armorPool.remove(potentialPart);
                        yield RESUPPLY_ARMOR_TONNAGE;
                    }
                    case DROP_TYPE_AMMO -> {
                        ammoBinPool.remove(potentialPart);
                        yield RESUPPLY_AMMO_TONNAGE;
                    }
                };

                currentLoad += partWeight;
                droppedItems.add(potentialPart);
            }
        }

        resupply.getConvoyContents().addAll(droppedItems);
        calculateConvoyWorth(resupply);
    }

    /**
     * Fetches a random part from the given pool of items and assigns a quality to the part,
     * based on the provided negotiator skill. If the pool is empty, returns {@code null}.
     *
     * @param dropPool       The list of potential items to choose from for resupply.
     * @param negotiatorSkill The skill level of the negotiator, affecting item quality.
     * @return A randomly selected part with an assigned quality, or {@code null} if the pool is empty.
     */
    private static @Nullable Part getRandomDrop(List<Part> dropPool, int negotiatorSkill) {
        if (dropPool.isEmpty()) {
            return null;
        }

        Part randomPart = ObjectUtility.getRandomItem(dropPool);
        randomPart.setQuality(getRandomPartQuality(negotiatorSkill));

        return randomPart;
    }

    /**
     * Determines a random part quality based on the input modifier.
     * This method uses unit-level quality logic as a placeholder, with planned
     * future integration of fame and infamy to influence quality.
     *
     * @param modifier The value influencing the randomness of part quality.
     * @return A randomly generated {@link PartQuality} based on the modifier.
     */
    static PartQuality getRandomPartQuality(int modifier) {
        // TODO: have fame & infamy influence this value, once that module has been implemented
        return getRandomUnitQuality(modifier);
    }

    /**
     * Calculates the worth of the convoy contents based on the resupply type and various campaign
     * conditions.
     *
     * @param resupply The {@link Resupply} object containing details about the convoy
     *                 and associated resupply operation.
     */
    private static void calculateConvoyWorth(Resupply resupply) {
        List<Part> convoyContents = resupply.getConvoyContents();
        Money sellValue = Money.zero();
        Money buyValue = Money.zero();
        for (Part part : convoyContents) {
            sellValue = sellValue.plus(part.getActualValue());
            buyValue = buyValue.plus(part.getStickerPrice());
        }
        resupply.setConvoyContentsValueBase(buyValue);

        ResupplyType resupplyType = resupply.getResupplyType();
        if (resupplyType.equals(ResupplyType.RESUPPLY_LOOT)) {
            // Calculated value initializes as zero, and looted supplies have no associated cost.
            return;
        }

        // Smugglers always double the cost of the supplies they're offering
        if (resupplyType.equals(ResupplyType.RESUPPLY_SMUGGLER)) {
            resupply.setConvoyContentsValueCalculated(buyValue.multipliedBy(2));
            return ;
        }

        // If the player faction matches the employer faction (and is not Mercenary, or Pirate),
        // then supplies are free.
        final Campaign campaign = resupply.getCampaign();
        final Faction campaignFaction = campaign.getFaction();

        final AtBContract contract = resupply.getContract();
        final Faction employerFaction = contract.getEmployerFaction();

        if (campaignFaction.equals(employerFaction) && !campaignFaction.isMercenary()
            && !campaignFaction.isPirate()) {
            // convoy contents initializes with a calculated value of zero, so no need to set it here.
            return;
        }

        // In all other cases, the value of the supplies is based on enemy morale. The logic is
        // that the direr the situation, the harder supplies are to come by, so the less willing
        // the employer is to part with them at a discount.
        AtBMoraleLevel moraleLevel = contract.getMoraleLevel();

        double multiplier = switch (moraleLevel) {
            case ROUTED -> 0.25;
            case CRITICAL -> 0.5;
            case WEAKENED -> 0.75;
            case STALEMATE -> 1;
            case ADVANCING -> 1.25;
            case DOMINATING -> 1.5;
            case OVERWHELMING -> 1.75;
        };

        resupply.setConvoyContentsValueCalculated(sellValue.multipliedBy(multiplier));
    }
}
