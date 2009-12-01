package edu.wustl.common.audit;


/**
 * Mock class for test cases of methods in AuditManager (AuditManagerTestCase).
 * @author ravi_kumar
 *
 */
public class AuditableImpl implements Auditable
{
	/**
	 * Id.
	 */
	private Long id;
	/**
	 * Constructor.
	 * @param id id to set.
	 */
	public AuditableImpl(long id)
	{
		this.id=id;
	}
	/**
	 * @return id.
	 */
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
