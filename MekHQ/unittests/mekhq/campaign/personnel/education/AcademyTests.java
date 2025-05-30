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
 *
 * MechWarrior Copyright Microsoft Corporation. MekHQ was created under
 * Microsoft's "Game Content Usage Rules"
 * <https://www.xbox.com/en-US/developers/rules> and it is not endorsed by or
 * affiliated with Microsoft.
 */
package mekhq.campaign.personnel.education;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import mekhq.campaign.Campaign;
import mekhq.campaign.personnel.Person;
import mekhq.campaign.personnel.enums.education.AcademyType;
import mekhq.campaign.personnel.enums.education.EducationLevel;
import mekhq.campaign.personnel.skills.SkillType;
import mekhq.campaign.universe.Faction;
import mekhq.campaign.universe.PlanetarySystem;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class AcademyTests {
    @Test
    void testSetName() {
        Academy academy = new Academy();
        academy.setName("Military Academy");
        assertEquals("Military Academy", academy.getName());
    }

    @Test
    void testSetTuition() {
        Academy academy = new Academy();
        academy.setTuition(5000);
        assertEquals(5000, academy.getTuition());
    }

    @Test
    void testIsMilitary() {
        Academy academy = new Academy();
        academy.setIsMilitary(true);
        assertTrue(academy.isMilitary());
    }

    @Test
    void testAcademyCreationAllFields() {
        Academy academy = new Academy("MekWarrior",
              "MekWarrior Academy",
              "College",
              true,
              false,
              true,
              "Top level MekWarrior Training",
              20,
              true,
              Arrays.asList("Sol", "Terra"),
              false,
              false,
              3045,
              3089,
              3099,
              2000,
              365,
              10,
              EducationLevel.EARLY_CHILDHOOD,
              EducationLevel.DOCTORATE,
              18,
              35,
              Arrays.asList("MekWarrior", "Leadership"),
              Arrays.asList("Combat", "Strategy"),
              Arrays.asList(3050, 3055),
              5,
              101);

        assertEquals("MekWarrior Academy", academy.getName());
        assertEquals(AcademyType.COLLEGE, academy.getType());
        assertTrue(academy.isMilitary());
        assertEquals(20, academy.getFactionDiscount());
        assertEquals(2000, academy.getTuition());
        assertEquals(Integer.valueOf(3089), academy.getDestructionYear());
    }

    @Test
    void testCompareToSameID() {
        Academy academy1 = new Academy();
        Academy academy2 = new Academy();
        academy1.setId(100);
        academy2.setId(100);
        assertEquals(0, academy1.compareTo(academy2));
    }

    @Test
    void testCompareToDifferentID() {
        Academy academy1 = new Academy();
        Academy academy2 = new Academy();
        academy1.setId(100);
        academy2.setId(200);
        assertTrue(academy1.compareTo(academy2) < 0);
    }

    @Test
    void testGetTuitionAdjustedLowEducationLevel() {
        Academy academy = new Academy();
        academy.setTuition(1000);
        academy.setEducationLevelMin(EducationLevel.EARLY_CHILDHOOD);
        academy.setEducationLevelMax(EducationLevel.HIGH_SCHOOL);
        Person person = Mockito.mock(Person.class);
        when(person.getEduHighestEducation()).thenReturn(EducationLevel.HIGH_SCHOOL);
        assertEquals(1000, academy.getTuitionAdjusted(person));
    }

    @Test
    void testGetTuitionAdjustedHighEducationLevel() {
        Academy academy = new Academy();
        academy.setTuition(1000);
        academy.setEducationLevelMin(EducationLevel.HIGH_SCHOOL);
        academy.setEducationLevelMax(EducationLevel.POST_GRADUATE);
        Person person = Mockito.mock(Person.class);
        when(person.getEduHighestEducation()).thenReturn(EducationLevel.COLLEGE);
        assertEquals(3000, academy.getTuitionAdjusted(person));
    }

    @Test
    void testIsQualifiedTrue() {
        Academy academy = new Academy();
        academy.setEducationLevelMin(EducationLevel.COLLEGE);
        Person person = Mockito.mock(Person.class);
        when(person.getEduHighestEducation()).thenReturn(EducationLevel.POST_GRADUATE);
        assertTrue(academy.isQualified(person));
    }

    @Test
    void testIsQualifiedFalse() {
        Academy academy = new Academy();
        academy.setEducationLevelMin(EducationLevel.COLLEGE);
        Person person = Mockito.mock(Person.class);
        when(person.getEduHighestEducation()).thenReturn(EducationLevel.EARLY_CHILDHOOD);
        assertFalse(academy.isQualified(person));
    }

    @Test
    void testGetFactionDiscountAdjustedNotPresentInLocationSystems() {
        Academy academy = new Academy();
        academy.setLocationSystems(List.of("Sol"));
        academy.setFactionDiscount(10);
        Person person = Mockito.mock(Person.class);
        Campaign campaign = Mockito.mock(Campaign.class);
        PlanetarySystem system = Mockito.mock(PlanetarySystem.class);
        when(campaign.getSystemById("Sol")).thenReturn(system);
        when(system.getFactions(Mockito.any())).thenReturn(List.of("Lyr"));
        when(person.getOriginFaction()).thenReturn(new Faction("FWL", ""));
        when(campaign.getFaction()).thenReturn(new Faction("FWL", ""));
        assertEquals(1.0, academy.getFactionDiscountAdjusted(campaign, person));
    }

    @Test
    public void testSkillParser_ValidSkill() {
        assertEquals(SkillType.S_PILOT_MEK, Academy.skillParser("piloting/mek"));
        assertEquals(SkillType.S_GUN_MEK, Academy.skillParser("gunnery/mek"));
        assertEquals(SkillType.S_GUN_MEK, Academy.skillParser("gunnery/mek"));
        assertEquals(SkillType.S_PILOT_AERO, Academy.skillParser("piloting/aerospace"));
        assertEquals(SkillType.S_GUN_AERO, Academy.skillParser("gunnery/aerospace"));
        assertEquals(SkillType.S_PILOT_GVEE, Academy.skillParser("piloting/ground vehicle"));
        assertEquals(SkillType.S_PILOT_VTOL, Academy.skillParser("piloting/vtol"));
        assertEquals(SkillType.S_PILOT_NVEE, Academy.skillParser("piloting/naval"));
        assertEquals(SkillType.S_GUN_VEE, Academy.skillParser("gunnery/vehicle"));
        assertEquals(SkillType.S_PILOT_JET, Academy.skillParser("piloting/aircraft"));
        assertEquals(SkillType.S_GUN_JET, Academy.skillParser("gunnery/aircraft"));
        assertEquals(SkillType.S_PILOT_SPACE, Academy.skillParser("piloting/spacecraft"));
        assertEquals(SkillType.S_GUN_SPACE, Academy.skillParser("gunnery/spacecraft"));
        assertEquals(SkillType.S_ARTILLERY, Academy.skillParser("artillery"));
        assertEquals(SkillType.S_GUN_BA, Academy.skillParser("gunnery/battlearmor"));
        assertEquals(SkillType.S_GUN_PROTO, Academy.skillParser("gunnery/protomek"));
        assertEquals(SkillType.S_SMALL_ARMS, Academy.skillParser("small arms"));
        assertEquals(SkillType.S_ANTI_MEK, Academy.skillParser("anti-mek"));
        assertEquals(SkillType.S_TECH_MEK, Academy.skillParser("tech/mek"));
        assertEquals(SkillType.S_TECH_MECHANIC, Academy.skillParser("tech/mechanic"));
        assertEquals(SkillType.S_TECH_AERO, Academy.skillParser("tech/aero"));
        assertEquals(SkillType.S_TECH_BA, Academy.skillParser("tech/battlearmor"));
        assertEquals(SkillType.S_TECH_VESSEL, Academy.skillParser("tech/vessel"));
        assertEquals(SkillType.S_ASTECH, Academy.skillParser("astech"));
        assertEquals(SkillType.S_SURGERY, Academy.skillParser("doctor"));
        assertEquals(SkillType.S_MEDTECH, Academy.skillParser("medtech"));
        assertEquals(SkillType.S_NAVIGATION, Academy.skillParser("hyperspace navigation"));
        assertEquals(SkillType.S_ADMIN, Academy.skillParser("administration"));
        assertEquals(SkillType.S_TACTICS, Academy.skillParser("tactics"));
        assertEquals(SkillType.S_STRATEGY, Academy.skillParser("strategy"));
        assertEquals(SkillType.S_NEGOTIATION, Academy.skillParser("negotiation"));
        assertEquals(SkillType.S_LEADER, Academy.skillParser("leadership"));
    }

    @Test
    public void testSkillParser_InvalidSkill() {
        assertThrows(IllegalStateException.class, () -> Academy.skillParser("invalid_skill"));
    }
}
