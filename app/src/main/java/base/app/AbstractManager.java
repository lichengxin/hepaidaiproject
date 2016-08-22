package base.app;

public class AbstractManager extends base.BaseObj {

	protected ManagerFactory mCore = null;
	public AbstractManager(ManagerFactory core)
	{
		mCore = core;
	}	
}
