
package edu.wustl.common.interfaces;

import java.util.List;

/**
 * This contains methods related to shopping cart.
 * @author deepti_shelar
 *
 */

public interface ShoppingCartInterface
{

	/**
	 * Returns the list of entities which are orderable
	 * @return List of string entity names
	 */
	List<String> getOrderableEntityList();
}
