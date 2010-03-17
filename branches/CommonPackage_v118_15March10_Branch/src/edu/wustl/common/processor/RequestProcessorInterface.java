package edu.wustl.common.processor;

import edu.wustl.common.exception.ApplicationException;


/**
 * @author vishvesh_mulay
 *
 * @param <T>
 * @param <S>
 */
public interface RequestProcessorInterface<T, S>
{

    /**
     * Process.
     *
     * @param inputDataCarrier the data carrier
     * @param outputDataCarrier the output data carrier
     * @throws ApplicationException
     * @throws Exception
     *
     */
    void process(T inputDataCarrier, S outputDataCarrier) throws ApplicationException;

}
