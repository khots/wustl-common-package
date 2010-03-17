/**
 *
 */
package edu.wustl.common.processor;

import edu.wustl.common.processor.datacarrier.InputDataInterface;
import edu.wustl.common.processor.datacarrier.OutputDataInterface;

/**
 * @author vishvesh_mulay
 *
 */
public abstract class BaseRequestProcessor<T extends InputDataInterface, S extends OutputDataInterface> implements
RequestProcessorInterface<T, S>
{

}
