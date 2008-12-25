package edu.wustl.common.audit;

/**
 * Mock class for test cases of methods in AuditManager (AuditManagerTestCase).
 * @author ravi_kumar
 *
 */
public class AuditableImpl implements Auditable
{

	private Long id;
	public AuditableImpl(long id)
	{
		this.id=id;
	}
	public Long getId()
	{
		return this.id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id)
	{
		this.id = id;
	}
}
