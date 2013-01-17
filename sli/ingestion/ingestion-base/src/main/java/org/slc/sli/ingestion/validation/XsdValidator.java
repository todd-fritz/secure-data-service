/*
 * Copyright 2012 Shared Learning Collaborative, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.slc.sli.ingestion.validation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.reporting.AbstractMessageReport;
import org.slc.sli.ingestion.reporting.ReportStats;
import org.slc.sli.ingestion.reporting.Source;
import org.slc.sli.ingestion.reporting.impl.BaseMessageCode;
import org.slc.sli.ingestion.reporting.impl.NeutralRecordSource;
import org.slc.sli.ingestion.reporting.impl.XmlFileSource;

/**
 * Validates the xml file against an xsd. Returns false if there is any error else it will always
 * return true. The error messages would be reported by the error handler.
 *
 * @author ablum
 *
 */
@Scope("prototype")
@Component
public class XsdValidator implements Validator<IngestionFileEntry> {

    private static final Logger LOG = LoggerFactory.getLogger(XsdValidator.class);
    private static final String STAGE_NAME = "XSD Validation";

    private Map<String, Resource> xsd;

    @Override
    public boolean isValid(IngestionFileEntry entry, AbstractMessageReport report,
            ReportStats reportStats, Source source) {

        // we know more of our source
        Source newsource = new XmlFileSource(entry.getFileName(),
                (source == null ? null : source.getStageName()));
        InputStream is = null;
        try {
            is = entry.getFileStream();

            return validateXmlFile(entry, is, report, reportStats);
        } catch (FileNotFoundException e) {
            LOG.error("File not found: " + entry.getFileName(), e);
            report.error(reportStats, newsource, BaseMessageCode.BASE_0023, entry.getFileName());
        } catch (IOException e) {
            LOG.error("Problem reading file: " + entry.getFileName(), e);
            report.error(reportStats, newsource, BaseMessageCode.BASE_0024, entry.getFileName());
        } catch (SAXException e) {
            LOG.error("SAXException");
        } catch (Exception e) {
            LOG.error("Error processing file " + entry.getFileName(), e);
        } finally {
            IOUtils.closeQuietly(is);
        }

        return false;
    }

    private boolean validateXmlFile(IngestionFileEntry entry, InputStream is, AbstractMessageReport report,
            ReportStats reportStats) throws SAXException, IOException {

        if (!entry.isValid()) {
            throw new FileNotFoundException();
        }

        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Resource xsdResource = xsd.get(entry.getFileType().getName());
        Schema schema = schemaFactory.newSchema(xsdResource.getURL());

        javax.xml.validation.Validator validator = schema.newValidator();
        validator.setResourceResolver(new ExternalEntityResolver());
        validator.setErrorHandler(new XsdErrorHandler(report, reportStats));

        javax.xml.transform.Source sc = new StreamSource(is, entry.getFileName());
        validator.validate(sc);

        return true;
    }

    @Override
    public String getStageName() {
        return STAGE_NAME;
    }

    public Map<String, Resource> getXsd() {
        return xsd;
    }

    public void setXsd(Map<String, Resource> xsd) {
        this.xsd = xsd;
    }

    /**
     * Throws a runtime exception which is caught by the XsdValidatior
     * if a user attempts to pass external entities in their XML.
     *
     * @author dshaw
     *
     */
    static final class ExternalEntityResolver implements LSResourceResolver {
        @Override
        public LSInput resolveResource(String type, String namespaceURI, String publicId, String systemId,
                String baseURI) {
            throw new RuntimeException("Attempted disallowed ingestion of External XML Entity (XXE).");
        }
    }

    /**
     * XsdErrorHandler
     *
     */
    static final class XsdErrorHandler implements org.xml.sax.ErrorHandler {

        private final AbstractMessageReport report;
        private final ReportStats reportStats;

        public XsdErrorHandler(AbstractMessageReport report, ReportStats reportStats) {
            this.report = report;
            this.reportStats = reportStats;
        }

        @Override
        public void warning(SAXParseException ex) {

            reportWarning(ex);
        }

        @Override
        public void error(SAXParseException ex) {

            reportWarning(ex);
        }

        @Override
        public void fatalError(SAXParseException ex) throws SAXException {

            reportWarning(ex);

            throw ex;
        }

        /**
         * Incorporate the SAX error message into an ingestion error message.
         *
         * @return Error message returned by Ingestion
         */
        private void reportWarning(SAXParseException ex) {
            if (report != null) {
                String fullParsefilePathname = (ex.getSystemId() == null) ? "" : ex.getSystemId();
                File parseFile = new File(fullParsefilePathname);

                Source source = new NeutralRecordSource(parseFile.getName(), STAGE_NAME, ex.getLineNumber(),
                        ex.getColumnNumber());

                report.warning(reportStats, source, BaseMessageCode.BASE_0017, parseFile.getName(), ex.getMessage());
            }
        }
    }

}
