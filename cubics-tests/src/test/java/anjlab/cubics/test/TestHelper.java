package anjlab.cubics.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import anjlab.cubics.FactModel;

public class TestHelper {

	@Test
	public void dummy() {
		//	junit needs this
	}
	
	public static FactModel<Fact> createFactModel() {
		FactModel<Fact> model = new FactModel<Fact>(Fact.class);
		model.setDimensions("year", "month", "day", "hour");
		model.setMeasures("duration", "succeeded");
		return model;
	}

	public static List<Fact> createTestFacts() {
		List<Fact> result = new ArrayList<Fact>();
		
		result.add(new Fact(2009, 01, 01, 00, 1000, 1));
		result.add(new Fact(2009, 01, 01, 01, 2000, 0));
		result.add(new Fact(2009, 02, 01, 00, 1000, 1));
		result.add(new Fact(2009, 02, 02, 00, 1000, 1));
		result.add(new Fact(2010, 01, 01, 00, 1000, 1));
		result.add(new Fact(2010, 01, 01, 00, 3000, 1));
		
		return result;
	}

	public static FactModel<Fact> createFactModel2() {
		FactModel<Fact> model = new FactModel<Fact>(Fact.class);
		model.setDimensions("month");
		model.setMeasures("duration", "succeeded");
		return model;
	}

}
