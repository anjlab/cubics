package anjlab.cubics.test;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Map;

import org.junit.Test;

import anjlab.cubics.Cube;
import anjlab.cubics.FactModel;
import anjlab.cubics.Hierarchy;
import anjlab.cubics.Key;
import anjlab.cubics.coerce.DateTimeCoercer;
import anjlab.cubics.coerce.DoubleCoercer;
import anjlab.cubics.coerce.IntegerCoercer;
import anjlab.cubics.csv.CsvDefinition;
import anjlab.cubics.csv.CsvEntry;
import anjlab.cubics.csv.CsvValueProvider;

public class TestCsvValueProvider {

    @Test
    public void calculateBasicAggregates() {
        CsvDefinition definition = new CsvDefinition();
        
        definition.addColumn("order_date", new DateTimeCoercer("yyyy-MM-dd HH:mm:ss"));
        definition.addColumn("items_count", new IntegerCoercer());
        definition.addColumn("total_price", new DoubleCoercer());
        
        FactModel<CsvEntry> model = new FactModel<CsvEntry>(new CsvValueProvider(definition));
        model.setDimensions("order_date");
        model.setMeasures("items_count", "total_price");
        
        Cube<CsvEntry> c = Cube.createCube(model);
        
        c.addFact(new CsvEntry("\"2009-12-23 09:16:11\",4,300.00"));
        c.addFact(new CsvEntry("\"2009-12-23 17:09:32\",2,300.00"));
        c.addFact(new CsvEntry("\"2009-12-23 20:08:20\",2,410.00"));
        c.addFact(new CsvEntry("\"2009-12-23 20:10:37\",6,885.00"));
        
        Hierarchy<CsvEntry> root = c.getRoot();

        assertEquals(14, root.getTotals().getAggregate("items_count").getSum(), 0);
        assertEquals(885, root.getTotals().getAggregate("total_price").getMax(), 0);

        Map<Key, Hierarchy<CsvEntry>> children = root.getChildren();
        assertEquals(4, children.size());
        
        Calendar cal = Calendar.getInstance();
        cal.set(2009, Calendar.DECEMBER, 23, 9, 16, 11);
        
        Key key = new Key(cal.getTime());
        
        assertTrue(children.containsKey(key));
    }

    @Test
    public void useFormattedDimensions() {
        CsvDefinition definition = new CsvDefinition();
        
        definition.addColumn("order_date", new DateTimeCoercer("yyyy-MM-dd HH:mm:ss"));
        definition.addColumn("items_count", new IntegerCoercer());
        definition.addColumn("total_price", new DoubleCoercer());
        
        FactModel<CsvEntry> model = new FactModel<CsvEntry>(new CsvValueProvider(definition));
        model.setDimensions("order_date-y", // Calendar.YEAR, 
                            "order_date-M", // Calendar.MONTH,
                            "order_date-d", // Calendar.DATE,
                            "order_date-H", // Calendar.HOUR_OF_DAY,
                            "order_date-m", // Calendar.MINUTE
                            "order_date-s"  // Calendar.SECOND
                            );
        model.setMeasures("items_count", "total_price");
        
        Cube<CsvEntry> c = Cube.createCube(model);
        
        c.addFact(new CsvEntry("\"2009-12-23 09:16:11\",4,300.00"));
        c.addFact(new CsvEntry("\"2009-12-23 17:09:32\",2,300.00"));
        c.addFact(new CsvEntry("\"2009-12-23 20:08:20\",2,410.00"));
        c.addFact(new CsvEntry("\"2009-12-23 20:10:37\",6,885.00"));
        
        Hierarchy<CsvEntry> root = c.getRoot();

        assertEquals(14, root.getTotals().getAggregate("items_count").getSum(), 0);
        assertEquals(885, root.getTotals().getAggregate("total_price").getMax(), 0);

        Map<Key, Hierarchy<CsvEntry>> children = root.getChildren();
        assertEquals(1, children.size());
        
        Hierarchy<CsvEntry> year2009 = children.get(new Key(2009));
        assertNotNull(year2009);
        Hierarchy<CsvEntry> december2009 = year2009.getChildren().get(new Key(Calendar.DECEMBER, 2009));
        assertNotNull(december2009);
        Hierarchy<CsvEntry> dateInDecember = december2009.getChildren().get(new Key(23, 2009, Calendar.DECEMBER));
        assertNotNull(dateInDecember);
        Hierarchy<CsvEntry> hour9 = dateInDecember.getChildren().get(new Key(9, 2009, Calendar.DECEMBER, 23));
        assertNotNull(hour9);
        Hierarchy<CsvEntry> hour20 = dateInDecember.getChildren().get(new Key(20, 2009, Calendar.DECEMBER, 23));
        assertNotNull(hour20);
        
        assertEquals(8, hour20.getTotals().getAggregate("items_count").getSum(), 0);
    }
}
