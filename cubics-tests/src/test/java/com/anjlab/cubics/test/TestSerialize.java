package com.anjlab.cubics.test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import org.junit.Assert;
import org.junit.Test;

import com.anjlab.cubics.Cube;
import com.anjlab.cubics.FactModel;


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
		
		Assert.assertEquals(5065, baos.size());
	}
	
}
