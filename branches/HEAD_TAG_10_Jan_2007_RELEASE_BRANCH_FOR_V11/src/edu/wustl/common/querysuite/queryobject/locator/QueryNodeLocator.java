/**
 * 
 */
package edu.wustl.common.querysuite.queryobject.locator;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.wustl.common.querysuite.exceptions.MultipleRootsException;
import edu.wustl.common.querysuite.factory.QueryObjectFactory;
import edu.wustl.common.querysuite.queryobject.IConstraints;
import edu.wustl.common.querysuite.queryobject.IExpression;
import edu.wustl.common.querysuite.queryobject.IExpressionId;
import edu.wustl.common.querysuite.queryobject.IJoinGraph;
import edu.wustl.common.querysuite.queryobject.IQuery;


/**
 * To locate the DAG nodes, so that they will appear on the Dag As Sirected routed aCyclic graph.
 * @author prafull_kadam
 *
 */
public class QueryNodeLocator
{
	List<Integer> maxNodeAtLevel;
	List<List<IExpressionId>> visibleExpListLevelWize;
	
	private int maxX;
	private IConstraints constraints;
	private IJoinGraph graph;
	private Map<IExpressionId, Position> positionMap;
	private final static int X_OFFSET = 10;
	private final static int WIDTH_OF_NODE = 220;
	
	/**
	 * Constructor to instanciate the Object.
	 * @param maxX The Max X cordinate.
	 * @param maxY The Max Y coordinate.
	 * @param query The reference to the Query Object.
	 */
	public QueryNodeLocator(int maxX, IQuery query)
	{
		this.maxX = maxX; 
		constraints = query.getConstraints();
		graph = constraints.getJoinGraph();
		int noOfRoots = countNodeAtLevel();
		if (noOfRoots==1)
			createPositionMap();
//		for (int level =0;level < maxNodeAtLevel.size();level++)
//		{
//			List<IExpressionId> list = visibleExpListLevelWize.get(level);
//			System.out.println("Level:"+level+":"+list);
//		}
	}
	
	/**
	 * To get the Map of the visible nodes verses the x & y positions.
	 * @return Map of the visible nodes verses the x & y positions.
	 */
	public Map<IExpressionId, Position>  getPositionMap()
	{
		return positionMap;
	}
	/**
	 * This method will create position Map.
	 *
	 */
	private void createPositionMap()
	{
		positionMap = new HashMap<IExpressionId, Position>();
		int x = X_OFFSET;
		for (int level =0;level < maxNodeAtLevel.size();level++)
		{
			List<IExpressionId> list = visibleExpListLevelWize.get(level);
			int nodesAtThisLevel = list.size() ;
			int yDiff = (maxX)/(nodesAtThisLevel+1);
			int y = yDiff;
			for(IExpressionId expId:list)
			{
				positionMap.put(expId, new Position(x,y));
				y+=yDiff;
			}
			x+=WIDTH_OF_NODE;
		}
	}
	
	/**
	 * This method will count visible node at each level.
	 * @throws MultipleRootsException
	 */
	private int countNodeAtLevel()
	{
		List<IExpressionId> allRoots = graph.getAllRoots();
		maxNodeAtLevel = new ArrayList<Integer>();
		
		int size = allRoots.size();
		if (size==1)
		{
			visibleExpListLevelWize = new ArrayList<List<IExpressionId>>();
			maxNodeAtLevel.add(1);
			addToVisibleMap(allRoots.get(0),1);
			process(allRoots.get(0),1);
		}
		else
		{
			positionMap = new HashMap<IExpressionId, Position>();
			Enumeration<IExpressionId> expressionIds = constraints.getExpressionIds();
			int yIncrement = X_OFFSET*6;
			int y = yIncrement;
			int x = X_OFFSET;
			while (expressionIds.hasMoreElements())
			{
				IExpressionId expressionId = expressionIds.nextElement();
				IExpression expression = constraints.getExpression(expressionId);
				if (expression.isVisible())
				{
					positionMap.put(expressionId, new Position(x,y));
					x+=WIDTH_OF_NODE/2;
					y+=yIncrement;
				}
			}
		}
		return size;
	}
	
	/**
	 * To add the node in visible node list.
	 * @param expId expression Id
	 * @param level The level of the node in the Graph.
	 */
	private void addToVisibleMap(IExpressionId expId, int level)
	{
		List<IExpressionId> list=null;
		if (level <= visibleExpListLevelWize.size()-1)
		{
			list = visibleExpListLevelWize.get(level);
		}
		else
		{
			list = new ArrayList<IExpressionId>();
			visibleExpListLevelWize.add(list);
		}
		list.add(expId);
	}
	/**
	 * To Process the expression node with the given level.
	 * @param expId expression Id
	 * @param level The level of the node in the Graph.
	 */
	private void process(IExpressionId expId, int level)
	{
		
		List<IExpressionId> childrenList = graph.getChildrenList(expId);
		for (IExpressionId child: childrenList)
		{
			IExpression expression = constraints.getExpression(child);
			if (expression.isInView())
			{
				if (maxNodeAtLevel.size()<=level)
				{
					maxNodeAtLevel.add(1);
					addToVisibleMap(child,level+1);
				}
				else
				{
					maxNodeAtLevel.set(level,maxNodeAtLevel.get(level)+1);
					addToVisibleMap(child,level);
				}
				
				process(child,level+1);
			}
			else
			{
				process(child, level);
			}
				
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
//		IQuery query = QueryGeneratorMock.createSampleQuery4();
		IQuery query = QueryObjectFactory.createQuery();
		setViewForAll(query);
		IConstraints constraints2 = query.getConstraints();
		Map<IExpressionId, Position> positionMap2 = new QueryNodeLocator(500,query).getPositionMap();
		Set<IExpressionId> keySet = positionMap2.keySet();
		for (IExpressionId expId:keySet)
		{
			Position position = positionMap2.get(expId);
			String name = constraints2.getExpression(expId).getQueryEntity().getDynamicExtensionsEntity().getName();
			System.out.println(expId+"."+name+":"+position.getX()+","+position.getY());
		}
	}
	/**
	 * Method is added to testing purpose, which sets all expression as visible.
	 * @param query reference to the Query.
	 */
	private static void setViewForAll(IQuery query)
	{
		IConstraints constraints2 = query.getConstraints();
		Enumeration<IExpressionId> expressionIds = constraints2.getExpressionIds();
		while (expressionIds.hasMoreElements())
		{
			constraints2.getExpression(expressionIds.nextElement()).setVisible(true);
		}
	}
}
