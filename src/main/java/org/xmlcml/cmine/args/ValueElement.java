package org.xmlcml.cmine.args;

import java.util.ArrayList;
import java.util.List;

import nu.xom.Element;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.xml.XMLUtil;

/**
 * Method of parameterising arg's in args.xml.
 * 
 *  child of <arg>.
 * 
 * @author pm286
 *
 */
public class ValueElement extends Element {

	private static final Logger LOG = Logger.getLogger(ValueElement.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public static final String TAG = "value";
	
	public static final String CLASS_NAME_ATT = "className";
	public static final String FIELDS_ATT = "fields";
	public static final String NAME_ATT   = "name";
	public static final String URL_ATT    = "url";
	public static final String TEXT_CHILD = "#text";
	
	public static final List<String> ATT_NAMES;
	public static final List<String> CHILD_NAMES;
	static {
		ATT_NAMES = new ArrayList<String>();
		ATT_NAMES.add(CLASS_NAME_ATT);
		ATT_NAMES.add(FIELDS_ATT);
		ATT_NAMES.add(NAME_ATT);
		ATT_NAMES.add(URL_ATT);
		
		CHILD_NAMES = new ArrayList<String>();
		CHILD_NAMES.add(TEXT_CHILD);
	};
	

	public ValueElement() {
		super(TAG);
	}
	
	public static ValueElement createValueElement(Element element) {
		ValueElement valueElement = null;
		if (element != null && TAG.equals(element.getLocalName())) {
			valueElement = new ValueElement();
			XMLUtil.copyAttributes(element, valueElement);
			XMLUtil.transferChildren(element, valueElement);
			XMLUtil.checkAttributeNames(valueElement, ATT_NAMES);
			XMLUtil.checkChildElementNames(valueElement, CHILD_NAMES);
		}
		return valueElement;
	}

	public String getName() {
		return this.getAttributeValue(NAME_ATT);
	}

	public String getClassName() {
		return this.getAttributeValue(CLASS_NAME_ATT);
	}
}