package org.iplantc.de.server;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestNameListLoader {
    // private static SessionFactory sf = null;
    // private static Session session = null;
    // private static NameListCsvParser csvParser;
    // private static File csvFile;
    // private static NameListTransformer optimus;
    // private static org.iplantc.treedata.model.File truck;
    // private static List<String[]> data;
    // private static PersistTreeData ptd;

    @BeforeClass
    public static void beforeClass() throws Exception {
        // System.out.println("in setUp");
        //
        // csvParser = new NameListCsvParser();
        // csvFile = new File(new TestNameListLoader().getClass().getResource("/Workbook1.csv").toURI());
        // optimus = new NameListTransformer();
        // ptd = new PersistTreeData();
        //
        // data = csvParser.parse(csvFile);
        // truck = optimus.transform(data, "Workbook1.csv");
        //
        // sf = HibernateUtil.getSessionFactory("hibernate-test.cfg.xml");
        // ptd.setSessionFactory(sf);
        // session = sf.openSession();
        // ptd.transform(truck);
    }

    @AfterClass
    public static void afterClass() throws Exception {
        // System.out.println("In tearDown");
        // session.close();
    }

    @Test
    public void testPersistRecordTableWithoutErrors() throws Throwable {
        // try
        // {
        // // ptd.transform(truck);
        // Query q = session.createQuery("from RecordTable");
        // List qlist = q.list();
        //
        // System.out.println("Length of list retreived: " + qlist.size());
        //
        // for(int i = 0;i < qlist.size();i++)
        // {
        // RecordTable thisRT = (RecordTable)qlist.get(i);
        // assertEquals(thisRT.getRecords().size(), 101);
        // }
        // }
        // catch(Throwable t)
        // {
        // t.printStackTrace();
        // throw t;
        // }
    }

    @Test
    public void testPersistRecordTableWithoutErrors2() throws Throwable {
        // try
        // {
        // // ptd.transform(truck);
        // Query q = session.createQuery("from RecordTable");
        // List qlist = q.list();
        //
        // System.out.println("Length of list retreived: " + qlist.size());
        //
        // for(int i = 0;i < qlist.size();i++)
        // {
        // RecordTable thisRT = (RecordTable)qlist.get(i);
        // assertEquals(thisRT.getRecords().size(), 101);
        // }
        // }
        // catch(Throwable t)
        // {
        // t.printStackTrace();
        // throw t;
        // }
    }

    @Test
    public void testNumberColumns() {
        // try
        // {
        // // ptd.transform(truck);
        // Query q = session.createQuery("from RecordTable");
        // List qlist = q.list();
        //
        // for(int i = 0;i < qlist.size();i++)
        // {
        // RecordTable thisRT = (RecordTable)qlist.get(i);
        // assertEquals(thisRT.getFields().size(), 2);
        // }
        // }
        // catch(HibernateException e)
        // {
        // e.printStackTrace();
        // throw e;
        // }
    }

    @Test
    public void testSearchForFirstRow() {
        // try
        // {
        // // ptd.transform(truck);
        // Query q = session.createQuery("from RecordTable");
        // List qlist = q.list();
        //
        // for(int i = 0;i < qlist.size();i++)
        // {
        // RecordTable thisRT = (RecordTable)qlist.get(i);
        //
        // Boolean foundColumn1Value = false;
        // Boolean foundColumn2Value = false;
        //
        // for(RecordTableRecord rtr : thisRT.getRecords())
        // {
        // for(RecordTableValue rtv : rtr.getValues())
        // {
        // if(rtv.getValue().equals("Family_submitted"))
        // {
        // foundColumn1Value = true;
        // }
        // else
        // {
        // if(rtv.getValue().equals("ScientificName_submitted"))
        // {
        // foundColumn2Value = true;
        // }
        // }
        //
        // }
        // }
        //
        // assertTrue(foundColumn1Value);
        // assertTrue(foundColumn2Value);
        // }
        // }
        // catch(HibernateException e)
        // {
        // e.printStackTrace();
        // throw e;
        // }
    }

    @Test
    public void testSearchForLastRow() {
        // try
        // {
        // // ptd.transform(truck);
        // Query q = session.createQuery("from RecordTable");
        // List qlist = q.list();
        //
        // for(int i = 0;i < qlist.size();i++)
        // {
        // RecordTable thisRT = (RecordTable)qlist.get(i);
        //
        // Boolean foundColumn1Value = false;
        // Boolean foundColumn2Value = false;
        //
        // for(RecordTableRecord rtr : thisRT.getRecords())
        // {
        // for(RecordTableValue rtv : rtr.getValues())
        // {
        // if(rtv.getValue().equals("ANACARDIACEAE"))
        // {
        // foundColumn1Value = true;
        // }
        // else
        // {
        // if(rtv.getValue().equals("Comocladia glabra"))
        // {
        // foundColumn2Value = true;
        // }
        // }
        //
        // }
        // }
        //
        // assertTrue(foundColumn1Value);
        // assertTrue(foundColumn2Value);
        // }
        // }
        // catch(HibernateException e)
        // {
        // e.printStackTrace();
        // throw e;
        // }
    }

    @Test
    public void testReferenceConsistency() {
        // try
        // {
        // // ptd.transform(truck);
        // Query q = session.createQuery("from RecordTable");
        // List qlist = q.list();
        //
        // for(int i = 0;i < qlist.size();i++)
        // {
        // RecordTable thisRT = (RecordTable)qlist.get(i);
        //
        // for(RecordTableRecord rtr : thisRT.getRecords())
        // {
        // for(RecordTableValue rtv : rtr.getValues())
        // {
        // assertSame(rtv.getRecord(), rtr);
        // }
        // }
        //
        // for(RecordTableField rtf : thisRT.getFields())
        // {
        // for(RecordTableValue rtv : rtf.getValues())
        // {
        // assertSame(rtv.getField(), rtf);
        // }
        // }
        // }
        // }
        // catch(HibernateException e)
        // {
        // e.printStackTrace();
        // throw e;
        // }
    }
}
