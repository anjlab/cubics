package anjlab.cubics.test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import org.junit.Assert;
import org.junit.Test;

import anjlab.cubics.Cube;
import anjlab.cubics.FactModel;

public class TestSerialize {

	@Test
	public void testSerializeCube() throws IOException {
		FactModel<Fact> model = TestHelper.createFactModel();
		Iterable<Fact> facts = TestHelper.createTestFacts();
		
		Cube<Fact> c = Cube.createCube(model, facts);
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		ObjectOutputStream os = new ObjectOutputStream(baos);
		
		os.writeObject(c);
		
		os.close();
		
		Assert.assertEquals(4838, baos.size());
	}
	
}
