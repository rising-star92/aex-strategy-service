package com.walmart.aex.strategy.util;

import com.walmart.aex.strategy.dto.request.Lvl3;
import com.walmart.aex.strategy.dto.request.SizeProfileDTO;
import com.walmart.aex.strategy.dto.request.Strategy;
import com.walmart.aex.strategy.dto.sizepack.SPMerchMethFineline;
import com.walmart.aex.strategy.dto.sizepack.SPMerchMethFixture;
import com.walmart.aex.strategy.dto.sizepack.SPMerchMethLvl3;
import com.walmart.aex.strategy.dto.sizepack.SPMerchMethLvl4;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
public class GetSPMerchMethodFixtureUtil {
    public static SPMerchMethLvl3 getLvl3List() {
        SPMerchMethLvl3 lvl3 = new SPMerchMethLvl3();
        lvl3.setLvl3Nbr(1056310);
        lvl3.setLvl3Desc("Bottoms Ecomm Womens");
        lvl3.setFixtureTypes(getFixtures());
        lvl3.setLvl4List(getLvl4s());
        return  lvl3;
    }

    public static List<SPMerchMethLvl4> getLvl4s() {
        List<SPMerchMethLvl4> lvls = new ArrayList<>();
        SPMerchMethLvl4 lvl4 = new SPMerchMethLvl4();
        lvl4.setLvl4Nbr(1056371);
        lvl4.setLvl4Desc("Capris Bottoms Ec Womens");
        lvl4.setFixtureTypes(getFixtures());
        lvl4.setFinelines(getFinelines());
        lvls.add(lvl4);
        return lvls;
    }

    public static List<SPMerchMethFineline> getFinelines() {
        SPMerchMethFineline fineline = new SPMerchMethFineline();
        List<SPMerchMethFineline> finelines = new ArrayList<>();
        fineline.setFinelineNbr(787);
        fineline.setFinelineDesc("787 - TT CONSTRUCTED CAPRI");
        fineline.setFixtureTypes(getFixtures());
        finelines.add(fineline);
        return  finelines;
    }

    public static List<SPMerchMethFixture> getFixtures() {
        List<SPMerchMethFixture> fixtures = new ArrayList<>();
        SPMerchMethFixture fixture1 = new SPMerchMethFixture();
        fixture1.setMerchMethodCode(1);
        fixture1.setFixtureTypeRollupId(1);
        fixtures.add(fixture1);
        SPMerchMethFixture fixture2 = new SPMerchMethFixture();
        fixture2.setMerchMethodCode(1);
        fixture2.setFixtureTypeRollupId(2);
        fixtures.add(fixture2);
        return fixtures;
    }

}
