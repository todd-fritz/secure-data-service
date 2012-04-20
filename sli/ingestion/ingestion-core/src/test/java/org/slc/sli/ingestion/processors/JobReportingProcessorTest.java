package org.slc.sli.ingestion.processors;

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultExchange;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.ingestion.BatchJobStageType;
import org.slc.sli.ingestion.FaultType;
import org.slc.sli.ingestion.FileFormat;
import org.slc.sli.ingestion.FileType;
import org.slc.sli.ingestion.landingzone.LocalFileSystemLandingZone;
import org.slc.sli.ingestion.model.Error;
import org.slc.sli.ingestion.model.Metrics;
import org.slc.sli.ingestion.model.NewBatchJob;
import org.slc.sli.ingestion.model.ResourceEntry;
import org.slc.sli.ingestion.model.Stage;
import org.slc.sli.ingestion.model.da.BatchJobDAO;
import org.slc.sli.ingestion.model.da.BatchJobMongoDA;
import org.slc.sli.ingestion.model.da.BatchJobMongoDAStatus;

/**
*
* @author bsuzuki
*
*/
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class JobReportingProcessorTest {

    private static final String TEMP_DIR = "tmp/";
    private static final String OUTFILE = "job-test.ctl-11111111111111111.log";

    private static final String BATCHJOBID = "test.ctl-11111111111111111";
    private static final String RESOURCEID = "InterchangeStudentParent.xml";
    private static final int RECORDS_CONSIDERED = 50;
    private static final int RECORDS_FAILED = 5;
    private static final int RECORDS_PASSED = RECORDS_CONSIDERED - RECORDS_FAILED;
    private static final String RECORDID = "recordIdentifier";
    private static final String ERRORDETAIL = "errorDetail";

    File tmpDir = new File(TEMP_DIR);

    @Before
    public void setUp() throws Exception {
        if (tmpDir.mkdirs()) {
            System.out.println("Created temp directory " + tmpDir.getAbsolutePath());
        }
    }

    @After
    public void tearDown() throws Exception {
        if (tmpDir.exists()) {
            String files[] = tmpDir.list();

            for (String temp : files) {
               //construct the file structure
               File fileToDelete = new File(tmpDir, temp);
               fileToDelete.delete();
            }
        }
        tmpDir.delete();
    }

    @Test
    public void testProcess() throws Exception {

        // create fake mocked method return objects
        List<ResourceEntry> mockedResourceEntries = createFakeResourceEntries();
        List<Stage> mockedStages  = createFakeStages();
        Map<String, String> mockedProperties = createFakeBatchProperties();
        NewBatchJob mockedJob = new NewBatchJob(BATCHJOBID, "192.168.59.11", "finished", 1, mockedProperties, mockedStages, mockedResourceEntries);
        mockedJob.setSourceId(TEMP_DIR);

        BatchJobMongoDAStatus mockedFindErrorsStatus = createFakeStatus();

        // set mocked BatchJobMongoDA in jobReportingProcessor
        BatchJobDAO mockedBatchJobDAO = Mockito.mock(BatchJobMongoDA.class);
        Mockito.when(mockedBatchJobDAO.findBatchJobById(Matchers.eq(BATCHJOBID))).thenReturn(mockedJob);
        Mockito.when(mockedBatchJobDAO.findBatchJobErrors(Matchers.eq(BATCHJOBID))).thenReturn(mockedFindErrorsStatus);
        Mockito.when(mockedBatchJobDAO.saveBatchJob(mockedJob)).thenReturn(null);

        // create exchange
        Exchange exchange = new DefaultExchange(new DefaultCamelContext());
        exchange.getIn().setHeader("BatchJobId", BATCHJOBID);

        // create the fake landing zone
        LocalFileSystemLandingZone tmpLz = new LocalFileSystemLandingZone();
        tmpLz.setDirectory(new File(TEMP_DIR));

        System.out.println("Writing to " + tmpLz.getDirectory().getAbsolutePath());

        JobReportingProcessor jobReportingProcessor = new JobReportingProcessor(tmpLz);
        jobReportingProcessor.setBatchJobDAO(mockedBatchJobDAO);

        jobReportingProcessor.process(exchange);

        // read the generated file and check values
        FileReader fr = new FileReader(TEMP_DIR + OUTFILE);
        BufferedReader br = new BufferedReader(fr);
        String s;

//        while ((s = br.readLine()) != null) {
//            System.out.println(s);
//        }

        assertTrue(br.readLine().contains("jobId: " + BATCHJOBID));
        assertTrue(br.readLine().contains("[file] " + RESOURCEID + " (" + FileFormat.EDFI_XML.getCode() + "/" + FileType.XML_STUDENT.getName() + ")"));
        assertTrue(br.readLine().contains("[file] " + RESOURCEID + " records considered: " + RECORDS_CONSIDERED));
        assertTrue(br.readLine().contains("[file] " + RESOURCEID + " records ingested successfully: " + RECORDS_PASSED));
        assertTrue(br.readLine().contains("[file] " + RESOURCEID + " records failed: " + RECORDS_FAILED));
        assertTrue(br.readLine().contains("[configProperty] purge: false"));
        assertTrue(br.readLine().contains("ERROR  " + BatchJobStageType.PERSISTENCE_PROCESSOR.getName() + "," + RESOURCEID + "," + RECORDID + "," + ERRORDETAIL));
        assertTrue(br.readLine().contains("Not all records were processed completely due to errors."));
        assertTrue(br.readLine().contains("Processed "+ RECORDS_CONSIDERED + " records."));

        fr.close();
    }

    private Map<String, String> createFakeBatchProperties() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("purge", "false");
        return map;
    }

    private List<ResourceEntry> createFakeResourceEntries() {
        List<ResourceEntry> resourceEntries = new LinkedList<ResourceEntry>();
        ResourceEntry re = new ResourceEntry();
        re.setResourceId(RESOURCEID);
        re.setExternallyUploadedResourceId(RESOURCEID);
        re.setResourceName(TEMP_DIR + RESOURCEID);
        re.update(FileFormat.EDFI_XML.getCode(), FileType.XML_STUDENT.getName(), "123456789", RECORDS_CONSIDERED, RECORDS_FAILED);
        resourceEntries.add(re);
        return resourceEntries;
    }

    private BatchJobMongoDAStatus createFakeStatus() {
        List<Error> errors = new LinkedList<Error>();
        Error error = new Error(BATCHJOBID, BatchJobStageType.PERSISTENCE_PROCESSOR.getName(),
                RESOURCEID, "10.81.1.27", "testhost", RECORDID, "20120412 10:04:46.111",
                FaultType.TYPE_ERROR.getName(), "errorType", ERRORDETAIL);
        errors.add(error);
        return new BatchJobMongoDAStatus(true, "Got fake errors for testing", errors);
    }

    private List<Stage> createFakeStages() {
        Metrics m = new Metrics(RESOURCEID, "192.168.59.11", "transform1.slidev.org",
                "20120412 10:04:13.463", "20120412 10:04:45.778", 50, 5);
        List<Metrics> ms = new LinkedList<Metrics>();
        ms.add(m);
        Stage s = new Stage(BatchJobStageType.PERSISTENCE_PROCESSOR.getName(), "fininshed", "20120412 10:04:13.463", "20120412 10:04:45.778", ms);

        List<Stage> listStages = new LinkedList<Stage>();
        listStages.add(s);
        return listStages;
    }

}
