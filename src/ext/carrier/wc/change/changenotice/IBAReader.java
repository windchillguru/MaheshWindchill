
/* bcwti
 *
 * Copyright (c) ITC Infotech India Ltd.(I3L). All Rights
 * Reserved.
 *
 * This software is the confidential and proprietary information of I3L.
 * You shall not disclose such confidential information and shall use it
 * only in accordance with the terms of the license agreement.
 *
 * Author  : Unknown
 * Approver: Unknown
 *
 * ecwti
 */

package ext.carrier.wc.change.changenotice;

import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.iba.definition.DefinitionLoader;
import wt.iba.definition.StringDefinition;
import wt.iba.definition.litedefinition.*;
import wt.iba.definition.service.IBADefinitionHelper;
import wt.iba.value.DefaultAttributeContainer;
import wt.iba.value.IBAHolder;
import wt.iba.value.IBAValueUtility;
import wt.iba.value.StringValue;
import wt.iba.value.litevalue.*;
import wt.iba.value.service.IBAValueHelper;
import wt.iba.value.service.LoadValue;
import wt.pds.StatementSpec;
import wt.query.ClassAttribute;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionHelper;
import wt.units.display.DefaultUnitRenderer;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 ** This Utility file is responsible for reading the IBA/softtypes values

 */
public class IBAReader
{

	Hashtable ibaContainer;
	// Constructor initializing the Hashtable
	public IBAReader()
	{
		ibaContainer = new Hashtable();
	}



	/*
	 * This constructor method sets the part
    @param IBAHolder ibaholder
	 */
	public IBAReader(IBAHolder ibaholder)
	{
		// Added by Upgrade Team to avoid workflow Rollback issue
				/*try{ 
 ext.carrier.wc.workflow.CarrierWorkflowHelper.avoidTransactionRollback(wt.pom.Transaction.getCurrentTransaction()); 
 }catch(wt.util.WTException e){ 
 e.printStackTrace(System.err);
}*/

		setPart(ibaholder);
	}

	static Class _mthclass$(String s)
	{
		try
		{
			return Class.forName(s);
		}
		catch(ClassNotFoundException classnotfoundexception)
		{
			throw new NoClassDefFoundError(classnotfoundexception.getMessage());
		}
	}

	private AttributeDefDefaultView getAttributeDefinition(String s)
	{
		AttributeDefDefaultView attributedefdefaultview = null;
		try
		{
			attributedefdefaultview = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath(s);
			if(attributedefdefaultview == null)
			{
				AbstractAttributeDefinizerView abstractattributedefinizerview = DefinitionLoader.getAttributeDefinition(s);
				if(abstractattributedefinizerview != null)
					attributedefdefaultview = IBADefinitionHelper.service.getAttributeDefDefaultView((AttributeDefNodeView)abstractattributedefinizerview);
			}
		}
		catch(Exception exception)
		{
			exception.printStackTrace();
		}
		return attributedefdefaultview;
	}

	/*
	 * This method calls the displayvalue to display and if null sets result to blank
    @param String s
    @return String
    @throws IncompatibleUnitsException, UnitFormatException
	 */
	public String getDisplayIbaValue(String s)throws wt.units.IncompatibleUnitsException, wt.units.UnitFormatException
	{
		String result = getDisplayValue(s);
		if ( result == null)
		{
			result = " ";

		}
		return result;
	}

	/*
	 * This method get the value of softype to display
   @param String s
   @return String
   @throws IncompatibleUnitsException, UnitFormatException
	 */

	public String getDisplayValue(String s)throws wt.units.IncompatibleUnitsException, wt.units.UnitFormatException
	{


		String value = null;
		if(ibaContainer.get(s) == null )
		{
			return value;
		}

		AbstractValueView abstractValueView = (AbstractValueView)((Object[])ibaContainer.get(s))[1];


		if(abstractValueView == null)
			return value;

		if(abstractValueView instanceof StringValueDefaultView)
		{
			value = ((StringValueDefaultView)abstractValueView).getValue();
		}
		else if(abstractValueView instanceof BooleanValueDefaultView)
		{
			value = ((BooleanValueDefaultView)abstractValueView).getValueAsString();
		}
		else if(abstractValueView instanceof IntegerValueDefaultView)
		{
			value = Long.toString(((IntegerValueDefaultView)abstractValueView).getValue());
		}
		else if(abstractValueView instanceof FloatValueDefaultView)
		{
			value = Double.toString(((FloatValueDefaultView)abstractValueView).getValue());
			value = value.replace('.',',');


		}
		else if(abstractValueView instanceof RatioValueDefaultView)
		{
			value = Double.toString(((RatioValueDefaultView)abstractValueView).getValue());
		}
		else if(abstractValueView instanceof TimestampValueDefaultView)
		{
			value = ((TimestampValueDefaultView)abstractValueView).getValue().toString();
		}
		else if(abstractValueView instanceof URLValueDefaultView)
		{
			value = ((URLValueDefaultView)abstractValueView).getValue();
		}
		else if(abstractValueView instanceof UnitValueDefaultView)
		{
			// Traitement reel avec unites.
			DefaultUnitRenderer unitRenderer = new DefaultUnitRenderer();
			try
			{
				unitRenderer.setDisplayDigits(3);
			}
			catch (WTPropertyVetoException wpe)
			{
				System.out.println("Exception in Renderer: "+wpe);
			}
			String currentMeasurementSystem = DefaultUnitRenderer.getDisplayUnitsPref();
			value = unitRenderer.renderValue(((UnitValueDefaultView)abstractValueView).toUnit(),((UnitValueDefaultView)abstractValueView).getUnitDisplayInfo(currentMeasurementSystem));
			value = value.replace('.',',');
		}

		return value;


	}
	public String getValue(String s)
	{
		try
		{
			return getValue(s, SessionHelper.manager.getLocale());
		}
		catch(Exception exception)
		{
			exception.printStackTrace();
		}
		return null;
	}

	// gets the abstract view from the iba container
	public AbstractValueView getAbstractValueView(String s)
	{
		try
		{
			if(ibaContainer.get(s) == null )
			{
				return null;
			}
			return (AbstractValueView)((Object[])ibaContainer.get(s))[1];
		}catch(Exception exception)
		{
			exception.printStackTrace();
		}
		return null;
	}

	// calls the Abstractvalueview to get the values of IBA
	public String getValue(String s, Locale locale)
	{
		try
		{
			if(ibaContainer.get(s) == null )
			{
				return null;
			}
			AbstractValueView abstractvalueview = (AbstractValueView)((Object[])ibaContainer.get(s))[1];
			return IBAValueUtility.getLocalizedIBAValueDisplayString(abstractvalueview, locale);
		}
		catch(Exception exception)
		{
			exception.printStackTrace();
		}
		return null;
	}

	private AbstractValueView internalCreateValue(AbstractAttributeDefinizerView abstractattributedefinizerview, String s)
	{
		AbstractValueView abstractvalueview = null;
		if(abstractattributedefinizerview instanceof FloatDefView)
			abstractvalueview = LoadValue.newFloatValue(abstractattributedefinizerview, s, null);
		else
			if(abstractattributedefinizerview instanceof StringDefView)
				abstractvalueview = LoadValue.newStringValue(abstractattributedefinizerview, s);
			else
				if(abstractattributedefinizerview instanceof IntegerDefView)
					abstractvalueview = LoadValue.newIntegerValue(abstractattributedefinizerview, s);
				else
					if(abstractattributedefinizerview instanceof RatioDefView)
						abstractvalueview = LoadValue.newRatioValue(abstractattributedefinizerview, s, null);
					else
						if(abstractattributedefinizerview instanceof TimestampDefView)
							abstractvalueview = LoadValue.newTimestampValue(abstractattributedefinizerview, s);
						else
							if(abstractattributedefinizerview instanceof BooleanDefView)
								abstractvalueview = LoadValue.newBooleanValue(abstractattributedefinizerview, s);
							else
								if(abstractattributedefinizerview instanceof URLDefView)
									abstractvalueview = LoadValue.newURLValue(abstractattributedefinizerview, s, null);
								else
									if(abstractattributedefinizerview instanceof ReferenceDefView)
										abstractvalueview = LoadValue.newReferenceValue(abstractattributedefinizerview, s, null);
									else
										if(abstractattributedefinizerview instanceof UnitDefView)
											abstractvalueview = LoadValue.newUnitValue(abstractattributedefinizerview, s, null);
		return abstractvalueview;
	}

	/*
	 * Thismethod sets the part as the iBAHolder
    @param IBAHolder ibaholder
	 */
	public void setPart(IBAHolder ibaholder)
	{
		ibaContainer = new Hashtable();
		try
		{
			ibaholder = IBAValueHelper.service.refreshAttributeContainer(ibaholder, null, SessionHelper.manager.getLocale(), null);
			DefaultAttributeContainer defaultattributecontainer = (DefaultAttributeContainer)ibaholder.getAttributeContainer();
			if(defaultattributecontainer != null)
			{
				AttributeDefDefaultView aattributedefdefaultview[] = defaultattributecontainer.getAttributeDefinitions();
				for(int i = 0; i < aattributedefdefaultview.length; i++)
				{
					AbstractValueView aabstractvalueview[] = defaultattributecontainer.getAttributeValues(aattributedefdefaultview[i]);
					if(aabstractvalueview != null)
					{
						Object aobj[] = new Object[2];
						aobj[0] = aattributedefdefaultview[i];
						aobj[1] = aabstractvalueview[0];
						ibaContainer.put(aattributedefdefaultview[i].getName(), ((Object) (aobj)));
					}
				}

			}
		}
		catch(Exception exception)
		{
			exception.printStackTrace();
		}
	}

	public void setValue(String s, String s1)
	throws WTPropertyVetoException
	{
		//AbstractValueView abstractvalueview = (AbstractValueView)((Object[])ibaContainer.get(s))[1];
		AbstractValueView abstractvalueview = null;
		AttributeDefDefaultView attributedefdefaultview = null;
		Object aobj1[] = (Object[])ibaContainer.get(s);
		if(aobj1 != null)
		{
			abstractvalueview = (AbstractValueView)aobj1[1];
			attributedefdefaultview = (AttributeDefDefaultView)aobj1[0];
		}

		//AttributeDefDefaultView attributedefdefaultview = (AttributeDefDefaultView)((Object[])ibaContainer.get(s))[0];
		if(abstractvalueview == null)
			attributedefdefaultview = getAttributeDefinition(s);
		if(attributedefdefaultview == null)
		{
			System.out.println("IBA definition is null ...");
			return;
		}
		abstractvalueview = internalCreateValue(attributedefdefaultview, s1);
		if(abstractvalueview == null)
		{
			System.out.println("after creation, iba value is null ..");
			//return;
		} else
		{
			abstractvalueview.setState(1);
			Object aobj[] = new Object[2];
			aobj[0] = attributedefdefaultview;
			aobj[1] = abstractvalueview;
			ibaContainer.put(attributedefdefaultview.getName(), ((Object) (aobj)));
			//return;
		}
	}

	// Overrides the string method in java.lang.Object
	public String toString()
	{
		StringBuffer stringbuffer = new StringBuffer();
		Enumeration enumeration = ibaContainer.keys();
		try
		{
			while(enumeration.hasMoreElements())
			{
				String s = (String)enumeration.nextElement();
				AbstractValueView abstractvalueview = (AbstractValueView)((Object[])ibaContainer.get(s))[1];
				stringbuffer.append(s);
				stringbuffer.append(" - ");
				stringbuffer.append(IBAValueUtility.getLocalizedIBAValueDisplayString(abstractvalueview, SessionHelper.manager.getLocale()));
				stringbuffer.append('\n');
			}
		}
		catch(Exception exception)
		{
			exception.printStackTrace();
		}
		return stringbuffer.toString();
	}

	/*
	 * This Method updates the part as per the value supplied
    @param IBAHolder ibaholder
    @return IBAHolder
    @throws Exception
	 */
	public IBAHolder updatePart(IBAHolder ibaholder)
	throws Exception
	{
		ibaholder = IBAValueHelper.service.refreshAttributeContainer(ibaholder, null, SessionHelper.manager.getLocale(), null);
		DefaultAttributeContainer defaultattributecontainer = (DefaultAttributeContainer)ibaholder.getAttributeContainer();
		for(Enumeration enumeration = ibaContainer.elements(); enumeration.hasMoreElements();)
			try
		{
				Object aobj[] = (Object[])enumeration.nextElement();
				AbstractValueView abstractvalueview = (AbstractValueView)aobj[1];
				AttributeDefDefaultView attributedefdefaultview = (AttributeDefDefaultView)aobj[0];
				if(abstractvalueview.getState() == 1)
				{
					defaultattributecontainer.deleteAttributeValues(attributedefdefaultview);
					abstractvalueview.setState(3);

					defaultattributecontainer.addAttributeValue(abstractvalueview);
					/*START Carrier Code ADDITION - Jan 5, 2009*/
					//code added below to support multi values for an attribute.
					int intArrSize = aobj.length; //must be at least 3 to go to following loop
					for ( int i =2; i < intArrSize ; i++)
					{
						abstractvalueview = (AbstractValueView) aobj[i];
						defaultattributecontainer.addAttributeValue(abstractvalueview);
					}
					/*END Carrier Code ADDITION - Jan 5, 2009*/
				}
		}
		catch(Exception exception)
		{
			exception.printStackTrace();
		}

		ibaholder.setAttributeContainer(defaultattributecontainer);
		return ibaholder;
	}

	/**
	 * Retrieve list of StringValue objects for given IBAHolder. Use for Multi Valued String IBA(All values will
	 * be retrieved)
	 * 
	 * @param attName
	 *           Name of IBA
	 * @param holder
	 * @return List of String Values types
	 * @throws wt.util.WTException
	 */
	public static List getStringIBAValues(String attName, IBAHolder holder)
	throws WTException {

		QuerySpec qs = new QuerySpec();
		int vIdx = qs.appendClassList(StringValue.class, true);
		int dIdx = qs.appendClassList(StringDefinition.class, false);

		qs.appendWhere(new SearchCondition(StringValue.class,"theIBAHolderReference.key.id", "=", ((Persistable) holder).getPersistInfo().getObjectIdentifier().getId()),new int[] {vIdx});
		qs.appendAnd();
		qs.appendWhere(new SearchCondition(StringDefinition.class,StringDefinition.NAME, "=", attName), new int[] {dIdx});
		qs.appendAnd();
		SearchCondition sc = new SearchCondition(new ClassAttribute(StringValue.class, "definitionReference.key.id"), "=", new ClassAttribute(StringDefinition.class,"thePersistInfo.theObjectIdentifier.id"));
		sc.setFromIndicies(new int[] {vIdx, dIdx}, 0);
		qs.appendWhere(sc, new int[] {vIdx, dIdx});
		QueryResult qr = PersistenceHelper.manager.find((StatementSpec)qs);

		List values = new ArrayList();
		while (qr.hasMoreElements()) {
			values.add(((StringValue)((Object[]) qr.nextElement())[0]).getValue());
		}
		return values;
	}

	/** 
	 * This method has been copied from ext.common.load.util.IBAUtil class
	 * Pass in a name if you want to set the value of an existing attribute.
	 * to create a new iba def you must pass in a full iba path value.
	 * @param name Name of the attribute you would like to change the value of
	 * @param value Arraylist of Values for that soft attribute.
	 * @throws WTPropertyVetoException Thrown if an invalid value is used for the named soft attribute
	 */
	public void setValue(String name, ArrayList arrlstValue) throws WTPropertyVetoException, WTException {
		try
		{
			//added below 1 line
			String value = (String)arrlstValue.get(0);

			AbstractValueView ibaValue = null;
			AttributeDefDefaultView theDef = null;
			Object[] obj = (Object[]) ibaContainer.get(name);
			if (obj != null) {
				ibaValue = (AbstractValueView) obj[1]; 
				theDef = (AttributeDefDefaultView) obj[0];
			}
			if ( ibaValue == null ) { 
				System.out.println("IBA Value is null.");
			}
			if ( ibaValue == null )		
				theDef = getAttributeDefinition(name);
			if ( theDef == null ) {
				System.out.println("definition is null ...");
				return;
			}

			ibaValue = internalCreateValue(theDef, value);
			if ( ibaValue == null )  {
				System.out.println("after creation, iba value is null ..");
				return;
			}

			ibaValue.setState(AbstractValueView.CHANGED_STATE);

			/*
	            Object[] temp = new Object[2];
	            temp[0] = theDef;
	            temp[1] = ibaValue;
			 */
			//Above 3 lines of code changed to below (upto the for loop ending)
			// Mutli value attribute code added below

			int intArrlstSize = arrlstValue.size() ;
			Object[] temp = new Object[intArrlstSize+1];
			temp[0] = theDef;
			temp[1] = ibaValue;

			System.out.println("size of input arraylist .."+intArrlstSize);

			for ( int i =1; i < intArrlstSize ; i++)
			{
				AbstractValueView ibaValue2 = internalCreateValue(theDef,(String)arrlstValue.get(i));
				temp[i+1] = ibaValue2;
			}

			//Multi value attrib code addition over.

			ibaContainer.put(theDef.getName(), temp);	            
		}
		catch ( Exception e)
		{
			System.out.println("Exception in NEW setvalue method in IBAReader.java   " + e);
			throw new WTException(e);
		}
	}
}