package base.app.user;

public class UserAccount extends base.BaseObj {

	private static final String LOGTAG = UserAccount.class.getSimpleName();

    public String token = null;
    public long userId = 0;
    public boolean mIsLocked = false;
    
    public long getUserId() {
    	return this.userId;
    }

    public void clear() {
        this.token = null;
        this.userId = 0;
    }
    
    public boolean isLocked() {
    	return mIsLocked;
    }
}
