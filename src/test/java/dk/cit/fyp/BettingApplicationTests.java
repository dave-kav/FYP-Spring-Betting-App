package dk.cit.fyp;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import dk.cit.fyp.bean.UserBetBeanTest;
import dk.cit.fyp.config.UserBetBeanConfigTest;
import dk.cit.fyp.config.WebConfigTest;
import dk.cit.fyp.service.BetServiceImplTest;
import dk.cit.fyp.service.ImageServiceImplTest;
import dk.cit.fyp.service.RaceServiceImplTest;

@RunWith(Suite.class)

@Suite.SuiteClasses({
   UserBetBeanTest.class,
   UserBetBeanConfigTest.class,
   WebConfigTest.class,
   BetServiceImplTest.class,
   ImageServiceImplTest.class,
   RaceServiceImplTest.class
})

public class BettingApplicationTests {}
