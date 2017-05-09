package dk.cit.fyp.runner;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import dk.cit.fyp.BettingApplicationTests;

public class TestRunner {
	public static void main(String[] args) {
		Result result = JUnitCore.runClasses(BettingApplicationTests.class);

	      for (Failure failure : result.getFailures()) {
	         System.out.println(failure.toString());
	      }
			
	      System.out.println("All tests successful: " + result.wasSuccessful());
	}
}
