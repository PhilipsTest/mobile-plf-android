/*
 * (C) Koninklijke Philips N.V., 2015, 2016.
 * All rights reserved.
 */
package com.philips.cl.di.common.ssdp.controller;

import com.philips.cdp.dicommclient.util.DICommLog;
import com.philips.cl.di.common.ssdp.contants.ConnectionLibContants;
import com.philips.cl.di.common.ssdp.contants.XmlParserConstants;
import com.philips.cl.di.common.ssdp.models.SSDPdevice;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Parser for the base URL currently parser only for icons
 */
@Deprecated
public class BaseUrlParser {

	/**
	 * Parser that does the whole job
	 */
	class BaseUrlXmlHandler extends DefaultHandler {
		private int devicesCount = -1;
		private SSDPdevice mSsdpDevice;
		/**
		 * Method startDocument.
		 * 
		 * @throws SAXException
		 *             * @see org.xml.sax.ContentHandler#startDocument() * @see
		 *             org.xml.sax.ContentHandler#startDocument()
		 */
		private StringBuilder mStringBuilder;

		/**
		 * Method characters.
		 * 
		 * @param ch
		 *            char[]
		 * @param start
		 *            int
		 * @param length
		 *            int
		 *            sax.ContentHandler#characters(char[], int, int) * * @see
		 *            org.xml.sax.ContentHandler#characters(char[], int, int) * * @see
		 *            org.xml.sax.ContentHandler#characters(char[], int, int)
		 * @throws SAXException
		 * @see org.xml.sax.ContentHandler#characters(char[], int, int)
		 */
		@Override
		public void characters(final char[] ch, final int start, final int length)
				throws SAXException {
			super.characters(ch, start, length);
			mStringBuilder.append(ch, start, length);
		}

		/**
		 * Method endElement.
		 * 
		 * @param uri
		 *            String
		 * @param localName
		 *            String
		 * @param qName
		 *            String
		 *            entHandler#endElement(String, String, String) * @see
		 *            org.xml.sax.ContentHandler#endElement(String, String, String) * @see
		 *            org.xml.sax.ContentHandler#endElement(String, String, String)
		 * @throws SAXException
		 * @see org.xml.sax.ContentHandler#endElement(String, String, String)
		 */
		@Override
		public void endElement(final String uri, final String localName, final String qName)
				throws SAXException {
			// TODO: remove .replaceAll(REGEXP, ""); and make one regexp for the whole xmlToParse
			super.endElement(uri, localName, qName);
			if (devicesCount >= 0) {
					final String modifiedString = mStringBuilder.toString().replaceAll(XmlParserConstants.REGEXP, "");
					 if (isEqual(localName, XmlParserConstants.DEVICE_TYPE)) {
						mSsdpDevice.setDeviceType(modifiedString);
					} else if (isEqual(localName, XmlParserConstants.FRIENDLY_NAME)) {
						mSsdpDevice.setFriendlyName(modifiedString);
					} else if (isEqual(localName, XmlParserConstants.MANUFACTURER)) {
						mSsdpDevice.setManufacturer(modifiedString);
					} else if (isEqual(localName, XmlParserConstants.MODEL_NAME)) {
						mSsdpDevice.setModelName(modifiedString);
					 } else if (isEqual(localName, XmlParserConstants.MODEL_NUMBER)) {
						 mSsdpDevice.setModelNumber(modifiedString);
					} else if (isEqual(localName, XmlParserConstants.UDN)) {
						mSsdpDevice.setUdn(modifiedString);
					} else if (isEqual(localName, XmlParserConstants.CPP_ID)) {
						mSsdpDevice.setCppId(modifiedString);
					} else if (isEqual(localName, XmlParserConstants.DEVICE)) {
						devicesCount--;
						if (devicesCount >= 0) {
							mSsdpDevice = devicesList.get(devicesCount);
						} else if (isEqual(localName, XmlParserConstants.FRIENDLY_NAME)) {
							mSsdpDevice.setFriendlyName(modifiedString);
						}
					}
			}
			mStringBuilder.setLength(0);
		}

		private boolean isEqual(final String first, final String second) {
			boolean flag = false;
			if ((null != first) && first.equalsIgnoreCase(second)) {
				flag = true;
			}
			return flag;
		}

		/**
		 * Method startDocument.
		 * 
		 * @throws SAXException
		 *             * @see org.xml.sax.ContentHandler#startDocument()
		 */
		@Override
		public void startDocument()
				throws SAXException {
			super.startDocument();

			mStringBuilder = new StringBuilder(16);
			devicesList = new ArrayList<>();
		}

		/**
		 * Method startElement.
		 * 
		 * @param uri
		 *            String
		 * @param localName
		 *            String
		 * @param qName
		 *            String
		 * @param attributes
		 *            Attributes
		 *            String,
		 *            String, Attributes) * @see org.xml.sax.ContentHandler#startElement(String,
		 *            String, String,
		 *            Attributes) * @see org.xml.sax.ContentHandler#startElement(String, String,
		 *            String, Attributes)
		 * @throws SAXException
		 * @see org.xml.sax.ContentHandler#startElement(String, String, String, Attributes)
		 */
		@Override
		public void startElement(final String uri, final String localName, final String qName,
				final Attributes attributes)
						throws SAXException {
			super.startElement(uri, localName, qName, attributes);

			if (isEqual(qName, XmlParserConstants.DEVICE)) {
				mSsdpDevice = new SSDPdevice();
				devicesList.add(mSsdpDevice);
				devicesCount++;
			}
		}
	}

	private List<SSDPdevice> devicesList;

	/**
	 * Method getDevicesList.
	 * 
	 * @return List<SSDPdevice>
	 */
	public List<SSDPdevice> getDevicesList() {
		if (null != devicesList) {
			for (SSDPdevice ssdPdevice : devicesList) {
				DICommLog.i("SSDPdevice", "Device Information:  "
						+ "    getManufacturer()= " + ssdPdevice.getManufacturer()
						+ "    getManufacturerURL= " + ssdPdevice.getManufacturerURL()
						+ "    getModelDescription= " + ssdPdevice.getModelDescription()
						+ "    getModelName= " + ssdPdevice.getModelName()
						+ "    getModelNumber= " + ssdPdevice.getModelNumber()
						+ "    getPresentationURL= " + ssdPdevice.getPresentationURL()
						+ "    getUdn= " + ssdPdevice.getUdn()
						+ "    getUPC= " + ssdPdevice.getUPC()
						+ "    getCppId= " + ssdPdevice.getCppId());
			}
		}
		return devicesList;
	}

	/**
	 * Starts parsing of the string object pointed by xmlToParse
	 * Results of parsing are available in iconList
	 * 
	 * @param xmlToParse
	 *            String
	 */
	public void parse(final String xmlToParse) {
		if (xmlToParse != null) {
			InputStream inputStream = null;
			try {
				final SAXParserFactory factory = SAXParserFactory.newInstance();
				if (null != factory) {
					factory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
					factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
					final SAXParser parser = factory.newSAXParser();
					if (null != parser) {
						inputStream = new ByteArrayInputStream(xmlToParse.getBytes("UTF-8"));
						InputSource is = new InputSource(inputStream);
			    	    is.setEncoding("UTF-8");
						final BaseUrlXmlHandler baseUrlXmlHandler = new BaseUrlXmlHandler();
						parser.parse(is, baseUrlXmlHandler);
					}
				}
			} catch (final ParserConfigurationException e) {
                DICommLog.e(ConnectionLibContants.LOG_TAG, "ParserConfigurationException in parse device info ssdp" + e.getMessage());
			} catch (final SAXException e) {
                DICommLog.e(ConnectionLibContants.LOG_TAG, "SAXException in parse device info ssdp " + e.getMessage());
			} catch (final IOException e) {
                DICommLog.e(ConnectionLibContants.LOG_TAG, "IOException in parse device info ssdp" + e.getMessage());
			} finally {
				try {
					if (null != inputStream) {
						inputStream.close();
					}
				} catch (final IOException e) {
                    DICommLog.e(ConnectionLibContants.LOG_TAG, "IOException ssdp close input stream" + e.getMessage());
				}
			}
		}
	}
}
