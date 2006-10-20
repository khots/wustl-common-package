
package edu.wustl.common.querysuite.queryobject.impl;

/**
 * @author Mandar Shidhore
 * @version 1.0
 * @created 19-Oct-2006 16.12.04 PM
 */

import junit.framework.TestCase;
import edu.wustl.common.querysuite.exceptions.IllegalArgumentException;
import edu.wustl.common.querysuite.queryobject.IExpression;
import edu.wustl.common.querysuite.queryobject.ILogicalConnector;
import edu.wustl.common.querysuite.queryobject.IRule;
import edu.wustl.common.querysuite.queryobject.LogicalOperator;
import edu.wustl.common.querysuite.queryobject.impl.Expression;
import edu.wustl.common.querysuite.queryobject.impl.LogicalConnector;
import edu.wustl.common.querysuite.queryobject.impl.Rule;

public class ExpressionTestCases extends TestCase
{
	IExpression expr;
	IExpression expr2;
	ILogicalConnector orCond;
	ILogicalConnector andCond;
	IRule a;
	IRule b;
	IRule c;
	IRule d;
	IRule e;

	public ExpressionTestCases()
	{
		super();
	}

	public ExpressionTestCases(String name)
	{
		super(name);
	}

	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception
	{
		expr = new Expression(null);
		a = new Rule();
		expr.addOperand(a);

		b = new Rule();
		orCond = new LogicalConnector(LogicalOperator.OR, 0);
		expr.addOperand(orCond, b);

		c = new Rule();
		andCond = new LogicalConnector(LogicalOperator.AND, 0);
		expr.addOperand(andCond, c);

		expr2 = new Expression(null);
		d = new Rule();
		expr2.addOperand(d);

		e = new Rule();
		orCond = new LogicalConnector(LogicalOperator.OR, 0);
		expr2.addOperand(orCond, e);
	}

	/**
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception
	{
		// TODO Auto-generated method stub
		super.tearDown();
	}

	/*
	 * Expression is (a or b) and c -- we try to remove operand b; 
	 * it should give us a and c as the final expression
	 */
	public void testremoveOperandCase1()
	{
		expr.addParantheses(0, 1);
		assertTrue(expr.removeOperand(b));

		try
		{
			assertEquals(andCond, expr.getLogicalConnector(0, 1));
		}
		catch (IllegalArgumentException e)
		{
			assertTrue(false);
		}
	}

	/*
	 * Expression is a or (b and c) -- we try to remove operand b; 
	 * it should give us a or c as the final expression
	 */
	public void testremoveOperandCase2()
	{
		expr.addParantheses(1, 2);
		assertTrue(expr.removeOperand(b));

		try
		{
			assertEquals(orCond, expr.getLogicalConnector(0, 1));
		}
		catch (IllegalArgumentException e)
		{
			assertTrue(false);
		}
	}

	/*
	 * Expression is (a or b and c) -- we try to remove operand b; 
	 * it should give us a or c as the final expression
	 */
	public void testremoveOperandCase3()
	{
		expr.addParantheses(0, 2);
		assertTrue(expr.removeOperand(b));

		try
		{
			assertEquals(orCond, expr.getLogicalConnector(0, 1));
		}
		catch (IllegalArgumentException e)
		{
			assertTrue(false);
		}
	}

	/*
	 * Expression is (a or b) and c -- we try to remove operand a; 
	 * it should give us b and c as the final expression
	 */
	public void testremoveOperandCase4()
	{
		expr.addParantheses(0, 1);
		assertTrue(expr.removeOperand(a));

		try
		{
			assertEquals(andCond, expr.getLogicalConnector(0, 1));
		}
		catch (IllegalArgumentException e)
		{
			assertTrue(false);
		}
	}

	/*
	 * Expression is (a or b) and c -- we try to remove operand c; 
	 * it should give us a or b as the final expression
	 */
	public void testremoveOperandCase5()
	{
		expr.addParantheses(0, 1);
		assertTrue(expr.removeOperand(c));

		try
		{
			assertEquals(orCond, expr.getLogicalConnector(0, 1));
		}
		catch (IllegalArgumentException e)
		{
			assertTrue(false);
		}
	}

	/*
	 * Expression is a or (b and c) -- we try to remove operand c; 
	 * it should give us a or b as the final expression
	 */
	public void testremoveOperandCase6()
	{
		expr.addParantheses(1, 2);
		assertTrue(expr.removeOperand(c));

		try
		{
			assertEquals(orCond, expr.getLogicalConnector(0, 1));
		}
		catch (IllegalArgumentException e)
		{
			assertTrue(false);
		}
	}

	/*
	 * Expression is a or (b and c) -- we try to remove operand a; 
	 * it should give us b and c as the final expression
	 */
	public void testremoveOperandCase7()
	{
		expr.addParantheses(1, 2);
		assertTrue(expr.removeOperand(a));

		try
		{
			assertEquals(andCond, expr.getLogicalConnector(0, 1));
		}
		catch (IllegalArgumentException e)
		{
			assertTrue(false);
		}
	}

	/*
	 * Expression is (a or b and c) -- we try to remove operand a; 
	 * it should give us b and c as the final expression
	 */
	public void testremoveOperandCase8()
	{
		expr.addParantheses(0, 2);
		assertTrue(expr.removeOperand(a));

		try
		{
			assertEquals(andCond, expr.getLogicalConnector(0, 1));
		}
		catch (IllegalArgumentException e)
		{
			assertTrue(false);
		}
	}

	/*
	 * Expression is (a or b and c) -- we try to remove operand c; 
	 * it should give us a or b as the final expression
	 */
	public void testremoveOperandCase9()
	{
		expr.addParantheses(0, 2);
		assertTrue(expr.removeOperand(c));

		try
		{
			assertEquals(orCond, expr.getLogicalConnector(0, 1));
		}
		catch (IllegalArgumentException e)
		{
			assertTrue(false);
		}
	}

	/*
	 * Expression is (d or e)-- we try to remove operand d; 
	 * it should give us e as the final expression
	 */
	public void testremoveOperandCase10()
	{
		expr2.addParantheses(0, 1);
		assertTrue(expr2.removeOperand(e));

		/*try
		 {
		 assertEquals(orCond, expr2.getLogicalConnector(0,1));
		 }
		 catch (IllegalArgumentException e)
		 {
		 assertTrue(false);
		 }*/
	}

	/*
	 * Expression is (a or b and c) -- we try to remove operand b and c; 
	 * it should give us a as the final expression
	 */
	public void testremoveOperandCase11()
	{
		expr.addParantheses(0, 2);
		assertTrue(expr.removeOperand(b));
		assertTrue(expr.removeOperand(c));

		/*try
		 {
		 assertEquals(orCond, expr.getLogicalConnector(0,1));
		 }
		 catch (IllegalArgumentException e)
		 {
		 assertTrue(false);
		 }*/
	}

	/*
	 * Expression is (a or b and c) -- we try to remove operand a and c; 
	 * it should give us b as the final expression
	 */
	public void testremoveOperandCase12()
	{
		expr.addParantheses(0, 2);
		assertTrue(expr.removeOperand(a));
		assertTrue(expr.removeOperand(c));

		/*try
		 {
		 assertEquals(orCond, expr.getLogicalConnector(0,1));
		 }
		 catch (IllegalArgumentException e)
		 {
		 assertTrue(false);
		 }*/
	}

	/*
	 * Expression is (a or b and c) and we try to remove operand a and b; 
	 * it should give us c as the final expression
	 */
	public void testremoveOperandCase13()
	{
		expr.addParantheses(0, 2);
		assertTrue(expr.removeOperand(a));
		assertTrue(expr.removeOperand(b));

		/*try
		 {
		 assertEquals(orCond, expr.getLogicalConnector(0,1));
		 }
		 catch (IllegalArgumentException e)
		 {
		 assertTrue(false);
		 }*/
	}

	/*
	 * Expression is (a or b and c) -- we try to remove operand a and b and c
	 */
	public void testremoveOperandCase14()
	{
		expr.addParantheses(0, 2);
		assertTrue(expr.removeOperand(a));
		assertTrue(expr.removeOperand(b));
		assertTrue(expr.removeOperand(c));

		/*try
		 {
		 assertEquals(orCond, expr.getLogicalConnector(0,1));
		 }
		 catch (IllegalArgumentException e)
		 {
		 assertTrue(false);
		 }*/
	}

	/*
	 * Expression is a or b and c -- we try to get the logical
	 * connector between 0 and 1 i.e. between a and b
	 */
	public void testgetLogicalConnectorCase1()
	{
		try
		{
			assertEquals(orCond, expr.getLogicalConnector(0, 1));
		}
		catch (IllegalArgumentException e)
		{
			assertFalse(true);
		}
	}

	/*
	 * Expression is a or b and c -- we try to get the logical
	 * connector between 1 and 2 i.e. between a and b
	 */
	public void testgetLogicalConnectorCase2()
	{
		try
		{
			assertEquals(andCond, expr.getLogicalConnector(1, 2));
		}
		catch (IllegalArgumentException e)
		{
			assertFalse(true);
		}
	}

	/*
	 * Expression is a or b and c -- we try to get the logical
	 * connector between 0 and 2 which is invalid
	 */
	public void testgetLogicalConnectorCase3()
	{
		try
		{
			expr.getLogicalConnector(0, 2);
			assertTrue(false);
		}
		catch (IllegalArgumentException e)
		{

		}
	}

}