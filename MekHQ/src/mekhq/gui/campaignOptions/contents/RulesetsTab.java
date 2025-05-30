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
package mekhq.gui.campaignOptions.contents;

import static mekhq.gui.campaignOptions.CampaignOptionsUtilities.createParentPanel;
import static mekhq.gui.campaignOptions.CampaignOptionsUtilities.createTipPanelUpdater;
import static mekhq.gui.campaignOptions.CampaignOptionsUtilities.getImageDirectory;

import java.awt.GridBagConstraints;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import megamek.client.ui.models.FileNameComboBoxModel;
import megamek.client.ui.comboBoxes.MMComboBox;
import megamek.client.ui.clientGUI.GUIPreferences;
import megamek.common.annotations.Nullable;
import megamek.common.enums.SkillLevel;
import mekhq.campaign.CampaignOptions;
import mekhq.campaign.autoresolve.AutoResolveMethod;
import mekhq.campaign.mission.AtBContract;
import mekhq.campaign.mission.enums.CombatRole;
import mekhq.campaign.personnel.skills.Skills;
import mekhq.gui.campaignOptions.components.CampaignOptionsButton;
import mekhq.gui.campaignOptions.components.CampaignOptionsCheckBox;
import mekhq.gui.campaignOptions.components.CampaignOptionsGridBagConstraints;
import mekhq.gui.campaignOptions.components.CampaignOptionsHeaderPanel;
import mekhq.gui.campaignOptions.components.CampaignOptionsLabel;
import mekhq.gui.campaignOptions.components.CampaignOptionsSpinner;
import mekhq.gui.campaignOptions.components.CampaignOptionsStandardPanel;

/**
 * Represents a tab in the campaign options UI for managing ruleset configurations in campaigns.
 * <p>
 * This class organizes and manages options related to universal rules, legacy AtB rules (Against the Bot),
 * and StratCon (Strategic Context) settings. It provides a UI to customize configurations such as
 * opponent force generation, scenario rules, equipment behavior, and campaign-specific variations.
 * </p>
 *
 * <strong>Tab Sections:</strong>
 * <ul>
 *     <li><b>Universal Options:</b> Handles features applicable to all campaigns,
 *         such as skill levels, unit ratios, map conditions, and auto-resolve settings.</li>
 *     <li><b>Legacy AtB:</b> Legacy-specific rules for opponent force generation,
 *         scenario generation probabilities, and battle intensity configurations.</li>
 *     <li><b>StratCon:</b> Settings for Strategic Context campaigns, including BV usage
 *         (Battle Values) and verbose bidding options.</li>
 * </ul>
 */
public class RulesetsTab {
    private final CampaignOptions campaignOptions;

    //start Universal Options
    private JLabel lblSkillLevel;
    private MMComboBox<SkillLevel> comboSkillLevel;
    private JPanel pnlScenarioGenerationPanel;
    private JPanel pnlCampaignOptions;

    private JPanel pnlUnitRatioPanel;
    private JLabel lblOpForLanceTypeMeks;
    private JSpinner spnOpForLanceTypeMeks;
    private JLabel lblOpForLanceTypeMixed;
    private JSpinner spnOpForLanceTypeMixed;
    private JLabel lblOpForLanceTypeVehicle;
    private JSpinner spnOpForLanceTypeVehicles;

    private JCheckBox chkUseDropShips;
    private JCheckBox chkOpForUsesVTOLs;

    private JCheckBox chkClanVehicles;
    private JCheckBox chkRegionalMekVariations;

    private JCheckBox chkAttachedPlayerCamouflage;
    private JCheckBox chkPlayerControlsAttachedUnits;
    private JLabel lblSPAUpgradeIntensity;
    private JSpinner spnSPAUpgradeIntensity;
    private JCheckBox chkAutoConfigMunitions;

    private JPanel pnlScenarioModifiers;
    private JLabel lblScenarioModMax;
    private JSpinner spnScenarioModMax;
    private JLabel lblScenarioModChance;
    private JSpinner spnScenarioModChance;
    private JLabel lblScenarioModBV;
    private JSpinner spnScenarioModBV;

    private JPanel pnlMapGenerationPanel;
    private JCheckBox chkUseWeatherConditions;
    private JCheckBox chkUseLightConditions;
    private JCheckBox chkUsePlanetaryConditions;
    private JLabel lblFixedMapChance;
    private JSpinner spnFixedMapChance;

    private JPanel pnlPartsPanel;
    private JCheckBox chkRestrictPartsByMission;

    private JPanel pnlAutoResolve;
    private JLabel lblAutoResolveMethod;
    private MMComboBox<AutoResolveMethod> comboAutoResolveMethod;
    private MMComboBox<String> minimapThemeSelector;
    private JCheckBox chkAutoResolveVictoryChanceEnabled;
    private JLabel lblMinimapTheme;
    private JCheckBox chkAutoResolveExperimentalPacarGuiEnabled;
    private JLabel lblAutoResolveNumberOfScenarios;
    private JSpinner spnAutoResolveNumberOfScenarios;
    //end Universal Options

    //start Legacy AtB
    private CampaignOptionsHeaderPanel legacyHeader;
    private JCheckBox chkUseAtB;

    private JPanel pnlLegacyOpForGenerationPanel;
    private JCheckBox chkUseVehicles;
    private JCheckBox chkDoubleVehicles;
    private JCheckBox chkOpForUsesAero;
    private JLabel lblOpForAeroChance;
    private JSpinner spnOpForAeroChance;
    private JCheckBox chkOpForUsesLocalForces;
    private JCheckBox chkAdjustPlayerVehicles;

    private JPanel pnlLegacyScenarioGenerationPanel;
    private JLabel lblIntensity;
    private JSpinner spnAtBBattleIntensity;
    private JLabel lblFightChance;
    private JLabel lblDefendChance;
    private JLabel lblScoutChance;
    private JLabel lblTrainingChance;
    private JSpinner[] spnAtBBattleChance;
    private JButton btnIntensityUpdate;
    private JCheckBox chkGenerateChases;
    //end Legacy AtB

    //start StratCon
    private CampaignOptionsHeaderPanel stratConHeader;
    private JCheckBox chkUseStratCon;
    private JCheckBox chkUseGenericBattleValue;
    private JCheckBox chkUseVerboseBidding;
    //end StratCon

    /**
     * Constructs a {@code RulesetsTab} instance for managing ruleset options.
     *
     * @param campaignOptions the {@link CampaignOptions} object to manage repair, maintenance, and other ruleset options.
     */
    public RulesetsTab(CampaignOptions campaignOptions) {
        this.campaignOptions = campaignOptions;

        initialize();
    }

    /**
     * Initializes the tab by setting up all three sections:
     * <p>
     *     <li>Universal Options</li>
     *     <li>StratCon Tab</li>
     *     <li>Legacy Tab</li>
     * </p>
     */
    private void initialize() {
        initializeUniversalOptions();
        initializeStratConTab();
        initializeLegacyTab();
    }

    /**
     * Initializes the universal options section of the tab.
     * <p>
     * Universal options include settings like skill levels, scenario modifiers,
     * map generation parameters, and auto-resolve behavior.
     * </p>
     */
    private void initializeUniversalOptions() {
        // General
        lblSkillLevel = new JLabel();
        comboSkillLevel = new MMComboBox<>("comboSkillLevel", getSkillLevelOptions());
        pnlScenarioGenerationPanel = new JPanel();

        // OpFor Generation
        pnlUnitRatioPanel = new JPanel();
        lblOpForLanceTypeMeks = new JLabel();
        spnOpForLanceTypeMeks = new JSpinner();
        lblOpForLanceTypeMixed = new JLabel();
        spnOpForLanceTypeMixed = new JSpinner();
        lblOpForLanceTypeVehicle = new JLabel();
        spnOpForLanceTypeVehicles = new JSpinner();

        chkUseDropShips = new JCheckBox();
        chkOpForUsesVTOLs = new JCheckBox();
        chkClanVehicles = new JCheckBox();
        chkRegionalMekVariations = new JCheckBox();

        chkAttachedPlayerCamouflage = new JCheckBox();
        chkPlayerControlsAttachedUnits = new JCheckBox();

        lblSPAUpgradeIntensity = new JLabel();
        spnSPAUpgradeIntensity = new JSpinner();
        chkAutoConfigMunitions = new JCheckBox();

        pnlScenarioModifiers = new JPanel();
        lblScenarioModMax = new JLabel();
        spnScenarioModMax = new JSpinner();
        lblScenarioModChance = new JLabel();
        spnScenarioModChance = new JSpinner();
        lblScenarioModBV = new JLabel();
        spnScenarioModBV = new JSpinner();

        // Map Generation
        pnlMapGenerationPanel = new JPanel();
        chkUseWeatherConditions = new JCheckBox();
        chkUseLightConditions = new JCheckBox();
        chkUsePlanetaryConditions = new JCheckBox();
        lblFixedMapChance = new JLabel();
        spnFixedMapChance = new JSpinner();

        // Parts
        pnlPartsPanel = new JPanel();
        chkRestrictPartsByMission = new JCheckBox();

        // Auto Resolve
        pnlAutoResolve = new JPanel();
        lblAutoResolveMethod = new JLabel();
        final DefaultComboBoxModel<AutoResolveMethod> autoResolveTypeModel = new DefaultComboBoxModel<>(
                AutoResolveMethod.values());
        comboAutoResolveMethod = new MMComboBox<>("comboAutoResolveMethod", autoResolveTypeModel);
        minimapThemeSelector = new MMComboBox<>("minimapThemeSelector",
            new FileNameComboBoxModel(GUIPreferences.getInstance().getMinimapThemes()));
        chkAutoResolveVictoryChanceEnabled = new JCheckBox();
        lblAutoResolveNumberOfScenarios = new JLabel();
        spnAutoResolveNumberOfScenarios = new JSpinner();
        lblMinimapTheme = new JLabel();
        chkAutoResolveExperimentalPacarGuiEnabled = new JCheckBox();
        // Here we set up the options, so they can be used across both the AtB and StratCon tabs
        substantializeUniversalOptions();
    }

    /**
     * Configures and initializes universal options components for use across tabs.
     * <p>
     * This method sets up and organizes the various UI elements for universal options,
     * such as skill levels, scenario generation, map generation, and more. These initialized
     * components are then used in other methods to build the complete universal options UI.
     * </p>
     */
    private void substantializeUniversalOptions() {
        // General
        lblSkillLevel = new CampaignOptionsLabel("SkillLevel");

        // OpFor Generation
        pnlUnitRatioPanel = createUniversalUnitRatioPanel();

        chkUseDropShips = new CampaignOptionsCheckBox("UseDropShips");
        chkOpForUsesVTOLs = new CampaignOptionsCheckBox("OpForUsesVTOLs");
        chkClanVehicles = new CampaignOptionsCheckBox("ClanVehicles");
        chkRegionalMekVariations = new CampaignOptionsCheckBox("RegionalMekVariations");

        chkAttachedPlayerCamouflage = new CampaignOptionsCheckBox("AttachedPlayerCamouflage");
        chkPlayerControlsAttachedUnits = new CampaignOptionsCheckBox("PlayerControlsAttachedUnits");
        lblSPAUpgradeIntensity = new CampaignOptionsLabel("SPAUpgradeIntensity");
        spnSPAUpgradeIntensity = new CampaignOptionsSpinner("SPAUpgradeIntensity",
            0, -1, 3, 1);
        chkAutoConfigMunitions = new CampaignOptionsCheckBox("AutoConfigMunitions");

        // Other
        pnlScenarioModifiers = createUniversalModifiersPanel();
        pnlMapGenerationPanel = createUniversalMapGenerationPanel();
        pnlPartsPanel = createUniversalPartsPanel();

        pnlScenarioGenerationPanel = createUniversalScenarioGenerationPanel();
        pnlCampaignOptions = createUniversalCampaignOptionsPanel();
        pnlAutoResolve = createAutoResolvePanel();
    }

    /**
     * Retrieves the available skill levels as a {@link DefaultComboBoxModel}.
     * <p>
     * Returns the predefined {@link SkillLevel} values, excluding {@link SkillLevel#NONE}.
     * Used for populating the skill level selector in the universal options UI.
     * </p>
     *
     * @return a {@link DefaultComboBoxModel} containing available {@link SkillLevel} options
     */
    private static DefaultComboBoxModel<SkillLevel> getSkillLevelOptions() {
        final DefaultComboBoxModel<SkillLevel> skillLevelModel = new DefaultComboBoxModel<>(
            Skills.SKILL_LEVELS);

        skillLevelModel.removeElement(SkillLevel.NONE);

        return skillLevelModel;
    }

    /**
     * Creates the UI panel for configuring universal scenario generation options.
     * <p>
     * Allows users to define settings for opponent force configurations, such as
     * enabling dropships, VTOLs, and clan vehicles, as well as other universal scenario parameters.
     * </p>
     *
     * @return a {@link JPanel} containing controls to configure universal scenario generation
     */
    private JPanel createUniversalScenarioGenerationPanel() {
        // Layout the panel
        final JPanel panel = new CampaignOptionsStandardPanel("UniversalScenarioGenerationPanel", true,
            "UniversalScenarioGenerationPanel");
        final GridBagConstraints layout = new CampaignOptionsGridBagConstraints(panel);

        layout.gridx = 0;
        layout.gridy = 0;
        layout.gridwidth = 3;
        panel.add(pnlUnitRatioPanel, layout);

        layout.gridy++;
        layout.gridwidth = 2;
        panel.add(chkUseDropShips, layout);

        layout.gridy++;
        panel.add(chkOpForUsesVTOLs, layout);

        layout.gridy++;
        panel.add(chkClanVehicles, layout);

        layout.gridy++;
        panel.add(chkRegionalMekVariations, layout);

        layout.gridy++;
        panel.add(chkAttachedPlayerCamouflage, layout);

        layout.gridy++;
        panel.add(chkPlayerControlsAttachedUnits, layout);

        layout.gridy++;
        panel.add(chkAutoConfigMunitions, layout);

        layout.gridy++;
        layout.gridwidth = 1;
        panel.add(lblSPAUpgradeIntensity, layout);
        layout.gridx++;
        panel.add(spnSPAUpgradeIntensity, layout);

        layout.gridx = 0;
        layout.gridy++;
        layout.gridwidth = 3;
        panel.add(pnlScenarioModifiers, layout);

        return panel;
    }

    /**
     * Creates the UI panel for configuring the auto-resolve options in campaigns.
     * <p>
     * Includes controls to set the auto-resolve method, enable victory chance calculation,
     * and specify the number of scenarios to consider during auto-resolution.
     * </p>
     *
     * @return a {@link JPanel} containing controls to configure auto-resolve behavior
     */
    private JPanel createAutoResolvePanel() {
        // Content
        lblAutoResolveMethod = new CampaignOptionsLabel("AutoResolveMethod");
        lblAutoResolveNumberOfScenarios = new CampaignOptionsLabel("AutoResolveNumberOfScenarios");
        spnAutoResolveNumberOfScenarios = new CampaignOptionsSpinner("AutoResolveNumberOfScenarios",
                250, 10, 1000, 10);
        chkAutoResolveVictoryChanceEnabled = new CampaignOptionsCheckBox("AutoResolveVictoryChanceEnabled");
        lblMinimapTheme = new CampaignOptionsLabel("MinimapTheme");
        chkAutoResolveExperimentalPacarGuiEnabled = new CampaignOptionsCheckBox("AutoResolveExperimentalPacarGuiEnabled");

        // Layout the panel
        final JPanel panel = new CampaignOptionsStandardPanel("AutoResolvePanel", true,
            "AutoResolvePanel");
        final GridBagConstraints layout = new CampaignOptionsGridBagConstraints(panel);

        layout.gridwidth = 1;
        layout.gridx = 0;
        layout.gridy = 0;
        panel.add(lblAutoResolveMethod, layout);
        layout.gridy++;
        panel.add(comboAutoResolveMethod, layout);
        layout.gridy++;
        panel.add(chkAutoResolveVictoryChanceEnabled, layout);
        layout.gridy++;
        panel.add(chkAutoResolveExperimentalPacarGuiEnabled, layout);
        layout.gridy++;
        panel.add(lblMinimapTheme, layout);
        layout.gridy++;
        panel.add(minimapThemeSelector, layout);
        layout.gridy++;
        panel.add(lblAutoResolveNumberOfScenarios, layout);
        layout.gridy++;
        panel.add(spnAutoResolveNumberOfScenarios, layout);

        return panel;
    }

    /**
     * Creates the UI panel for configuring unit ratios in universal options.
     * <p>
     * Includes spinners for setting the ratio of various unit types, such as
     * mechs, mixed units, and vehicles, for the opponent forces.
     * </p>
     *
     * @return a {@link JPanel} containing controls for unit ratio configuration
     */
    private JPanel createUniversalUnitRatioPanel() {
        // Content
        lblOpForLanceTypeMeks = new CampaignOptionsLabel("OpForLanceTypeMeks");
        spnOpForLanceTypeMeks = new CampaignOptionsSpinner("OpForLanceTypeMeks",
            0, 0, 10, 1);
        lblOpForLanceTypeMixed = new CampaignOptionsLabel("OpForLanceTypeMixed");
        spnOpForLanceTypeMixed = new CampaignOptionsSpinner("OpForLanceTypeMixed",
            0, 0, 10, 1);
        lblOpForLanceTypeVehicle = new CampaignOptionsLabel("OpForLanceTypeVehicle");
        spnOpForLanceTypeVehicles = new CampaignOptionsSpinner("OpForLanceTypeVehicle",
            0, 0, 10, 1);

        // Layout the panel
        final JPanel panel = new CampaignOptionsStandardPanel("UniversalUnitRatioPanel", true,
            "UniversalUnitRatioPanel");
        final GridBagConstraints layout = new CampaignOptionsGridBagConstraints(panel);

        layout.gridx = 0;
        layout.gridy = 0;
        layout.gridwidth = 1;
        panel.add(lblOpForLanceTypeMeks, layout);
        layout.gridx++;
        panel.add(spnOpForLanceTypeMeks, layout);
        layout.gridx++;
        panel.add(lblOpForLanceTypeMixed, layout);
        layout.gridx++;
        panel.add(spnOpForLanceTypeMixed, layout);
        layout.gridx++;
        panel.add(lblOpForLanceTypeVehicle, layout);
        layout.gridx++;
        panel.add(spnOpForLanceTypeVehicles, layout);

        return panel;
    }

    /**
     * Creates the UI panel for configuring universal scenario modifiers.
     * <p>
     * This panel includes controls to adjust the maximum modifiers for scenario generation,
     * modifier chance percentages, and BV (Battle Value) impact. It is designed to provide
     * flexible settings for campaign customization.
     * </p>
     *
     * @return a {@link JPanel} containing controls to configure universal scenario modifiers
     */
    private JPanel createUniversalModifiersPanel() {
        //Content
        lblScenarioModMax = new CampaignOptionsLabel("ScenarioModMax");
        spnScenarioModMax = new CampaignOptionsSpinner("ScenarioModMax",
            3, 0, 10, 1);
        lblScenarioModChance = new CampaignOptionsLabel("ScenarioModChance");
        spnScenarioModChance = new CampaignOptionsSpinner("ScenarioModChance",
            25, 5, 100, 5);
        lblScenarioModBV = new CampaignOptionsLabel("ScenarioModBV");
        spnScenarioModBV = new CampaignOptionsSpinner("ScenarioModBV",
            50, 5, 100, 5);

        // Layout the panel
        final JPanel panel = new CampaignOptionsStandardPanel("UniversalModifiersPanel", true,
            "UniversalModifiersPanel");
        final GridBagConstraints layout = new CampaignOptionsGridBagConstraints(panel);

        layout.gridx = 0;
        layout.gridy = 0;
        layout.gridwidth = 1;
        panel.add(lblScenarioModMax, layout);
        layout.gridx++;
        panel.add(spnScenarioModMax, layout);

        layout.gridx = 0;
        layout.gridy++;
        panel.add(lblScenarioModChance, layout);
        layout.gridx++;
        panel.add(spnScenarioModChance, layout);

        layout.gridx = 0;
        layout.gridy++;
        panel.add(lblScenarioModBV, layout);
        layout.gridx++;
        panel.add(spnScenarioModBV, layout);

        return panel;
    }

    /**
     * Creates the UI panel for configuring universal map generation settings.
     * <p>
     * Includes options for enabling weather, light, planetary conditions, and fixed map chances,
     * with spinners and checkboxes for user input.
     * </p>
     *
     * @return a {@link JPanel} containing controls to configure map generation options
     */
    private JPanel createUniversalMapGenerationPanel() {
        // Content
        chkUseWeatherConditions = new CampaignOptionsCheckBox("UseWeatherConditions");
        chkUseLightConditions = new CampaignOptionsCheckBox("UseLightConditions");
        chkUsePlanetaryConditions = new CampaignOptionsCheckBox("UsePlanetaryConditions");
        lblFixedMapChance = new CampaignOptionsLabel("FixedMapChance");
        spnFixedMapChance = new CampaignOptionsSpinner("FixedMapChance",
            0, 0, 100, 1);

        // Layout the panel
        final JPanel panel = new CampaignOptionsStandardPanel("UniversalMapGenerationPanel", true,
            "UniversalMapGenerationPanel");
        final GridBagConstraints layout = new CampaignOptionsGridBagConstraints(panel);

        layout.gridx = 0;
        layout.gridy = 0;
        layout.gridwidth = 2;
        panel.add(chkUseWeatherConditions, layout);

        layout.gridy++;
        panel.add(chkUseLightConditions, layout);

        layout.gridy++;
        panel.add(chkUsePlanetaryConditions, layout);

        layout.gridy++;
        layout.gridwidth = 1;
        panel.add(lblFixedMapChance, layout);
        layout.gridx++;
        panel.add(spnFixedMapChance, layout);

        return panel;
    }

    /**
     * Creates the UI panel that consolidates universal campaign options.
     * <p>
     * This panel combines sub-panels like the parts panel, lance panel, and map generation panel
     * into a single cohesive UI for configuring general campaign options.
     * </p>
     *
     * @return a {@link JPanel} containing all universal campaign options organized in sections
     */
    private JPanel createUniversalCampaignOptionsPanel() {
        // Layout the panel
        final JPanel panel = new CampaignOptionsStandardPanel("UniversalCampaignOptionsPanel");
        GridBagConstraints layout = new CampaignOptionsGridBagConstraints(panel);

        layout.gridwidth = 2;
        layout.gridy = 0;
        layout.gridx = 0;
        panel.add(pnlPartsPanel, layout);
        layout.gridy++;
        panel.add(pnlMapGenerationPanel, layout);

        return panel;
    }

    /**
     * Creates the UI panel for configuring universal parts restrictions during campaigns.
     * <p>
     * Includes settings such as restricting parts availability based on mission requirements.
     * </p>
     *
     * @return a {@link JPanel} containing controls to configure parts-related options for campaigns
     */
    private JPanel createUniversalPartsPanel() {
        // Content
        chkRestrictPartsByMission = new CampaignOptionsCheckBox("RestrictPartsByMission");

        // Layout the panel
        final JPanel panel = new CampaignOptionsStandardPanel("UniversalPartsPanel", true,
            "UniversalPartsPanel");
        final GridBagConstraints layout = new CampaignOptionsGridBagConstraints(panel);

        layout.gridx = 0;
        layout.gridy = 0;
        layout.gridwidth = 2;
        panel.add(chkRestrictPartsByMission, layout);

        return panel;
    }

    /**
     * Initializes the StratCon (Strategic Context) section of the tab.
     */
    private void initializeStratConTab() {
        chkUseStratCon = new JCheckBox();
        chkUseGenericBattleValue = new JCheckBox();
        chkUseVerboseBidding = new JCheckBox();
    }

    /**
     * Creates the UI panel for the StratCon configuration.
     * <p>
     * This section includes settings for using generic battle values,
     * enabling verbose bidding, and other Strategic Conquest-specific rules.
     * </p>
     *
     * @return a {@link JPanel} containing all StratCon settings.
     */
    public JPanel createStratConTab() {
        // Header
        stratConHeader = new CampaignOptionsHeaderPanel("StratConTab",
              getImageDirectory() + "logo_clan_wolf.png",
              false,
              true,
              6);

        // Universal Content

        // Right now the universal content all lives in the StratCon tab, but that might not always be the case if
        // we ever introduce a new Digital GM. So, as this content is initialized before the stratConHeader, we need
        // to wait until now to add the mouse listeners. It's awkward, but it works.
        lblAutoResolveMethod.addMouseListener(createTipPanelUpdater(stratConHeader, "AutoResolveMethod"));
        comboAutoResolveMethod.addMouseListener(createTipPanelUpdater(stratConHeader, "AutoResolveMethod"));
        lblMinimapTheme.addMouseListener(createTipPanelUpdater(stratConHeader, "MinimapTheme"));
        minimapThemeSelector.addMouseListener(createTipPanelUpdater(stratConHeader, "MinimapTheme"));
        lblAutoResolveNumberOfScenarios.addMouseListener(createTipPanelUpdater(stratConHeader,
              "AutoResolveNumberOfScenarios"));
        spnAutoResolveNumberOfScenarios.addMouseListener(createTipPanelUpdater(stratConHeader,
              "AutoResolveNumberOfScenarios"));
        chkAutoResolveVictoryChanceEnabled.addMouseListener(createTipPanelUpdater(stratConHeader,
              "AutoResolveVictoryChanceEnabled"));
        chkAutoResolveExperimentalPacarGuiEnabled.addMouseListener(createTipPanelUpdater(stratConHeader,
              "AutoResolveExperimentalPacarGuiEnabled"));
        chkRestrictPartsByMission.addMouseListener(createTipPanelUpdater(stratConHeader, "RestrictPartsByMission"));
        chkUseWeatherConditions.addMouseListener(createTipPanelUpdater(stratConHeader, "UseWeatherConditions"));
        chkUseLightConditions.addMouseListener(createTipPanelUpdater(stratConHeader, "UseLightConditions"));
        chkUsePlanetaryConditions.addMouseListener(createTipPanelUpdater(stratConHeader, "UsePlanetaryConditions"));
        lblFixedMapChance.addMouseListener(createTipPanelUpdater(stratConHeader, "FixedMapChance"));
        spnFixedMapChance.addMouseListener(createTipPanelUpdater(stratConHeader, "FixedMapChance"));
        lblScenarioModMax.addMouseListener(createTipPanelUpdater(stratConHeader, "ScenarioModMax"));
        spnScenarioModMax.addMouseListener(createTipPanelUpdater(stratConHeader, "ScenarioModMax"));
        lblScenarioModChance.addMouseListener(createTipPanelUpdater(stratConHeader, "ScenarioModChance"));
        spnScenarioModChance.addMouseListener(createTipPanelUpdater(stratConHeader, "ScenarioModChance"));
        lblScenarioModBV.addMouseListener(createTipPanelUpdater(stratConHeader, "ScenarioModBV"));
        spnScenarioModBV.addMouseListener(createTipPanelUpdater(stratConHeader, "ScenarioModBV"));
        lblSkillLevel.addMouseListener(createTipPanelUpdater(stratConHeader, "SkillLevel"));
        comboSkillLevel.addMouseListener(createTipPanelUpdater(stratConHeader, "SkillLevel"));
        chkUseDropShips.addMouseListener(createTipPanelUpdater(stratConHeader, "UseDropShips"));
        chkOpForUsesVTOLs.addMouseListener(createTipPanelUpdater(stratConHeader, "OpForUsesVTOLs"));
        chkClanVehicles.addMouseListener(createTipPanelUpdater(stratConHeader, "ClanVehicles"));
        chkRegionalMekVariations.addMouseListener(createTipPanelUpdater(stratConHeader, "RegionalMekVariations"));
        chkAttachedPlayerCamouflage.addMouseListener(createTipPanelUpdater(stratConHeader, "AttachedPlayerCamouflage"));
        chkPlayerControlsAttachedUnits.addMouseListener(createTipPanelUpdater(stratConHeader,
              "PlayerControlsAttachedUnits"));
        lblSPAUpgradeIntensity.addMouseListener(createTipPanelUpdater(stratConHeader, "SPAUpgradeIntensity"));
        spnSPAUpgradeIntensity.addMouseListener(createTipPanelUpdater(stratConHeader, "SPAUpgradeIntensity"));
        chkAutoConfigMunitions.addMouseListener(createTipPanelUpdater(stratConHeader, "AutoConfigMunitions"));

        // Content
        chkUseStratCon = new CampaignOptionsCheckBox("UseStratCon");
        chkUseStratCon.addMouseListener(createTipPanelUpdater(stratConHeader, "UseStratCon"));
        chkUseGenericBattleValue = new CampaignOptionsCheckBox("UseGenericBattleValue");
        chkUseGenericBattleValue.addMouseListener(createTipPanelUpdater(stratConHeader, "UseGenericBattleValue"));
        chkUseVerboseBidding = new CampaignOptionsCheckBox("UseVerboseBidding");
        chkUseVerboseBidding.addMouseListener(createTipPanelUpdater(stratConHeader, "UseVerboseBidding"));

        // Layout the Panel
        final JPanel panel = new CampaignOptionsStandardPanel("StratConTab", true);
        GridBagConstraints layout = new CampaignOptionsGridBagConstraints(panel);

        layout.gridwidth = 5;
        layout.gridy = 0;
        panel.add(stratConHeader, layout);

        layout.gridwidth = 1;
        layout.gridy++;
        panel.add(chkUseStratCon, layout);

        layout.gridx = 0;
        layout.gridy++;
        panel.add(lblSkillLevel, layout);
        layout.gridx++;
        panel.add(comboSkillLevel, layout);
        layout.gridx++;
        panel.add(chkUseGenericBattleValue, layout);
        layout.gridx++;
        panel.add(chkUseVerboseBidding, layout);

        layout.gridwidth = 2;
        layout.gridx = 0;
        layout.gridy++;
        panel.add(pnlScenarioGenerationPanel, layout);

        layout.gridwidth = 2;
        layout.gridx = 2;
        panel.add(pnlCampaignOptions, layout);

        layout.gridwidth = 1;
        layout.gridx = 4;
        panel.add(pnlAutoResolve, layout);

        // Create panel and return
        return createParentPanel(panel, "StratConTab");
    }

    /**
     * Initializes the Legacy AtB (Against the Bot) section of the tab.
     */
    private void initializeLegacyTab() {
        // General
        chkUseAtB = new JCheckBox();

        // OpFor Generation
        pnlLegacyOpForGenerationPanel = new JPanel();
        chkUseVehicles = new JCheckBox();
        chkDoubleVehicles = new JCheckBox();
        chkOpForUsesAero = new JCheckBox();
        lblOpForAeroChance = new JLabel();
        spnOpForAeroChance = new JSpinner();
        chkOpForUsesLocalForces = new JCheckBox();
        chkAdjustPlayerVehicles = new JCheckBox();

        // Scenarios
        pnlLegacyScenarioGenerationPanel = new JPanel();
        chkGenerateChases = new JCheckBox();
        lblIntensity = new JLabel();
        spnAtBBattleIntensity = new JSpinner();
        lblFightChance = new JLabel();
        lblDefendChance = new JLabel();
        lblScoutChance = new JLabel();
        lblTrainingChance = new JLabel();
        spnAtBBattleChance = new JSpinner[CombatRole.values().length - 1];
        btnIntensityUpdate = new JButton();
    }

    /**
     * Creates the UI panel for the Legacy AtB configuration.
     * <p>
     * This section configures opponent force generation, scenario generation probabilities,
     * and customization of battle intensities for "Against the Bot" campaigns.
     * </p>
     *
     * @return a {@link JPanel} containing all Legacy AtB settings.
     */
    public JPanel createLegacyTab() {
        // Header
        legacyHeader = new CampaignOptionsHeaderPanel("LegacyTab",
              getImageDirectory() + "logo_free_rasalhague_republic.png",
              true, true, 5);

        chkUseAtB = new CampaignOptionsCheckBox("UseAtB");
        chkUseAtB.addMouseListener(createTipPanelUpdater(legacyHeader, "UseAtB"));
        pnlLegacyOpForGenerationPanel = createLegacyOpForGenerationPanel();
        pnlLegacyScenarioGenerationPanel = createLegacyScenarioGenerationPanel();

        // Layout the Panel
        final JPanel panel = new CampaignOptionsStandardPanel("LegacyTab", true);
        final GridBagConstraints layout = new CampaignOptionsGridBagConstraints(panel);

        layout.gridwidth = 5;
        layout.gridx = 0;
        layout.gridy = 0;
        panel.add(legacyHeader, layout);

        layout.gridy++;
        layout.gridwidth = 1;
        panel.add(chkUseAtB, layout);

        layout.gridy++;
        panel.add(pnlLegacyOpForGenerationPanel, layout);
        layout.gridx++;
        panel.add(pnlLegacyScenarioGenerationPanel, layout);

        // Create panel and return
        return createParentPanel(panel, "LegacyTab");
    }

    /**
     * Creates the UI panel for configuring the Legacy AtB opponent force (OpFor) generation settings.
     * <p>
     * Options include enabling vehicle support, aero unit chances, local forces, and player
     * vehicle adjustments. The panel provides various checkboxes and spinners for user interaction.
     * </p>
     *
     * @return a {@link JPanel} containing controls to configure AtB opponent force generation options
     */
    private JPanel createLegacyOpForGenerationPanel() {
        // Content
        chkUseVehicles = new CampaignOptionsCheckBox("UseVehicles");
        chkUseVehicles.addMouseListener(createTipPanelUpdater(legacyHeader, "UseVehicles"));
        chkDoubleVehicles = new CampaignOptionsCheckBox("DoubleVehicles");
        chkDoubleVehicles.addMouseListener(createTipPanelUpdater(legacyHeader, "DoubleVehicles"));
        chkOpForUsesAero = new CampaignOptionsCheckBox("OpForUsesAero");
        chkOpForUsesAero.addMouseListener(createTipPanelUpdater(legacyHeader, "OpForUsesAero"));
        lblOpForAeroChance = new CampaignOptionsLabel("OpForAeroChance");
        lblOpForAeroChance.addMouseListener(createTipPanelUpdater(legacyHeader, "OpForAeroChance"));
        spnOpForAeroChance = new CampaignOptionsSpinner("OpForAeroChance",
            0, 0, 6, 1);
        spnOpForAeroChance.addMouseListener(createTipPanelUpdater(legacyHeader, "OpForAeroChance"));
        chkOpForUsesLocalForces = new CampaignOptionsCheckBox("OpForUsesLocalForces");
        chkOpForUsesLocalForces.addMouseListener(createTipPanelUpdater(legacyHeader, "OpForUsesLocalForces"));
        chkAdjustPlayerVehicles = new CampaignOptionsCheckBox("AdjustPlayerVehicles");
        chkAdjustPlayerVehicles.addMouseListener(createTipPanelUpdater(legacyHeader, "AdjustPlayerVehicles"));

        // Layout the Panel
        final JPanel panel = new CampaignOptionsStandardPanel("LegacyOpForGenerationPanel", true,
            "LegacyOpForGenerationPanel");
        final GridBagConstraints layout = new CampaignOptionsGridBagConstraints(panel);

        layout.gridx = 0;
        layout.gridy = 0;
        layout.gridwidth = 2;
        panel.add(chkUseVehicles, layout);

        layout.gridy++;
        panel.add(chkDoubleVehicles, layout);

        layout.gridy++;
        panel.add(chkOpForUsesAero, layout);

        layout.gridx = 0;
        layout.gridy++;
        layout.gridwidth = 1;
        panel.add(lblOpForAeroChance, layout);
        layout.gridx++;
        panel.add(spnOpForAeroChance, layout);

        layout.gridx = 0;
        layout.gridy++;
        layout.gridwidth = 2;
        panel.add(chkOpForUsesLocalForces, layout);

        layout.gridy++;
        panel.add(chkAdjustPlayerVehicles, layout);

        return panel;
    }

    /**
     * Creates the UI panel for configuring the Legacy AtB (Against the Bot) scenario generation settings.
     * <p>
     * This panel includes settings for enabling chase generation, adjusting battle role chances
     * (Fight, Defend, Scout, Training), and updating these values based on a calculated intensity.
     * </p>
     *
     * @return a {@link JPanel} containing controls to configure AtB scenario generation options
     */
    private JPanel createLegacyScenarioGenerationPanel() {
        // Content
        chkGenerateChases = new CampaignOptionsCheckBox("GenerateChases");
        chkGenerateChases.addMouseListener(createTipPanelUpdater(legacyHeader, "GenerateChases"));
        lblIntensity = new CampaignOptionsLabel("AtBBattleIntensity");
        lblIntensity.addMouseListener(createTipPanelUpdater(legacyHeader, "AtBBattleIntensity"));
        spnAtBBattleIntensity = new CampaignOptionsSpinner("AtBBattleIntensity",
            0.0, 0.0, 100.0, 0.1);
        spnAtBBattleIntensity.addMouseListener(createTipPanelUpdater(legacyHeader, "AtBBattleIntensity"));

        lblFightChance = new JLabel(CombatRole.MANEUVER.toString());
        lblDefendChance = new JLabel(CombatRole.FRONTLINE.toString());
        lblScoutChance = new JLabel(CombatRole.PATROL.toString());
        lblTrainingChance = new JLabel(CombatRole.TRAINING.toString());
        spnAtBBattleChance = new JSpinner[CombatRole.values().length - 1];

        for (int i = 0; i < spnAtBBattleChance.length; i++) {
            spnAtBBattleChance[i] = new JSpinner(
                new SpinnerNumberModel(0, 0, 100, 1));
        }

        btnIntensityUpdate = new CampaignOptionsButton("IntensityUpdate");
        btnIntensityUpdate.addMouseListener(createTipPanelUpdater(legacyHeader, "IntensityUpdate"));
        AtBBattleIntensityChangeListener atBBattleIntensityChangeListener = new AtBBattleIntensityChangeListener();
        btnIntensityUpdate.addChangeListener(evt -> {
            spnAtBBattleIntensity.removeChangeListener(atBBattleIntensityChangeListener);
            spnAtBBattleIntensity.setValue(determineAtBBattleIntensity());
            spnAtBBattleIntensity.addChangeListener(atBBattleIntensityChangeListener);
        });

        // Layout the Panel
        final JPanel panelBattleChance = new CampaignOptionsStandardPanel("LegacyScenarioGenerationPanel");
        final GridBagConstraints layoutBattleChance = new CampaignOptionsGridBagConstraints(panelBattleChance);

        layoutBattleChance.gridx = 0;
        layoutBattleChance.gridy = 0;
        layoutBattleChance.gridwidth = 1;
        panelBattleChance.add(lblFightChance, layoutBattleChance);
        layoutBattleChance.gridx++;
        panelBattleChance.add(spnAtBBattleChance[CombatRole.MANEUVER.ordinal()], layoutBattleChance);

        layoutBattleChance.gridx = 0;
        layoutBattleChance.gridy++;
        panelBattleChance.add(lblDefendChance, layoutBattleChance);
        layoutBattleChance.gridx++;
        panelBattleChance.add(spnAtBBattleChance[CombatRole.FRONTLINE.ordinal()], layoutBattleChance);

        layoutBattleChance.gridx = 0;
        layoutBattleChance.gridy++;
        panelBattleChance.add(lblScoutChance, layoutBattleChance);
        layoutBattleChance.gridx++;
        panelBattleChance.add(spnAtBBattleChance[CombatRole.PATROL.ordinal()], layoutBattleChance);

        layoutBattleChance.gridx = 0;
        layoutBattleChance.gridy++;
        panelBattleChance.add(lblTrainingChance, layoutBattleChance);
        layoutBattleChance.gridx++;
        panelBattleChance.add(spnAtBBattleChance[CombatRole.TRAINING.ordinal()], layoutBattleChance);

        final JPanel panel = new CampaignOptionsStandardPanel("LegacyScenarioGenerationPanel", true,
            "LegacyScenarioGenerationPanel");
        final GridBagConstraints layout = new CampaignOptionsGridBagConstraints(panel);

        layout.gridx = 0;
        layout.gridy = 0;
        layout.gridwidth = 2;
        panel.add(chkGenerateChases, layout);

        layout.gridy++;
        layout.gridwidth = 1;
        panel.add(lblIntensity, layout);
        layout.gridx++;
        panel.add(spnAtBBattleIntensity, layout);

        layout.gridx = 0;
        layout.gridy++;
        layout.gridwidth = 1;
        panel.add(panelBattleChance, layout);

        layout.gridy++;
        layout.gridwidth = 1;
        panel.add(btnIntensityUpdate, layout);

        return panel;
    }

    /**
     * Determines the AtB (Against the Bot) battle intensity value based on the current
     * settings of battle role chance spinners.
     * <p>
     * Each role (e.g., Maneuver, Frontline, Patrol, Training) contributes to the overall
     * battle intensity value based on complex formulas. The result is normalized, capped
     * at 100.0, and rounded to a single decimal place.
     * </p>
     *
     * @return the calculated battle intensity value as a {@code double}
     */
    private double determineAtBBattleIntensity() {
        double intensity = 0.0;

        int x = (int) spnAtBBattleChance[CombatRole.MANEUVER.ordinal()].getValue();
        intensity += ((-3.0 / 2.0) * (2.0 * x - 1.0)) / (2.0 * x - 201.0);

        x = (int) spnAtBBattleChance[CombatRole.FRONTLINE.ordinal()].getValue();
        intensity += ((-4.0) * (2.0 * x - 1.0)) / (2.0 * x - 201.0);

        x = (int) spnAtBBattleChance[CombatRole.PATROL.ordinal()].getValue();
        intensity += ((-2.0 / 3.0) * (2.0 * x - 1.0)) / (2.0 * x - 201.0);

        x = (int) spnAtBBattleChance[CombatRole.TRAINING.ordinal()].getValue();
        intensity += ((-9.0) * (2.0 * x - 1.0)) / (2.0 * x - 201.0);

        intensity = intensity / 4.0;

        if (intensity > 100.0) {
            intensity = 100.0;
        }

        return Math.round(intensity * 10.0) / 10.0;
    }

    /**
     * A listener to manage changes in the AtB (Against the Bot) battle intensity spinner value.
     * <p>
     * It listens for changes in the battle intensity spinner, recalculates the values for different
     * battle roles (e.g., Maneuver, Frontline, Patrol, Training), and updates the corresponding spinners
     * for the player to see the effects of the intensity change.
     * </p>
     */
    private class AtBBattleIntensityChangeListener implements ChangeListener {
        /**
         * Called when the state of the AtB battle intensity spinner changes.
         * <p>
         * Updates the battle role chance spinners based on the current value of the battle intensity
         * spinner. If the intensity is below the minimum defined in {@link AtBContract#MINIMUM_INTENSITY},
         * all role chance spinners are set to zero.
         * </p>
         *
         * @param e the {@link ChangeEvent} triggered when the spinner value changes
         */
        @Override
        public void stateChanged(ChangeEvent e) {
            double intensity = (double) spnAtBBattleIntensity.getValue();

            if (intensity >= AtBContract.MINIMUM_INTENSITY) {
                int value = (int) Math.min(
                    Math.round(400.0 * intensity / (4.0 * intensity + 6.0) + 0.05), 100);
                spnAtBBattleChance[CombatRole.MANEUVER.ordinal()].setValue(value);
                value = (int) Math.min(Math.round(200.0 * intensity / (2.0 * intensity + 8.0) + 0.05),
                    100);
                spnAtBBattleChance[CombatRole.FRONTLINE.ordinal()].setValue(value);
                value = (int) Math.min(Math.round(600.0 * intensity / (6.0 * intensity + 4.0) + 0.05),
                    100);
                spnAtBBattleChance[CombatRole.PATROL.ordinal()].setValue(value);
                value = (int) Math.min(Math.round(100.0 * intensity / (intensity + 9.0) + 0.05), 100);
                spnAtBBattleChance[CombatRole.TRAINING.ordinal()].setValue(value);
            } else {
                spnAtBBattleChance[CombatRole.MANEUVER.ordinal()].setValue(0);
                spnAtBBattleChance[CombatRole.FRONTLINE.ordinal()].setValue(0);
                spnAtBBattleChance[CombatRole.PATROL.ordinal()].setValue(0);
                spnAtBBattleChance[CombatRole.TRAINING.ordinal()].setValue(0);
            }
        }
    }

    /**
     * Applies the current values configured in the tab back to the provided {@link CampaignOptions}.
     * <p>
     * If no custom {@link CampaignOptions} is provided, it uses the default {@link CampaignOptions}
     * associated with the tab.
     * </p>
     *
     * @param presetCampaignOptions an optional custom {@link CampaignOptions} object to apply the values to;
     *                              if {@code null}, the default options are used.
     */
    public void applyCampaignOptionsToCampaign(@Nullable CampaignOptions presetCampaignOptions) {
        CampaignOptions options = presetCampaignOptions;
        if (presetCampaignOptions == null) {
            options = this.campaignOptions;
        }

        // Universal
        options.setSkillLevel(comboSkillLevel.getSelectedItem());
        options.setOpForLanceTypeMeks((int) spnOpForLanceTypeMeks.getValue());
        options.setOpForLanceTypeMixed((int) spnOpForLanceTypeMixed.getValue());
        options.setOpForLanceTypeVehicles((int) spnOpForLanceTypeVehicles.getValue());
        options.setUseDropShips(chkUseDropShips.isSelected());
        options.setOpForUsesVTOLs(chkOpForUsesVTOLs.isSelected());
        options.setClanVehicles(chkClanVehicles.isSelected());
        options.setRegionalMekVariations(chkRegionalMekVariations.isSelected());
        options.setAttachedPlayerCamouflage(chkAttachedPlayerCamouflage.isSelected());
        options.setPlayerControlsAttachedUnits(chkPlayerControlsAttachedUnits.isSelected());
        options.setSpaUpgradeIntensity((int) spnSPAUpgradeIntensity.getValue());
        options.setAutoConfigMunitions(chkAutoConfigMunitions.isSelected());
        options.setScenarioModMax((int) spnScenarioModMax.getValue());
        options.setScenarioModChance((int) spnScenarioModChance.getValue());
        options.setScenarioModBV((int) spnScenarioModBV.getValue());
        options.setUseWeatherConditions(chkUseWeatherConditions.isSelected());
        options.setUseLightConditions(chkUseLightConditions.isSelected());
        options.setUsePlanetaryConditions(chkUsePlanetaryConditions.isSelected());
        options.setFixedMapChance((int) spnFixedMapChance.getValue());
        options.setRestrictPartsByMission(chkRestrictPartsByMission.isSelected());
        options.setAutoResolveMethod(comboAutoResolveMethod.getSelectedItem());
        options.setStrategicViewTheme(minimapThemeSelector.getSelectedItem());
        options.setAutoResolveVictoryChanceEnabled(chkAutoResolveVictoryChanceEnabled.isSelected());
        options.setAutoResolveNumberOfScenarios((int) spnAutoResolveNumberOfScenarios.getValue());
        options.setAutoResolveExperimentalPacarGuiEnabled(chkAutoResolveExperimentalPacarGuiEnabled.isSelected());

        // StratCon
        options.setUseStratCon(chkUseStratCon.isSelected());
        options.setUseGenericBattleValue(chkUseGenericBattleValue.isSelected());
        options.setUseVerboseBidding(chkUseVerboseBidding.isSelected());

        // Legacy
        options.setUseAtB(chkUseAtB.isSelected() && !chkUseStratCon.isSelected());
        options.setUseVehicles(chkUseVehicles.isSelected());
        options.setDoubleVehicles(chkDoubleVehicles.isSelected());
        options.setUseAero(chkOpForUsesAero.isSelected());
        options.setOpForAeroChance((int) spnOpForAeroChance.getValue());
        options.setAllowOpForLocalUnits(chkOpForUsesLocalForces.isSelected());
        options.setAdjustPlayerVehicles(chkAdjustPlayerVehicles.isSelected());
        options.setGenerateChases(chkGenerateChases.isSelected());

        for (int i = 0; i < spnAtBBattleChance.length; i++) {
            options.setAtBBattleChance(i, (int) spnAtBBattleChance[i].getValue());
        }
    }

    /**
     * A convenience method to load values from the default {@link CampaignOptions} instance.
     */
    public void loadValuesFromCampaignOptions() {
        loadValuesFromCampaignOptions(null);
    }

    /**
     * Loads the ruleset values from a {@link CampaignOptions} object into the UI components.
     * <p>
     * If no custom {@link CampaignOptions} is provided, it will fetch values from the default
     * {@link CampaignOptions} instance.
     * </p>
     *
     * @param presetCampaignOptions an optional custom {@link CampaignOptions} object to load values from;
     *                              if {@code null}, the default options are used.
     */
    public void loadValuesFromCampaignOptions(@Nullable CampaignOptions presetCampaignOptions) {
        CampaignOptions options = presetCampaignOptions;
        if (presetCampaignOptions == null) {
            options = this.campaignOptions;
        }

        // Universal
        comboSkillLevel.setSelectedItem(options.getSkillLevel());
        spnOpForLanceTypeMeks.setValue(options.getOpForLanceTypeMeks());
        spnOpForLanceTypeMixed.setValue(options.getOpForLanceTypeMixed());
        spnOpForLanceTypeVehicles.setValue(options.getOpForLanceTypeVehicles());
        chkUseDropShips.setSelected(options.isUseDropShips());
        chkOpForUsesVTOLs.setSelected(options.isOpForUsesVTOLs());
        chkClanVehicles.setSelected(options.isClanVehicles());
        chkRegionalMekVariations.setSelected(options.isRegionalMekVariations());
        chkAttachedPlayerCamouflage.setSelected(options.isAttachedPlayerCamouflage());
        chkPlayerControlsAttachedUnits.setSelected(options.isPlayerControlsAttachedUnits());
        spnSPAUpgradeIntensity.setValue(options.getSpaUpgradeIntensity());
        chkAutoConfigMunitions.setSelected(options.isAutoConfigMunitions());
        spnScenarioModMax.setValue(options.getScenarioModMax());
        spnScenarioModChance.setValue(options.getScenarioModChance());
        spnScenarioModBV.setValue(options.getScenarioModBV());
        chkUseWeatherConditions.setSelected(options.isUseWeatherConditions());
        chkUseLightConditions.setSelected(options.isUseLightConditions());
        chkUsePlanetaryConditions.setSelected(options.isUsePlanetaryConditions());
        spnFixedMapChance.setValue(options.getFixedMapChance());
        chkRestrictPartsByMission.setSelected(options.isRestrictPartsByMission());
        comboAutoResolveMethod.setSelectedItem(options.getAutoResolveMethod());
        minimapThemeSelector.setSelectedItem(options.getStrategicViewTheme().getName());
        chkAutoResolveVictoryChanceEnabled.setSelected(options.isAutoResolveVictoryChanceEnabled());
        chkAutoResolveExperimentalPacarGuiEnabled.setSelected(options.isAutoResolveExperimentalPacarGuiEnabled());
        spnAutoResolveNumberOfScenarios.setValue(options.getAutoResolveNumberOfScenarios());

        // StratCon
        chkUseStratCon.setSelected(options.isUseStratCon());
        chkUseGenericBattleValue.setSelected(options.isUseGenericBattleValue());
        chkUseVerboseBidding.setSelected(options.isUseVerboseBidding());

        // Legacy
        chkUseAtB.setSelected(options.isUseAtB() && !options.isUseStratCon());
        chkUseVehicles.setSelected(options.isUseVehicles());
        chkDoubleVehicles.setSelected(options.isDoubleVehicles());
        chkOpForUsesAero.setSelected(options.isUseAero());
        spnOpForAeroChance.setValue(options.getOpForAeroChance());
        chkOpForUsesLocalForces.setSelected(options.isAllowOpForLocalUnits());
        chkAdjustPlayerVehicles.setSelected(options.isAdjustPlayerVehicles());
        chkGenerateChases.setSelected(options.isGenerateChases());
        for (CombatRole role : CombatRole.values()) {
            if (role.ordinal() <= CombatRole.TRAINING.ordinal()) {
                spnAtBBattleChance[role.ordinal()].setValue(options.getAtBBattleChance(role));
            }
        }
    }
}
